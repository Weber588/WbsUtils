package wbs.utils.util.particles;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that appears in a ring of points
 */
public class RingParticleEffect extends CircleParticleEffect {

	public RingParticleEffect() {
		super();
	}

	public RingParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
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
		refreshProviders();

		if (about.val().equals(upVector)) {
			points.addAll(WbsMath.get2Ring(amount.intVal(), radius.val(), rotation.val()));
		} else {
			points.addAll(WbsMath.get3Ring(amount.intVal(), radius.val(), about.val(), rotation.val()));
		}
		
		return this;
	}
}
