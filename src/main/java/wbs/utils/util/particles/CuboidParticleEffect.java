package wbs.utils.util.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import wbs.utils.util.WbsMath;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

/**
 * A particle effect that draws the outline of a cuboid region
 */
public class CuboidParticleEffect extends VelocityParticleEffect {

	/**
	 * Creates this effect with x, y, and z statically initialized to 1, and rotation initialized as 0.
	 */
	public CuboidParticleEffect() {
		x = new NumProvider(1);
		y = new NumProvider(1);
		z = new NumProvider(1);

		rotation = new NumProvider(0);
	}
	
	private NumProvider x, y, z;
	private NumProvider rotation;
	private VectorProvider about = new VectorProvider(upVector);
	// When true, amount = particles per block. When false, amount = particles per edge
	private boolean scaleAmount = false;

	/**
	 * Create this effect from a ConfigurationSection, logging errors in the given settings
	 * @param section The section where this effect is defined
	 * @param settings The settings to log errors against
	 * @param directory The path taken through the config to get to this point, for logging purposes
	 */
    protected CuboidParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
    	super(section, settings, directory);

		WbsConfigReader.requireNotNull(section, "xSize", settings, directory );
		x = new NumProvider(section, "xSize", settings, directory + "/xSize", 1);
		WbsConfigReader.requireNotNull(section, "ySize", settings, directory);
		y = new NumProvider(section, "ySize", settings, directory + "/ySize", 1);
		WbsConfigReader.requireNotNull(section, "zSize", settings, directory);
		z = new NumProvider(section, "zSize", settings, directory + "/zSize", 1);

		if (section.getConfigurationSection("rotation") != null) {
			rotation = new NumProvider(section, "rotation", settings, directory + "/rotation", 0);
		} else {
			rotation = new NumProvider(0);
		}

		if (section.get("scale-amount") != null) {
			scaleAmount = section.getBoolean("scale-amount", scaleAmount);
		}

