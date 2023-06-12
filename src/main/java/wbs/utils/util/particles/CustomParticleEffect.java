package wbs.utils.util.particles;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * An abstract particle effect to be overridden anonymously
 */
public abstract class CustomParticleEffect extends WbsParticleEffect {

	/**
	 * Set the points to be used in {@link #play(Particle, Location)}
	 * @param points The new list of points to play at
	 * @return The same particle effect
	 */
	public CustomParticleEffect setPoints(List<Vector> points) {
		this.points.clear();
		this.points.addAll(points);
		return this;
	}
	
	@Override
	public final CustomParticleEffect build() {
		return this;
	}

	public abstract CustomParticleEffect play(Particle particle, Location loc);

	@Override
	public CustomParticleEffect clone() {
		return null;
	}

}
