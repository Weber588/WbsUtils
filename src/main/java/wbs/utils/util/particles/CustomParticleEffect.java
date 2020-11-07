package wbs.utils.util.particles;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public abstract class CustomParticleEffect extends WbsParticleEffect {

	public CustomParticleEffect() {
		
	}
	
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
