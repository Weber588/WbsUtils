package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.Nullable;
import wbs.utils.util.WbsMath;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that spawns many small lines in a
 * random pattern around a location, similar to sparks
 */
public class ElectricParticleEffect extends WbsParticleEffect {

	/**
	 * Creates the effect with the following values:
	 * <ul>
	 *     <li>Radius: 0.5</li>
	 *     <li>Speed: 0</li>
	 *     <li>Arc Length: 0.25</li>
	 *     <li>Ticks: 2</li>
	 * </ul>
	 */
	public ElectricParticleEffect() {
		radius = new NumProvider(0.5);
		speed = new NumProvider(0);
		arcLength = new NumProvider(0.25);
		ticks = new NumProvider(2);
	}

	private NumProvider radius;
	private NumProvider speed;
	private NumProvider arcLength;
	
	private NumProvider ticks;

	/**
	 * Create this effect from a ConfigurationSection, logging errors in the given settings
	 * @param section The section where this effect is defined
	 * @param settings The settings to log errors against
	 * @param directory The path taken through the config to get to this point, for logging purposes
	 */
	protected ElectricParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);

		if (section.get("radius") != null) {
			radius = new NumProvider(section, "radius", settings, directory + "/radius", 0.5);
		} else {
			radius = new NumProvider(0.5);
		}
		if (section.get("speed") != null) {
			speed = new NumProvider(section, "speed", settings, directory + "/speed", 0);
		} else {
			speed = new NumProvider(0);
		}
		if (section.get("arcLength") != null) {
			arcLength = new NumProvider(section, "arcLength", settings, directory + "/arcLength", 0.25);
		} else {
			arcLength = new NumProvider(0.25);
		}

		if (section.get("ticks") != null) {
			ticks = new NumProvider(section, "ticks", settings, directory + "/ticks", 2);
		} else {
			ticks = new NumProvider(2);
		}
	}
	
	@Override
	public ElectricParticleEffect clone() {
		ElectricParticleEffect cloned = new ElectricParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setRadius(radius.val())
				.setSpeed(speed.val());
		
		return cloned;
	}

	@Override
	protected void refreshProviders() {
		super.refreshProviders();

		radius.refresh();
		speed.refresh();
		arcLength.refresh();
		ticks.refresh();
	}

	@Override
	public ElectricParticleEffect build() {
		refreshProviders();
		/* As the locations are random, can't 
		 * pre-generate points.
		 * (Not really an issue though as it's an
		 * easy calculation)
		 */
		return this;
	}

	@Override
	public ElectricParticleEffect play(Particle particle, Location loc, @Nullable Player player) {
		if (ticks.intVal() == 1 || ticks.intVal() == 0) {
			for (int i = 0; i < amount.intVal(); i++) {
				drawArc(particle, loc, player);
			}
			return this;
		}

		new BukkitRunnable() {
			int escape = 0;
			@Override
			public void run() {
				escape++;
				if (escape > ticks.intVal()) {
					cancel();
				}
				for (int i = 0; i < amount.intVal(); i++) {
					drawArc(particle, loc, player);
				}
			}
		}.runTaskTimer(pl, 0L, 1L);

		return this;
	}

	@Override
	public ElectricParticleEffect play(Particle particle, Location loc) {
		play(particle, loc, null);
		return this;
	}

	private void drawArc(Particle particle, Location loc, @Nullable Player player) {
		World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		Location newPoint, newEnd;
		
		newPoint = loc.clone().add(WbsMath.randomVector(radius.val()));
		newEnd = newPoint.clone().add(WbsMath.randomVector(arcLength.val()));

		points.clear();
		points.addAll(WbsMath.getLine((int) (10 * arcLength.val()), newEnd.clone().subtract(newPoint).toVector()));
		
		ArrayList<Location> locations = WbsMath.offsetPoints(newPoint, points);
		locations = filterChances(locations);

		if (player == null) {
			if (preventDataUse(particle)) {
				for (Location point : locations) {
					world.spawnParticle(particle, point, 1, 0, 0, 0, speed.val(), null, force);
				}
			} else {
				for (Location point : locations) {
					world.spawnParticle(particle, point, 1, 0, 0, 0, speed.val(), particle.getDataType().cast(data), force);
				}
			}
		} else {
			if (preventDataUse(particle)) {
				for (Location point : locations) {
					player.spawnParticle(particle, point, 1, 0, 0, 0, speed.val(), null);
				}
			} else {
				for (Location point : locations) {
					player.spawnParticle(particle, point, 1, 0, 0, 0, speed.val(), particle.getDataType().cast(data));
				}
			}
		}
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/

	/**
	 * @return The radius within which arcs can appear
	 */
	public double getRadius() {
		return radius.val();
	}

	/**
	 * Gets the radius provider directly
	 * @return The radius provider
	 */
	public NumProvider getRadiusProvider() {
		return radius;
	}

	/**
	 * @param radius The radius within which arcs can appear
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setRadius(double radius) {
		this.radius = new NumProvider(radius);
		return this;
	}

	/**
	 * Set the radius provider directly
	 * @param radius The radius within which arcs can appear
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setRadius(NumProvider radius) {
		this.radius = new NumProvider(radius);
		return this;
	}

	/**
	 * @return The speed of spawned particles in a random direction
	 */
	public double getSpeed() {
		return speed.val();
	}

	/**
	 * Gets the speed provider directly
	 * @return The speed provider
	 */
	public NumProvider getSpeedProvider() {
		return speed;
	}

	/**
	 * @param speed The speed of spawned particles in a random direction
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	/**
	 * Set the speed provider directly
	 * @param speed The speed of spawned particles in a random direction
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setSpeed(NumProvider speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	/**
	 * @return How long the spawned arcs should be
	 */
	public double getArcLength() {
		return arcLength.val();
	}

	/**
	 * Gets the arc length directly
	 * @return The arc length provider
	 */
	public NumProvider getArcLengthProvider() {
		return arcLength;
	}

	/**
	 * @param arcLength How long the spawned arcs should be
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setArcLength(double arcLength) {
		this.arcLength = new NumProvider(arcLength);
		return this;
	}

	/**
	 * Set the arc length directly
	 * @param arcLength How long the spawned arcs should be
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setArcLength(NumProvider arcLength) {
		this.arcLength = new NumProvider(arcLength);
		return this;
	}

	/**
	 * @return How many ticks to play the effect for
	 */
	public int getTicks() {
		return ticks.intVal();
	}

	/**
	 * @param ticks How many ticks to play the effect for
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setTicks(int ticks) {
		this.ticks = new NumProvider(ticks);
		return this;
	}

	/**
	 * Set the ticks provider directly
	 * @param ticks How many ticks to play the effect for
	 * @return The same particle effect
	 */
	public ElectricParticleEffect setTicks(NumProvider ticks) {
		this.ticks = new NumProvider(ticks);
		return this;
	}
	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		radius.writeToConfig(section, path + ".radius");
		speed.writeToConfig(section, path + ".speed");
		arcLength.writeToConfig(section, path + ".arcLength");
		ticks.writeToConfig(section, path + ".ticks");
	}
}
