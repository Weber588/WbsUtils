package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that draws a ring but specifically controls
 * speed to move outwards to draw a spiral
 */
public class SpiralParticleEffect extends CircleParticleEffect {

	public SpiralParticleEffect() {
		super();
	}
	
	private boolean clockwise = true;

	/**
	 * Create this effect from a ConfigurationSection, logging errors in the given settings
	 * @param section The section where this effect is defined
	 * @param settings The settings to log errors against
	 * @param directory The path taken through the config to get to this point, for logging purposes
	 */
	public SpiralParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
		if (section.get("clockwise") != null) {
			clockwise = section.getBoolean("clockwise");
		}
	}

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
		refreshProviders();

		if (about.val().equals(upVector)) {
			points.addAll(WbsMath.get2Ring(amount.intVal(), radius.val(), rotation.val()));
		} else {
			points.addAll(WbsMath.get3Ring(amount.intVal(), radius.val(), about.val(), rotation.val()));
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

		direction.val().normalize();
		
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
			vec = scaleVector(vec, variation.val());
			Vector vecSave = vec;
			for (int k = 0; k < amount.intVal(); k++) {
				vec = vecSave.clone();
				if (options == null) {
					world.spawnParticle(particle, point, 0, vec.getX() + direction.getX(), vec.getY() + direction.getY(), vec.getZ() + direction.getZ(), speed.val(), null, force);
				} else {
					world.spawnParticle(particle, point, 0, vec.getX() + direction.getX(), vec.getY() + direction.getY(), vec.getZ() + direction.getZ(), speed.val(), particle.getDataType().cast(options), force);
				}
			}
		}
		
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/

	/**
	 * @return Whether or not to draw clockwise
	 */
	public boolean getClockwise() {
		return clockwise;
	}

	/**
	 * @param clockwise Whether or not to draw clockwise
	 * @return The same particle effect
	 */
	public SpiralParticleEffect setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
		
		return this;
	}

	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		section.set(path + "clockwise", clockwise);
	}
}
