package wbs.utils.util.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class NormalParticleEffect extends WbsParticleEffect {

	public NormalParticleEffect() {
		speed = new NumProvider(0);
		x = new NumProvider(0);
		y = new NumProvider(0);
		z = new NumProvider(0);
	}

	private NumProvider speed;
	private NumProvider x, y, z;

	public NormalParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
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

	public double getSpeed() {
		return speed.val();
	}
	public NormalParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	public double getX() {
		return x.val();
	}
	public NormalParticleEffect setX(double x) {
		this.x = new NumProvider(x);
		return this;
	}
	public double getY() {
		return y.val();
	}
	public NormalParticleEffect setY(double y) {
		this.y = new NumProvider(y);
		return this;
	}
	public double getZ() {
		return z.val();
	}
	public NormalParticleEffect setZ(double z) {
		this.z = new NumProvider(z);
		return this;
	}

	public NormalParticleEffect setXYZ(double size) {
		x = new NumProvider(size);
		y = new NumProvider(size);
		z = new NumProvider(size);
		
		return this;
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