		ConfigurationSection aboutConfig = section.getConfigurationSection("about");
		if (aboutConfig != null) {
			about = new VectorProvider(aboutConfig, settings, directory + "/about", upVector);
		} else {
			about = new VectorProvider(upVector);
		}
	}

    @Override
	public CuboidParticleEffect clone() {
		CuboidParticleEffect cloned = new CuboidParticleEffect();
		this.cloneInto(cloned);
		
		cloned.x = this.x;
		cloned.y = this.y;
		cloned.z = this.z;
		
		return cloned;
	}

	@Override
	protected void refreshProviders() {
		super.refreshProviders();

		x.refresh();
		y.refresh();
		z.refresh();
		rotation.refresh();
		about.refresh();
	}

	@Override
	public CuboidParticleEffect build() {
		points.clear();
		refreshProviders();
		/*
		 *  Seed vertices: A+X+Y+Z, B+X-Y-Z, C-X+Y-Z, D-X-Y+Z
		 *  These vertices do not connect to each other, so 4 vertices with 3 edges
		 *  each is the whole cuboid
		 */
		Vector start = new Vector(x.val(), y.val(), z.val());
		
		Vector finish = new Vector(-x.val(), y.val(), z.val());
		
		double scaledX, scaledY, scaledZ; // Need to go size/2 away from the origin in each direction
		scaledX = x.val() / 2;
		scaledY = y.val() / 2;
		scaledZ = z.val() / 2;
		
		double[] signs = {1, 1, 1};
		Vector rotatedStart, rotatedFinish;
		
		for (int i = 0; i < 4; i++) {
			start.setX(scaledX*signs[0]);
			start.setY(scaledY*signs[1]);
			start.setZ(scaledZ*signs[2]);
			
			rotatedStart = WbsMath.rotateVector(start, about.val(), rotation.val());
			
			for (int j = 0; j < 3; j++) {
				signs[j] = -signs[j];
				finish.setX(scaledX*signs[0]);
				finish.setY(scaledY*signs[1]);
				finish.setZ(scaledZ*signs[2]);

				rotatedFinish = WbsMath.rotateVector(finish, about.val(), rotation.val());

				int localAmount;
				if (scaleAmount) {
					localAmount = (int) (rotatedStart.distance(rotatedFinish) * amount.val());
				} else {
					localAmount = amount.intVal();
				}
				points.addAll(WbsMath.getLine(localAmount, rotatedStart, rotatedFinish));
				
				signs[j] = -signs[j];
			}

			if (i % 2 == 0) {
				signs[0] = -signs[0];
				signs[1] = -signs[1];
			} else {
				signs[1] = -signs[1];
				signs[2] = -signs[2];
			}
		}
		
		return this;
	}

	/*
	 * TODO: Change location params to blocks to better convey intent of outlining blocks,
	 *  as opposed to the region between two corners.
	 */

	/*
	 * TODO: Add location version to create cuboid region between two locations exactly,
	 *  not adjusting for blocks.
	 */

	/**
	 * Configure this effect to outline the cuboid region defined by corner1, corner2
	 * and return the location to play at to center on those blocks.
	 * @param corner1 The first corner of the cuboid region
	 * @param corner2 The second corner of the cuboid region
	 * @return A location that, when used in {@link #play(Particle, Location)}, will make
	 * this effect outline the cuboid region defined
	 */
	public Location configureBlockOutline(Location corner1, Location corner2) {
		// Clone both to avoid changing the passed references
		corner1 = corner1.clone();
		corner2 = corner2.clone();

		if (corner1.getX() < corner2.getX()) {
			corner2.setX(corner2.getX() + 1);
		} else {
			corner1.setX(corner1.getX() + 1);
		}

		if (corner1.getY() < corner2.getY()) {
			corner2.setY(corner2.getY() + 1);
		} else {
			corner1.setY(corner1.getY() + 1);
		}

		if (corner1.getZ() < corner2.getZ()) {
			corner2.setZ(corner2.getZ() + 1);
		} else {
			corner1.setZ(corner1.getZ() + 1);
		}

		Location difference = corner2.clone().subtract(corner1);
		Vector halfDifference = difference.toVector().multiply(0.5);
		Location selectionCenter = corner1.clone().add(halfDifference);

		setXYZ(difference.toVector());

		return selectionCenter;
	}

	/**
	 * Set whether or not to use the "amount" field as points-per-block
	 * @param scaleAmount True to make amount be points-per-block
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setScaleAmount(boolean scaleAmount) {
		this.scaleAmount = scaleAmount;
		return this;
	}

	/**
	 * Set the size of the region in the X axis
	 * @param x The new size in the X axis
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setX(double x) {
		this.x = new NumProvider(x);
		return this;
	}
	/**
	 * Set the size of the region in the Y axis
	 * @param y The new size in the Y axis
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setY(double y) {
		this.y = new NumProvider(y);
		return this;
	}
	/**
	 * Set the size of the region in the Z axis
	 * @param z The new size in the Z axis
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setZ(double z) {
		this.z = new NumProvider(z);
		return this;
	}

	/**
	 * Set the provider for the region in the X axis
	 * @param x The new size in the X axis
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setX(NumProvider x) {
		this.x = new NumProvider(x);
		return this;
	}
	/**
	 * Set the size of the region in the Y axis
	 * @param y The new size in the Y axis
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setY(NumProvider y) {
		this.y = new NumProvider(y);
		return this;
	}
	/**
	 * Set the size of the region in the Z axis
	 * @param z The new size in the Z axis
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setZ(NumProvider z) {
		this.z = new NumProvider(z);
		return this;
	}

	/**
	 * Set the size of the region in the all 3 axes,
	 * making the region a cube
	 * @param size The new size in the all axes
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setXYZ(double size) {
    	setX(size);
    	setY(size);
    	setZ(size);
    	return this;
	}
	/**
	 * Set the provider to be used for all
	 * @param size The new size in the all axes
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setXYZ(NumProvider size) {
    	setX(size);
    	setY(size);
    	setZ(size);
    	return this;
	}

	/**
	 * Set the size of the region in each axis defined by the
	 * X, Y, and Z components of a given vector
	 * @param xyz The vector representing the size of the region
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setXYZ(Vector xyz) {
    	setX(xyz.getX());
    	setY(xyz.getY());
    	setZ(xyz.getZ());
    	return this;
	}

	/**
	 * @param rotation The rotation in degrees
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setRotation(double rotation) {
		this.rotation = new NumProvider(rotation);
		return this;
	}

	/**
	 * Set the rotation provider directly
	 * @param rotation The rotation in degrees
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setRotation(NumProvider rotation) {
		this.rotation = new NumProvider(rotation);
		return this;
	}


	/**
	 * @param about The vector about which to rotate
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setAbout(Vector about) {
		this.about = new VectorProvider(about);
		return this;
	}

	/**
	 * Set the about vector provider directly
	 * @param about The vector about which to rotate
	 * @return The same particle effect
	 */
	public CuboidParticleEffect setAbout(VectorProvider about) {
		this.about = new VectorProvider(about);
		return this;
	}

	/*=============================*/
	/*        Serialization        */
	/*=============================*/

	public void writeToConfig(ConfigurationSection section, String path) {
		super.writeToConfig(section, path);

		x.writeToConfig(section, path + ".xSize");
		y.writeToConfig(section, path + ".ySize");
		z.writeToConfig(section, path + ".zSize");

		about.writeToConfig(section, path + ".about");
		rotation.writeToConfig(section, path + ".rotation");
	}
}
