package wbs.utils.util.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that mimics the default behaviour of particle spawning,
 * and works the same as the vanilla /particle command.
 */
public class NormalParticleEffect extends WbsParticleEffect {

	public NormalParticleEffect() {
		speed = new NumProvider(0);
		x = new NumProvider(0);
		y = new NumProvider(0);
		z = new NumProvider(0);
	}

	private NumProvider speed;
	private NumProvider x, y, z;

	protected NormalParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
		if (section.get("speed") != null) {
			speed = new NumProvider(section, "speed", settings, directory + "/speed", 0);
		} else {
			speed = new NumProvider(0);
		}

		WbsConfigReader.requireNotNull(section, "xSize", settings, directory);
		x = new NumProvider(section, "xSize", settings, directory + "/xSize", 0);
		WbsConfigReader.requireNotNull(section, "ySize", settings, directory);
		y = new NumProvider(section, "ySize", settings, directory + "/ySize", 0);
		WbsConfigReader.requireNotNull(section, "zSize", settings, directory);
		z = new NumProvider(section, "zSize", settings, directory + "/zSize", 0);

	}

	@Override
	public NormalParticleEffect clone() {
		NormalParticleEffect cloned = new NormalParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setX(x.val())
				.setY(y.val())
				.setZ(z.val())
				.setSpeed(speed.val());
		
		return null;
	}

	@Override
	protected void refreshProviders() {
		super.refreshProviders();

		speed.refresh();
		x.refresh();
		y.refresh();
		z.refresh();
	}

	@Override
	public NormalParticleEffect build() {
		refreshProviders();
		// Nothing to pre-generate
		return this;
	}

	@Override
	public NormalParticleEffect play(Particle particle, Location loc, Player player) {

		if (options == null) {
			player.spawnParticle(particle, loc, amount.intVal(), x.val(), y.val(), z.val(), speed.val(), null);
		} else {
			player.spawnParticle(particle, loc, amount.intVal(), x.val(), y.val(), z.val(), speed.val(), particle.getDataType().cast(options));
		}

		return this;
	}

	@Override
	public NormalParticleEffect play(Particle particle, Location loc) {
		World world = loc.getWorld();
		if (world != null) {
			if (options == null) {
				world.spawnParticle(particle, loc, amount.intVal(), x.val(), y.val(), z.val(), speed.val(), null, force);
			} else {
				world.spawnParticle(particle, loc, amount.intVal(), x.val(), y.val(), z.val(), speed.val(), particle.getDataType().cast(options), force);
			}
		}
		
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/

	/**
	 * @return The speed spawned particles will go in a random direction
	 */
	public double getSpeed() {
		return speed.val();
	}

	/**
	 * @param speed The speed spawned particles will go in a random direction
	 * @return The same particle effect
	 */
	public NormalParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	/**
	 * Set the size of the region in the X axis
	 * @param x The new size in the X axis
	 * @return The same particle effect
	 */
	public NormalParticleEffect setX(double x) {
		this.x = new NumProvider(x);
		return this;
	}
	/**
	 * Set the size of the region in the Y axis
	 * @param y The new size in the Y axis
	 * @return The same particle effect
	 */
	public NormalParticleEffect setY(double y) {
		this.y = new NumProvider(y);
		return this;
	}
	/**
	 * Set the size of the region in the Z axis
	 * @param z The new size in the Z axis
	 * @return The same particle effect
	 */
	public NormalParticleEffect setZ(double z) {
		this.z = new NumProvider(z);
		return this;
	}

	/**
	 * Set the size of the region in the all 3 axes,
	 * making the region a cube
	 * @param size The new size in the all axes
	 * @return The same particle effect
	 */
	public NormalParticleEffect setXYZ(double size) {
		setX(size);
		setY(size);
		setZ(size);
		return this;
	}

	/**
	 * Set the size of the region in each axis defined by the
	 * X, Y, and Z components of a given vector
	 * @param xyz The vector representing the size of the region
	 * @return The same particle effect
	 */
	public NormalParticleEffect setXYZ(Vector xyz) {
		setX(xyz.getX());
		setY(xyz.getY());
		setZ(xyz.getZ());
		return this;
	}

	/**
	 * @return The current X axis size of this particle
	 */
	public double getX() {
		return x.val();
	}

	/**
	 * @return The current Y axis size of this particle
	 */
	public double getY() {
		return y.val();
	}

	/**
	 * @return The current Z axis size of this particle
	 */
	public double getZ() {
		return z.val();
	}

	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		x.writeToConfig(section, path + ".xSize");
		y.writeToConfig(section, path + ".ySize");
		z.writeToConfig(section, path + ".zSize");

		speed.writeToConfig(section, path + ".speed");
	}
}
