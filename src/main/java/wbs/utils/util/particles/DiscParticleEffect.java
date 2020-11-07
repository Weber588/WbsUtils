package wbs.utils.util.particles;

import wbs.utils.util.WbsMath;

public class DiscParticleEffect extends CircleParticleEffect {

	public DiscParticleEffect() {
		
	}
	
	private boolean random = false;
	
	@Override
	public DiscParticleEffect clone() {
		DiscParticleEffect cloned = new DiscParticleEffect();
		this.cloneInto(cloned);
		
		return cloned;
	}
	
	@Override
	public DiscParticleEffect build() {
		points.clear();
		if (about.equals(upVector)) {
			if (random) {
				points.addAll(WbsMath.getRandom2Disc(amount, radius));
			} else {
				points.addAll(WbsMath.get2Disc(amount, radius, rotation));
			}
		} else {
			points.addAll(WbsMath.get3Disc(amount, radius, about, rotation));
		}
		
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public boolean getRandom() {
		return random;
	}
	public DiscParticleEffect setRandom(boolean random) {
		this.random = random;
		return this;
	}
}
