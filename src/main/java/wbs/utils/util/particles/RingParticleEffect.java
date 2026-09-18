package wbs.utils.util.particles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsSettings;

import java.util.LinkedList;
import java.util.List;

/**
 * A particle effect that appears in a ring of points
 */
public class RingParticleEffect extends CircleParticleEffect {

	public RingParticleEffect() {
		super();
	}

	/**
	 * Create this effect from a ConfigurationSection, logging errors in the given settings
	 * @param section The section where this effect is defined
	 * @param settings The settings to log errors against
	 * @param directory The path taken through the config to get to this point, for logging purposes
	 */
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
	protected List<Vector> generatePoints() {
		LinkedList<Vector> points = new LinkedList<>();

		if (about.val().equals(upVector)) {
			points.addAll(WbsMath.get2Ring(amount.intVal(), radius.val(), rotation.val()));
		} else {
			points.addAll(WbsMath.get3Ring(amount.intVal(), radius.val(), about.val(), rotation.val()));
		}
		
		return points;
	}
}
