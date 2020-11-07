package wbs.utils.util.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class NormalParticleEffect extends WbsParticleEffect {

	public NormalParticleEffect() {
		
	}

	private double speed = 0;
	private double x = 0, y = 0, z = 0;
	
	@Override
	public NormalParticleEffect clone() {
		NormalParticleEffect cloned = new NormalParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setX(x)
				.setY(y)
				.setZ(z)
				.setSpeed(speed);
		
		return null;
	}

	@Override
	public NormalParticleEffect build() {
		// Nothing to pre-generate
		return this;
	}

	@Override
	public NormalParticleEffect play(Particle particle, Location loc) {
		World world = loc.getWorld();
		if (world != null) {
			if (options == null) {
				world.spawnParticle(particle, loc, amount, x, y, z, speed, null, true);
			} else {
				world.spawnParticle(particle, loc, amount, x, y, z, speed, particle.getDataType().cast(options), true);
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
	public NormalParticleEffect setSpeed(double speed) {
		this.speed = speed;
		return this;
	}

	public double getX() {
		return x;
	}
	public NormalParticleEffect setX(double x) {
		this.x = x;
		return this;
	}
	public double getY() {
		return y;
	}
	public NormalParticleEffect setY(double y) {
		this.y = y;
		return this;
	}
	public double getZ() {
		return z;
	}
	public NormalParticleEffect setZ(double z) {
		this.z = z;
		return this;
	}

	public NormalParticleEffect setXYZ(double size) {
		x = size;
		y = size;
		z = size;
		
		return this;
	}

}
