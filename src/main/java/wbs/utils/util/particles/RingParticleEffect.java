package wbs.utils.util.particles;

import wbs.utils.util.WbsMath;

public class RingParticleEffect extends CircleParticleEffect {

	public RingParticleEffect() {
		
	}
	
	@Override
	public RingParticleEffect clone() {
		RingParticleEffect cloned = new RingParticleEffect();
		this.cloneInto(cloned);
		
		return cloned;
	}
	
	@Override
	public WbsParticleEffect build() {
		points.clear();
		if (about.equals(upVector)) {
			points.addAll(WbsMath.get2Ring(amount, radius, rotation));
		} else {
			points.addAll(WbsMath.get3Ring(amount, radius, about, rotation));
		}
		
		return this;
	}
}
