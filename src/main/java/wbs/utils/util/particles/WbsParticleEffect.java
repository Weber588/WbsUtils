package wbs.utils.util.particles;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import wbs.utils.WbsUtils;
import wbs.utils.util.WbsMath;

public abstract class WbsParticleEffect {
	protected static Vector upVector = new Vector(0, 1, 0);

	protected static WbsUtils pl;
	public static void setPlugin(WbsUtils plugin) {
		pl = plugin;
	}
	
	protected double chance = 100;
	protected int amount = 1;
	protected Object options = null;
	
	public WbsParticleEffect() {
		
	}


	/*===========================*/
	/*          BUILDER          */
	/*===========================*/
	
	protected final ArrayList<Vector> points = new ArrayList<>();
	protected Particle particle;
	
	/**
	 * Generate the particle set based on current settings.
	 * Call this before the first time it is run, or call
	 * buildAndRun each time to run based on current settings.
	 */
	public abstract WbsParticleEffect build();

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
		cloned.setAmount(amount)
				.setChance(chance)
				.setOptions(options);
		
		return cloned;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public int getAmount() {
		return amount;
	}
	public WbsParticleEffect setAmount(int amount) {
		this.amount = amount;
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
}
