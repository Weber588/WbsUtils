package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import wbs.utils.util.WbsMath;

public class ElectricParticleEffect extends WbsParticleEffect {

	public ElectricParticleEffect() {
		
	}

	private double radius = 0.5;
	private double speed = 0;
	private double arcLength = 0.25;
	
	private int ticks = 2;
	
	@Override
	public ElectricParticleEffect clone() {
		ElectricParticleEffect cloned = new ElectricParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setRadius(radius)
				.setSpeed(speed);
		
		return cloned;
	}

	@Override
	public ElectricParticleEffect build() {
		/* As the locations are random, can't 
		 * pre-generate points.
		 * (Not really an issue though as it's an
		 * easy calculation)
		 */
		return this;
	}

	@Override
	public ElectricParticleEffect play(Particle particle, Location loc) {
		if (ticks == 1 || ticks == 0) {
			for (int i = 0; i < amount; i++) {
				drawArc(particle, loc);
			}
			return this;
		}
		
		new BukkitRunnable() {
			int escape = 0;
			@Override
			public void run() {
				escape++;
				if (escape > ticks) {
					cancel();
				}
				for (int i = 0; i < amount; i++) {
					drawArc(particle, loc);
				}
			}
        }.runTaskTimer(pl, 0L, 1L);
        
		return this;
	}

	private void drawArc(Particle particle, Location loc) {
		World world = loc.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Location had an invalid world.");

		Location newPoint, newEnd;
		
		newPoint = loc.clone().add(WbsMath.randomVector(radius));
		newEnd = newPoint.clone().add(WbsMath.randomVector(arcLength));

		points.clear();
		points.addAll(WbsMath.getLine((int) (10 * arcLength), newEnd.clone().subtract(newPoint).toVector()));
		
		ArrayList<Location> locations = WbsMath.offsetPoints(newPoint, points);
		locations = filterChances(locations);
		
		if (options == null) {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 1, 0, 0, 0, speed, null, true);
			}
		} else {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 1, 0, 0, 0, speed, particle.getDataType().cast(options), true);
			}
		}
	}
	
	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getRadius() {
		return radius;
	}
	public ElectricParticleEffect setRadius(double radius) {
		this.radius = radius;
		return this;
	}

	public double getSpeed() {
		return speed;
	}
	public ElectricParticleEffect setSpeed(double speed) {
		this.speed = speed;
		return this;
	}
	
	public double getArcLength() {
		return arcLength;
	}
	public ElectricParticleEffect setArcLength(double arcLength) {
		this.arcLength = arcLength;
		return this;
	}
	
	public int getTicks() {
		return ticks;
	}
	public ElectricParticleEffect setTicks(int ticks) {
		this.ticks = ticks;
		return this;
	}
}
