package wbs.utils.util.particles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

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

	public CircleParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
		super(section, settings, directory);
		WbsConfigReader.requireNotNull(section, "radius", settings, directory);
		radius = new NumProvider(section, "radius", settings, directory + "/radius", 1);

		if (section.get("rotation") != null) {
			rotation = new NumProvider(section, "rotation", settings, directory + "/rotation", 0);
		} else {
			rotation = new NumProvider(0);
		}

		if (section.get("about") != null) {
			about = new VectorProvider(section.getConfigurationSection("about"), settings, directory + "/about", upVector);
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
	public CircleParticleEffect setRadius(double radius) {
		this.radius = new NumProvider(radius);
		return this;
	}
	public CircleParticleEffect setRadius(NumProvider radius) {
		this.radius = radius;
		return this;
	}
	
	public Vector getAbout() {
		return about.val();
	}
	public CircleParticleEffect setAbout(Vector about) {
		this.about = new VectorProvider(about);
		return this;
	}
	
	public double getRotation() {
		return rotation.val();
	}
	/**
	 * @param rotation The rotation in degrees
	 */
	public CircleParticleEffect setRotation(double rotation) {
		this.rotation = new NumProvider(rotation);
		return this;
	}
	public CircleParticleEffect setRotation(NumProvider rotation) {
		this.rotation = rotation;
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
