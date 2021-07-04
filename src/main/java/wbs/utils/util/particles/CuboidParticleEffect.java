package wbs.utils.util.particles;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import wbs.utils.util.WbsMath;
import wbs.utils.util.configuration.NumProvider;
import wbs.utils.util.configuration.VectorProvider;
import wbs.utils.util.configuration.WbsConfigReader;
import wbs.utils.util.plugin.WbsSettings;

public class CuboidParticleEffect extends VelocityParticleEffect {

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

    public CuboidParticleEffect(ConfigurationSection section, WbsSettings settings, String directory) {
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

	public CuboidParticleEffect setScaleAmount(boolean scaleAmount) {
		this.scaleAmount = scaleAmount;
		return this;
	}

	public CuboidParticleEffect setSpeed(double speed) {
		this.speed = new NumProvider(speed);
		return this;
	}

	public CuboidParticleEffect setX(double x) {
		this.x = new NumProvider(x);
		return this;
	}
	public CuboidParticleEffect setY(double y) {
		this.y = new NumProvider(y);
		return this;
	}
	public CuboidParticleEffect setZ(double z) {
		this.z = new NumProvider(z);
		return this;
	}

	public CuboidParticleEffect setXYZ(double size) {
    	setX(size);
    	setY(size);
    	setZ(size);
    	return this;
	}

	public CuboidParticleEffect setXYZ(Vector xyz) {
    	setX(xyz.getX());
    	setY(xyz.getY());
    	setZ(xyz.getZ());
    	return this;
	}

	public CuboidParticleEffect setRotation(double rotation) {
		this.rotation = new NumProvider(rotation);
		return this;
	}
	public CuboidParticleEffect setAbout(Vector about) {
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
