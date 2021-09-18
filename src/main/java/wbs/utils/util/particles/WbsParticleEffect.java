package wbs.utils.util.particles;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import wbs.utils.WbsUtils;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.WbsMath;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * Represents a particle effect in a specific pattern
 */
public abstract class WbsParticleEffect {
	protected static Vector upVector = new Vector(0, 1, 0);

	public enum WbsParticleType { CUBOID, DISC, ELECTRIC, LINE, NORMAL, RING, SPHERE, SPIRAL }

	/**
	 * Build a particle effect from a config, and output errors to the settings field
	 * @param section The configuration section to build from
	 * @param settings The settings to log errors to
	 * @param directory The path to the section within the parent section for logging purpsoes
	 */
	public static WbsParticleEffect buildParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		WbsConfigReader.requireNotNull(section, "type", settings, directory);
		String typeString = section.getString("type");
		WbsParticleType type = WbsEnums.getEnumFromString(WbsParticleType.class, typeString);

		if (type == null) {
			settings.logError("Invalid type: " + typeString
					+ ". Choose from the following: " + String.join(", ", WbsEnums.toStringList(WbsParticleType.class)), directory + "/type");
			throw new InvalidConfigurationException();
		}

		switch (type) {
			case CUBOID:
				return new CuboidParticleEffect(section, settings, directory);
			case DISC:
				return new DiscParticleEffect(section, settings, directory);
			case ELECTRIC:
				return new ElectricParticleEffect(section, settings, directory);
			case LINE:
				return new LineParticleEffect(section, settings, directory);
			case NORMAL:
				return new NormalParticleEffect(section, settings, directory);
			case RING:
				return new RingParticleEffect(section, settings, directory);
			case SPHERE:
				return new SphereParticleEffect(section, settings, directory);
			case SPIRAL:
				return new SpiralParticleEffect(section, settings, directory);
		}

		return null;
	}

	protected static WbsUtils pl;
	public static void setPlugin(WbsUtils plugin) {
		pl = plugin;
	}
	
	protected double chance = 100;
	protected NumProvider amount;
	protected Object options = null;
	protected boolean force = true;
	
	public WbsParticleEffect() {
		amount = new NumProvider(1);
	}

	protected WbsParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		if (section.get("amount") != null) {
			amount = new NumProvider(section, "amount", settings, directory + "/amount", 1);
		} else {
			amount = new NumProvider(1);
		}

		if (section.get("force") != null) {
			force = section.getBoolean("force");
		}
	}

	/*===========================*/
	/*          BUILDER          */
	/*===========================*/

	protected final ArrayList<Vector> points = new ArrayList<>();
	
	/**
	 * Generate the particle set based on current settings.
	 * Call this before the first time it is run, or call
	 * buildAndRun each time to run based on current settings.
	 */
	public abstract WbsParticleEffect build();

	/**
	 * Refresh all providers for this object.
	 * Subclasses may override this, but should
	 * call super.refreshProviders() at the start
	 * of the method
	 */
	protected void refreshProviders() {
		amount.refresh();
	}

	/**
	 * Run and regenerate the point set based on new settings.
	 */
	public final WbsParticleEffect buildAndPlay(Particle particle, Location loc) {
		build();
		return play(particle, loc);
	}

	/*===========================*/
	/*        RUN METHODS        */
	/*===========================*/
	
	/**
	 * Run the effect pattern at the given location with the given particle.
	 * For shapes needing two locations (such as Line), a random second location
	 * will be chosen a random number of blocks away from the given
	 * location.
	 * @param particle The particle type to use
	 * @param loc The location at which to run the effect.
	 */
	public abstract WbsParticleEffect play(Particle particle, Location loc);

	/**
	 * Run the effect pattern at the given location with the given particle.
	 * For shapes needing two locations (such as Line), a random second location
	 * will be chosen a random number of blocks away from the given
	 * location.
	 * @param particle The particle type to use
	 * @param loc The location at which to run the effect.
	 * @param player The only play who will see it
	 */
	public abstract WbsParticleEffect play(Particle particle, Location loc, Player player);

	/*===========================*/
	/*       UTILITY METHODS      */
	/*===========================*/
	
	protected ArrayList<Location> filterChances(ArrayList<Location> points) {
		if (chance < 100) {
			LinkedList<Location> removePoints = new LinkedList<>();
			for (Location point : points) {
				if (!WbsMath.chance(chance)) {
					removePoints.add(point);
				}
			}
			
			points.removeAll(removePoints);
		}
		
		return points;
	}
	
	protected ArrayList<Location> getLocations(Location loc) {
		if (points.isEmpty()) {
			build();
		}
		ArrayList<Location> locations = WbsMath.offsetPoints(loc, points);
		return filterChances(locations);
	}

	/*===========================*/
	/*        MATH METHODS        */
	/*===========================*/
	
	protected static Vector scaleVector(Vector original, double magnitude) {
		return (original.clone().normalize().multiply(magnitude));
	}
	
	protected static double rand(double max) {
		double returnVal = Math.random() * 2 - 1;
		returnVal = returnVal * max;
		return returnVal;
	}

	/*===============*/
	/*     CLONE     */
	/*===============*/
	
	public abstract WbsParticleEffect clone();
	
	protected WbsParticleEffect cloneInto(WbsParticleEffect cloned) {
		cloned.setAmount((int) amount.val())
				.setChance(chance)
				.setOptions(options);
		
		return cloned;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/

	/**
	 * Gets the amount defined in setAmount(). Note that,
	 * while in many shapes this will be the amount of particles spawn,
	 * it's not always the case.
	 * @return The amount
	 */
	public int getAmount() {
		return (int) amount.val();
	}

	/**
	 * Gets the amount provider directly.
	 * @return The amount provider
	 */
	public NumProvider getAmountProvider() {
		return amount;
	}

	/**
	 * @param amount The new amount
	 * @return The same particle effect
	 */
	public WbsParticleEffect setAmount(int amount) {
		this.amount = new NumProvider(amount);
		return this;
	}

	/**
	 * @param amount The new amount provider
	 * @return The same particle effect
	 */
	public WbsParticleEffect setAmount(NumProvider amount) {
		this.amount = new NumProvider(amount);
		return this;
	}

	/**
	 * @param options The particle specific options - different for redstone, falling dust etc
	 */
	public WbsParticleEffect setOptions(Object options) {
		this.options = options;
		return this;
	}
	
	/**
	 * Set the chance of a given point spawning a particle.
	 * Will not affect the NORMAL effect type
	 * @param chance The chance of a given point to spawn
	 * @return The same particle effect
	 */
	public WbsParticleEffect setChance(double chance) {
		this.chance = chance;
		return this;
	}

	/**
	 * Set whether particles spawned should be forced to render
	 * for players at any distance
	 * @param force Whether or not to force rendering
	 * @return The same particle effect
	 */
	public WbsParticleEffect setForce(boolean force) {
		this.force = force;
		return this;
	}

	/**
	 * @return Whether or not to force rendering for players
	 * at any distance
	 */
	public boolean getForce() {
		return force;
	}

	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	/**
	 * Save this effect in a config that can be read by
	 * {@link #buildParticleEffect(ConfigurationSection, WbsSettings, String)}
	 * @param section The section to write to
	 * @param path The field/path inside the given section
	 */
	public void writeToConfig(ConfigurationSection section, String path) {
		amount.writeToConfig(section, path + ".amount");
		section.set(path + ".force", force);
		section.set(path + ".chance", chance);
	}

}
