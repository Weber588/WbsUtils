package wbs.utils.util.particles;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A superclass for particle effects that can have speed set in specific
 * directions
 */
public abstract class VelocityParticleEffect extends WbsParticleEffect implements SpeedParticleEffect {

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

	/**
	 * Create this effect from a ConfigurationSection, logging errors in the given settings
	 * @param section The section where this effect is defined
	 * @param settings The settings to log errors against
	 * @param directory The path taken through the config to get to this point, for logging purposes
	 */
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

		ConfigurationSection directionSection = section.getConfigurationSection("direction");
		if (directionSection != null) {
			direction = new VectorProvider(directionSection, settings, directory + "/direction", upVector);
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

		cloned.setDirection(new VectorProvider(direction))
				.setVariation(new NumProvider(variation))
				.setSpeed(new NumProvider(speed));
		
		return cloned;
	}

	@Override
	public VelocityParticleEffect play(Particle particle, Location loc, @Nullable Player player) {
		World world = Preconditions.checkNotNull(loc.getWorld(), "Location must be in a world.");

		ArrayList<Vector> points = getPoints();
		ArrayList<Location> locations = WbsMath.offsetPoints(loc, points);

		List<Vector> localDirections = new ArrayList<>();
		if (relative) {
            for (Location offsetLoc : locations) {
				Vector direction = offsetLoc.clone()
						.subtract(loc)
						.toVector();

	            localDirections.add(direction);
            }
        } else {
			for (int i = 0; i < locations.size(); i++) {
				localDirections.add(direction.val());
			}
		}

		for (int i = 0; i < points.size(); i++) {
			Location locPoint = locations.get(i);
			Vector localDirection = localDirections.get(i);
			Vector point = points.get(i);

			Object data = getData(point, particle);
			if (player == null) {
				playParticle(world, particle, locPoint, localDirection, data);
			} else {
				playParticle(particle, player, locPoint, localDirection, data);
			}
        }
		return this;
	}

	private void playParticle(Particle particle, Player player, Location locPoint, Vector localDirection, Object data) {
        player.spawnParticle(particle, locPoint, 0, localDirection.getX() + rand(variation.val()), localDirection.getY() + rand(variation.val()), localDirection.getZ() + rand(variation.val()), speed.val(), data, force);
    }

	private void playParticle(World world, Particle particle, Location locPoint, Vector localDirection, Object data) {
		world.spawnParticle(particle, locPoint, 0, localDirection.getX() + rand(variation.val()), localDirection.getY() + rand(variation.val()), localDirection.getZ() + rand(variation.val()), speed.val(), data, force);
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/

	@Override
	public double getSpeed() {
		return speed.val();
	}
	@Override
	public NumProvider getSpeedProvider() {
		return speed;
	}
	@Override
	public VelocityParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}
	@Override
	public VelocityParticleEffect setSpeed(NumProvider speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	public Vector getDirection() {
		return direction.val();
	}
	public VectorProvider getDirectionProvider() {
		return direction;
	}
	public VelocityParticleEffect setDirection(Vector direction) {
		this.direction = new VectorProvider(direction);
		return this;
	}
	public VelocityParticleEffect setDirection(VectorProvider direction) {
		this.direction = new VectorProvider(direction);
		return this;
	}

	public double getVariation() {
		return variation.val();
	}
	public NumProvider getVariationProvider() {
		return variation;
	}

	/**
	 * @param variation The variation in direction when the
	 * particles have speed.
	 * @return The same object.
	 */
	public VelocityParticleEffect setVariation(double variation) {
		this.variation = new NumProvider(variation);
		return this;
	}

	/**
	 * @param variation The variation in direction when the
	 * particles have speed.
	 * @return The same object.
	 */
	public VelocityParticleEffect setVariation(NumProvider variation) {
		this.variation = new NumProvider(variation);
		return this;
	}

	/**
	 * @return Whether or not the velocity is relative to the origin of the
	 * particle effect.
	 */
	public boolean getRelative() {
		return relative;
	}

	/**
	 * @param relative Whether or not this effect should be relative to the
	 *                 origin of the particle effect.
	 * @return The same object.
	 */
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
