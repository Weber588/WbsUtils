package wbs.utils.util.particles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that has a radius, rotation, and axis of rotation
 */
public abstract class CircleParticleEffect extends VelocityParticleEffect {

	public CircleParticleEffect() {
		super();
		radius = new NumProvider(1);
		rotation = new NumProvider(0);
		about = new VectorProvider(upVector);
	}
	
	protected VectorProvider about;
	protected NumProvider radius;
	protected NumProvider rotation;

	protected CircleParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
		WbsConfigReader.requireNotNull(section, "radius", settings, directory);
		radius = new NumProvider(section, "radius", settings, directory + "/radius", 1);

		if (section.get("rotation") != null) {
			rotation = new NumProvider(section, "rotation", settings, directory + "/rotation", 0);
		} else {
			rotation = new NumProvider(0);
		}

		ConfigurationSection aboutSection = section.getConfigurationSection("about");
		if (aboutSection != null) {
			about = new VectorProvider(aboutSection, settings, directory + "/about", upVector);
		} else {
			about = new VectorProvider(upVector);
		}
	}

	@Override
	protected void refreshProviders() {
		super.refreshProviders();

		about.refresh();
		radius.refresh();
		rotation.refresh();
	}

	protected CircleParticleEffect cloneInto(CircleParticleEffect cloned) {
		super.cloneInto(cloned);
		
		cloned.setAbout(about.val())
				.setRadius(radius.val())
				.setRotation(rotation.val());
		
		return cloned;
	}
	

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getRadius() {
		return radius.val();
	}
	public NumProvider getRadiusProvider() {
		return radius;
	}
	public CircleParticleEffect setRadius(double radius) {
		this.radius = new NumProvider(radius);
		return this;
	}
	public CircleParticleEffect setRadius(NumProvider radius) {
		this.radius = new NumProvider(radius);
		return this;
	}

	/**
	 * @return The vector about which rotation occurs
	 */
	public Vector getAbout() {
		return about.val();
	}
	public VectorProvider getAboutProvider() {
		return about;
	}

	/**
	 * Sets the vector about which rotation occurs
	 * @param about The about vector
	 * @return The same particle effect
	 */
	public CircleParticleEffect setAbout(Vector about) {
		this.about = new VectorProvider(about);
		return this;
	}

	/**
	 * Sets the about vector provider directly
	 * @param about The about vector provider
	 * @return The same particle effect
	 */
	public CircleParticleEffect setAbout(VectorProvider about) {
		this.about = new VectorProvider(about);
		return this;
	}

	/**
	 * @return The rotation in degrees
	 */
	public double getRotation() {
		return rotation.val();
	}
	public NumProvider getRotationProvider() {
		return rotation;
	}
	/**
	 * @param rotation The rotation in degrees
	 * @return The same particle effect
	 */
	public CircleParticleEffect setRotation(double rotation) {
		this.rotation = new NumProvider(rotation);
		return this;
	}

	/**
	 * Set the rotation as a provider directly
	 * @param rotation The new rotation NumProvider
	 * @return The same particle effect
	 */
	public CircleParticleEffect setRotation(NumProvider rotation) {
		this.rotation = new NumProvider(rotation);
		return this;
	}

	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);
		about.writeToConfig(section, path + ".about");
		radius.writeToConfig(section, path + ".radius");
		rotation.writeToConfig(section, path + ".rotation");
	}
}
