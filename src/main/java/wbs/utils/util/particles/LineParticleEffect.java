package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import wbs.utils.util.WbsMath;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

public class LineParticleEffect extends WbsParticleEffect {

	public LineParticleEffect() {
		radius = new NumProvider(0);
		speed = new NumProvider(0);
	}

	// When true, radius is used in a random direction. When false, "end" is used for point2.
	private boolean random;
	private NumProvider radius;
	private NumProvider speed;
	private VectorProvider end;

	public LineParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
		if (section.get("radius") != null) {
			radius = new NumProvider(section, "radius", settings, directory + "/radius", 0);
		} else{
			radius = new NumProvider(0);
		}
		if (section.get("speed") != null) {
			speed = new NumProvider(section, "speed", settings, directory + "/speed", 0);
		} else{
			speed = new NumProvider(0);
		}

		random = section.get("end") == null;

		if (section.get("end") != null) {
			end = new VectorProvider(section.getConfigurationSection("end"), settings, directory + "/end", upVector);
		} else {
			end = new VectorProvider(upVector);
		}

		if (section.get("random") != null) {
			random = section.getBoolean("random", random);
		}
	}

	@Override
	public LineParticleEffect clone() {
		LineParticleEffect cloned = new LineParticleEffect();
		this.cloneInto(cloned);
		
		cloned.setRadius(radius.val())
				.setSpeed(speed.val());
		
		return cloned;
	}

	@Override
	protected void refreshProviders() {
		super.refreshProviders();

		radius.refresh();
		speed.refresh();
		end.refresh();
	}

	@Override
	public LineParticleEffect build() {
		refreshProviders();
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
		if (random) {
			play(particle, loc, loc.clone().add(WbsMath.randomVector()));
		} else {
			play(particle, loc, loc.clone().add(end.val()));
		}
		return this;
	}


	@Override
	public LineParticleEffect play(Particle particle, Location loc, Player player) {
		if (random) {
			play(particle, loc, loc.clone().add(WbsMath.randomVector()), player);
		} else {
			play(particle, loc, loc.clone().add(end.val()), player);
		}
		return this;
	}

	public LineParticleEffect play(Particle particle, Location start, Location finish, Player player) {
		points.clear();
		points.addAll(WbsMath.getLine(amount.intVal(), finish.clone().subtract(start).toVector()));

		ArrayList<Location> locations = WbsMath.offsetPoints(start, points);
		locations = filterChances(locations);

		if (options == null) {
			for (Location point : locations) {
				player.spawnParticle(particle, point, 1, radius.val(), radius.val(), radius.val(), speed.val(), null);
			}
		} else {
			for (Location point : locations) {
				player.spawnParticle(particle, point, 1, radius.val(), radius.val(), radius.val(), speed.val(), particle.getDataType().cast(options));
			}
		}
		return this;
	}

	public LineParticleEffect play(Particle particle, Location start, Location finish) {
		World world = start.getWorld();

		if (world == null)
			throw new IllegalArgumentException("Start location had an invalid world");
		if (finish.getWorld() != world)
			throw new IllegalArgumentException("Start and Finish location must be constrained to the same world");

		points.clear();
		points.addAll(WbsMath.getLine(amount.intVal(), finish.clone().subtract(start).toVector()));
		
		ArrayList<Location> locations = WbsMath.offsetPoints(start, points);
		locations = filterChances(locations);
		
		if (options == null) {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 1, radius.val(), radius.val(), radius.val(), speed.val(), null, force);
			}
		} else {
			for (Location point : locations) {
				world.spawnParticle(particle, point, 1, radius.val(), radius.val(), radius.val(), speed.val(), particle.getDataType().cast(options), force);
			}
		}
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getRadius() {
		return radius.val();
	}
	public LineParticleEffect setRadius(double radius) {
		this.radius = new NumProvider(radius);
		return this;
	}

	public double getSpeed() {
		return speed.val();
	}
	public LineParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}
	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		speed.writeToConfig(section, path + ".speed");
		radius.writeToConfig(section, path + ".radius");
		end.writeToConfig(section, path + ".end");
		section.set("random", random);
	}
}
