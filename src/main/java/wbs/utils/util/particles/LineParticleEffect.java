package wbs.utils.util.particles;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that spawns points on a line between two points
 */
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
	private boolean scaleAmount = false;

	protected LineParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
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

		ConfigurationSection endSection = section.getConfigurationSection("end");
		if (endSection != null) {
			end = new VectorProvider(endSection, settings, directory + "/end", upVector);
		} else {
			end = new VectorProvider(upVector);
		}

		if (section.get("random") != null) {
			random = section.getBoolean("random", random);
		}

		if (section.get("scale-amount") != null) {
			scaleAmount = section.getBoolean("scale-amount");
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
	 * within a radius of 1 of loc. Or, if
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

		if (scaleAmount) {
			int localAmount = (int) (amount.intVal() * start.distance(finish));
			points.addAll(WbsMath.getLine(localAmount, finish.clone().subtract(start).toVector()));
		} else {
			points.addAll(WbsMath.getLine(amount.intVal(), finish.clone().subtract(start).toVector()));
		}

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

		if (scaleAmount) {
			int localAmount = (int) (amount.intVal() * start.distance(finish));
			points.addAll(WbsMath.getLine(localAmount, finish.clone().subtract(start).toVector()));
		} else {
			points.addAll(WbsMath.getLine(amount.intVal(), finish.clone().subtract(start).toVector()));
		}

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

	/**
	 * @return The thickness of the line
	 */
	public double getRadius() {
		return radius.val();
	}

	/**
	 * @param radius The thickness of the line
	 * @return The same particle effect
	 */
	public LineParticleEffect setRadius(double radius) {
		this.radius = new NumProvider(radius);
		return this;
	}

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
	public LineParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	/**
	 * @return Whether or not to use the amount value
	 * as points-block-block
	 */
	public boolean getScaleAmount() {
		return scaleAmount;
	}

	/**
	 * @param scaleAmount Whether or not to use the amount value
	 *                    as points-block-block
	 * @return The same particle effect
	 */
	public LineParticleEffect setScaleAmount(boolean scaleAmount) {
		this.scaleAmount = scaleAmount;
		return this;
	}

	/**
	 * @return Whether or not the other end of the line should be
	 * random when {@link #play(Particle, Location)} is called without a second
	 * location. When false, the end point must have been set with {@link #setEnd(Vector)}
	 */
	public boolean getRandom() {
		return random;
	}

	/**
	 * @param random Whether or not the other end of the line should be
	 *               random when {@link #play(Particle, Location)} is called without a second
	 *               location. When false, the end point must have been set with {@link #setEnd(Vector)}
	 * @return The same particle effect
	 */
	public LineParticleEffect setRandom(boolean random) {
		this.random = random;
		return this;
	}

	/**
	 * @return The end point to use when {@link #setRandom(boolean)} has been set to false
	 */
	public Vector getEnd() {
		return end.val();
	}

	/**
	 * @param end The end point to use when {@link #setRandom(boolean)} has been set to false
	 * @return The same particle effect
	 */
	public LineParticleEffect setEnd(Vector end) {
		this.end = new VectorProvider(end);
		return this;
	}
	/**
	 * @param end The end point to use when {@link #setRandom(boolean)} has been set to false
	 * @return The same particle effect
	 */
	public LineParticleEffect setEnd(VectorProvider end) {
		this.end = end;
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
