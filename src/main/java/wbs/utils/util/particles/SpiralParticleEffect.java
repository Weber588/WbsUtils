package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import wbs.utils.util.WbsMath;

public class SpiralParticleEffect extends CircleParticleEffect {

	public SpiralParticleEffect() {
		
	}
	
	private boolean clockwise = true;
	
	@Override
	public SpiralParticleEffect clone() {
		SpiralParticleEffect cloned = new SpiralParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setClockwise(clockwise);
		
		return cloned;
	}

	@Override
	public SpiralParticleEffect build() {
		points.clear();
		if (about.equals(upVector)) {
			points.addAll(WbsMath.get2Ring(amount, radius, rotation));
		} else {
			points.addAll(WbsMath.get3Ring(amount, radius, about, rotation));
		}
		
		return this;
	}

	/*
	 * Overriding because velocity is specifically controlled here.
	 */
	@Override
	public SpiralParticleEffect play(Particle particle, Location loc) {
		World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		direction.normalize().multiply(1);
		
		ArrayList<Location> locations = getLocations(loc);

		int i = 0;
		Location velPoint;
		for (Location point : locations) {
			if (!WbsMath.chance(chance)) {
				continue;
			}
			i++;
			if (clockwise) {
				velPoint = locations.get((i + (points.size() / 4)) % points.size());
			} else {
				int pointerIndex = (i - (points.size() / 4)) % points.size();
				while (pointerIndex < 0) {
					pointerIndex += locations.size();
				}
				
				velPoint = locations.get(pointerIndex);
			}
			Vector vec = velPoint.clone().subtract(loc.toVector()).toVector();
			vec = scaleVector(vec, variation);
			Vector vecSave = vec;
			for (int k = 0; k < amount; k++) {
				vec = vecSave.clone();
				if (options == null) {
					world.spawnParticle(particle, point, 0, vec.getX() + direction.getX(), vec.getY() + direction.getY(), vec.getZ() + direction.getZ(), speed, null, true);
				} else {
					world.spawnParticle(particle, point, 0, vec.getX() + direction.getX(), vec.getY() + direction.getY(), vec.getZ() + direction.getZ(), speed, particle.getDataType().cast(options), true);
				}
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
	public SpiralParticleEffect setRadius(double radius) {
		this.radius = radius;
		return this;
	}

	public double getSpeed() {
		return speed;
	}
	public SpiralParticleEffect setSpeed(double speed) {
		this.speed = speed;
		return this;
	}
	
	public Vector getAbout() {
		return about;
	}
	public SpiralParticleEffect setAbout(Vector about) {
		this.about = about;
		return this;
	}
	
	public Vector getDirection() {
		return direction;
	}
	public SpiralParticleEffect setDirection(Vector direction) {
		this.direction = direction;
		return this;
	}
	
	public double getRotation() {
		return rotation;
	}
	/**
	 * @param rotation The rotation in degrees
	 */
	public SpiralParticleEffect setRotation(double rotation) {
		this.rotation = rotation;
		return this;
	}

	public double getVariation() {
		return variation;
	}
	/**
	 * @param variation The variation in direction when the
	 * particles have speed.
	 */
	public SpiralParticleEffect setVariation(double variation) {
		this.variation = variation;
		return this;
	}
	
	public SpiralParticleEffect setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
		
		return this;
	}
}
