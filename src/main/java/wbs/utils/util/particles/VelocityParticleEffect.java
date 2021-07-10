package wbs.utils.util.particles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * Some particle effects don't allow particles
 * to be given speed; this is for the ones that do.
 * @author Weber588
 *
 */
public abstract class VelocityParticleEffect extends WbsParticleEffect {

	public VelocityParticleEffect() {
		super();
		direction = new VectorProvider(upVector);
		speed = new NumProvider(0);
		variation = new NumProvider(0);
	}
	
	protected VectorProvider direction;
	
	protected NumProvider speed;
	protected NumProvider variation;

	protected boolean relative = false;

	public VelocityParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);

		if (section.get("speed") != null) {
			speed = new NumProvider(section, "speed", settings, directory + "/speed", 0);
		} else {
			speed  = new NumProvider(0);
		}

		if (section.get("variation") != null) {
			variation = new NumProvider(section, "variation", settings, directory + "/variation", 0);
		} else {
			variation = new NumProvider(0);
		}

		if (section.get("direction") != null) {
			direction = new VectorProvider(section.getConfigurationSection("direction"), settings, directory + "/direction", upVector);
		} else {
			direction = new VectorProvider(upVector);
		}

		relative = section.getBoolean("relative");
	}

	@Override
	protected void refreshProviders() {
		super.refreshProviders();

		direction.refresh();
		speed.refresh();
		variation.refresh();
	}

	protected VelocityParticleEffect cloneInto(VelocityParticleEffect cloned) {
		super.cloneInto(cloned);

		cloned.setDirection(direction.val())
				.setVariation(variation.val())
				.setSpeed(speed.val());
		
		return cloned;
	}

	@Override
	public VelocityParticleEffect play(Particle particle, Location loc, Player player) {
		ArrayList<Location> locations = getLocations(loc);

		List<Vector> localDirections = new ArrayList<>();
		if (relative) {
			localDirections.addAll(points);
		} else {
			for (int i = 0; i < points.size(); i++) {
				localDirections.set(i, direction.val());
			}
		}

		for (int i = 0; i < points.size(); i++) {
			Location point = locations.get(i);
			Vector localDirection = localDirections.get(i);
			if (options == null) {
				player.spawnParticle(particle, point, 0, localDirection.getX() + rand(variation.val()), localDirection.getY() + rand(variation.val()), localDirection.getZ() + rand(variation.val()), speed.val(), null);
			} else {
				player.spawnParticle(particle, point, 0, localDirection.getX() + rand(variation.val()), localDirection.getY() + rand(variation.val()), localDirection.getZ() + rand(variation.val()), speed.val(), particle.getDataType().cast(options));
			}
		}
		return this;
	}

	@Override
	public VelocityParticleEffect play(Particle particle, Location loc) {
		World world = loc.getWorld();
		if (world == null) return this;
		ArrayList<Location> locations = getLocations(loc);

		List<Vector> localDirections = new ArrayList<>();
		if (relative) {
			locations.forEach(
					offsetLoc -> localDirections.add(
							offsetLoc.clone()
							.subtract(loc)
							.toVector()
					)
			);
		} else {
			for (int i = 0; i < locations.size(); i++) {
				localDirections.add(direction.val());
			}
		}

		for (int i = 0; i < locations.size(); i++) {
			Location point = locations.get(i);
			Vector localDirection = localDirections.get(i);
			if (options == null) {
				world.spawnParticle(particle, point, 0, localDirection.getX() + rand(variation.val()), localDirection.getY() + rand(variation.val()), localDirection.getZ() + rand(variation.val()), speed.val(), null, force);
			} else {
				world.spawnParticle(particle, point, 0, localDirection.getX() + rand(variation.val()), localDirection.getY() + rand(variation.val()), localDirection.getZ() + rand(variation.val()), speed.val(), particle.getDataType().cast(options), force);
			}
		}
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getSpeed() {
		return speed.val();
	}
	public VelocityParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	public Vector getDirection() {
		return direction.val();
	}
	public VelocityParticleEffect setDirection(Vector direction) {
		this.direction = new VectorProvider(direction);
		return this;
	}

	// TODO: Make these return a copy of the NumProvider
	public double getVariation() {
		return variation.val();
	}
	/**
	 * @param variation The variation in direction when the
	 * particles have speed.
	 */
	public VelocityParticleEffect setVariation(double variation) {
		this.variation = new NumProvider(variation);
		return this;
	}

	public VelocityParticleEffect setRelative(boolean relative) {
		this.relative = relative;
		return this;
	}
	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		speed.writeToConfig(section, path + ".speed");
		variation.writeToConfig(section, path + ".variation");
		direction.writeToConfig(section, path + ".direction");
	}
}
