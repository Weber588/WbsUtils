package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import wbs.utils.util.WbsMath;

public class LineParticleEffect extends WbsParticleEffect {

	public LineParticleEffect() {
		
	}

	private double radius = 0;
	private double speed = 0;

	@Override
	public LineParticleEffect clone() {
		LineParticleEffect cloned = new LineParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setRadius(radius)
				.setSpeed(speed);
		
		return cloned;
	}

	@Override
	public LineParticleEffect build() {
		/* As start and finish location are given
		 * at runtime, can't pre-generate points.
		 * (Not really an issue though as it's an
		 * easy calculation)
		 */
		return this;
	}

	/**
	 * Run the effect pattern at the given location with the given particle.
	 * As Line needs a second location, this method generates a random second location
	 * within a radius of 1 of loc.
	 * @param particle The particle type to use
	 * @param loc The location at which to run the effect.
	 */
	@Override
	public LineParticleEffect play(Particle particle, Location loc) {
		play(particle, loc, loc.clone().add(WbsMath.randomVector()));
		return this;
	}
	
	public LineParticleEffect play(Particle particle, Location start, Location finish) {
		World world = start.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Start location had an invalid world");
		if (finish.getWorld() != world)
			throw new IllegalArgumentException("Start and Finish location must be constrained to the same world");

		points.clear();
		points.addAll(WbsMath.getLine(amount, finish.clone().subtract(start).toVector()));
		
		ArrayList<Location> locations = WbsMath.offsetPoints(start, points);
		locations = filterChances(locations);
		
		if (options == null) {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 1, radius, radius, radius, speed, null, true);
			}
		} else {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 1, radius, radius, radius, speed, particle.getDataType().cast(options), true);
			}
		}
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getRadius() {
		return radius;
	}
	public LineParticleEffect setRadius(double radius) {
		this.radius = radius;
		return this;
	}

	public double getSpeed() {
		return speed;
	}
	public LineParticleEffect setSpeed(double speed) {
		this.speed = speed;
		return this;
	}
}
