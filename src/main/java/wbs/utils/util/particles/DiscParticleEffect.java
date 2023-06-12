package wbs.utils.util.particles;

import org.bukkit.configuration.ConfigurationSection;
import wbs.utils.util.WbsMath;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that appears in a disc
 */
public class DiscParticleEffect extends CircleParticleEffect {

	/**
	 * Create this effect with default values.
	 */
	public DiscParticleEffect() {}
	
	private boolean random = false;

	/**
	 * Create this effect from a ConfigurationSection, logging errors in the given settings
	 * @param section The section where this effect is defined
	 * @param settings The settings to log errors against
	 * @param directory The path taken through the config to get to this point, for logging purposes
	 */
	protected DiscParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
		if (section.get("random") != null) {
			random = section.getBoolean("random");
		}
	}

	@Override
	public DiscParticleEffect clone() {
		DiscParticleEffect cloned = new DiscParticleEffect();
		this.cloneInto(cloned);
		
		return cloned;
	}
	
	@Override
	public DiscParticleEffect build() {
		points.clear();
		refreshProviders();

		if (about.val().equals(upVector)) {
			if (random) {
				points.addAll(WbsMath.getRandom2Disc(amount.intVal(), radius.val()));
			} else {
				points.addAll(WbsMath.get2Disc(amount.intVal(), radius.val(), rotation.val()));
			}
		} else {
			points.addAll(WbsMath.get3Disc(amount.intVal(), radius.val(), about.val(), rotation.val()));
		}
		
		return this;
	}

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/

	/**
	 * @return Whether or not to randomly distribute
	 * points on the disc
	 */
	public boolean getRandom() {
		return random;
	}

	/**
	 * @param random Whether or not to randomly distribute
	 *               points on the disc
	 * @return The same particle effect
	 */
	public DiscParticleEffect setRandom(boolean random) {
		this.random = random;
		return this;
	}

	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		section.set(path + ".random", random);
	}
}
