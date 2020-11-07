package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Some particle effects don't allow particles
 * to be given speed; this is for the ones that do.
 * @author Weber588
 *
 */
public abstract class VelocityParticleEffect extends WbsParticleEffect {

	public VelocityParticleEffect() {
		
	}
	
	protected Vector direction = upVector;
	
	protected double speed = 0;
	protected double variation = 0;

	protected VelocityParticleEffect cloneInto(VelocityParticleEffect cloned) {
		super.cloneInto(cloned);
		
		cloned.setDirection(direction)
				.setVariation(variation)
				.setSpeed(speed);
		
		return cloned;
	}
	
	@Override
	public VelocityParticleEffect play(Particle particle, Location loc) {
		World world = loc.getWorld();
		if (world == null) return this;
		ArrayList<Location> locations = getLocations(loc);
		
		if (options == null) {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 0, direction.getX() + rand(variation), direction.getY() + rand(variation), direction.getZ() + rand(variation), speed, null, true);
			}
		} else {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 0, direction.getX() + rand(variation), direction.getY() + rand(variation), direction.getZ() + rand(variation), speed, particle.getDataType().cast(options), true);
			}
		}
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getSpeed() {
		return speed;
	}
	public VelocityParticleEffect setSpeed(double speed) {
		this.speed = speed;
		return this;
	}

	public Vector getDirection() {
		return direction;
	}
	public VelocityParticleEffect setDirection(Vector direction) {
		this.direction = direction;
		return this;
	}


	public double getVariation() {
		return variation;
	}
	/**
	 * @param variation The variation in direction when the
	 * particles have speed.
	 */
	public VelocityParticleEffect setVariation(double variation) {
		this.variation = variation;
		return this;
	}
}
