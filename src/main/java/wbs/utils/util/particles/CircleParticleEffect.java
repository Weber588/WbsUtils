package wbs.utils.util.particles;

import org.bukkit.util.Vector;

public abstract class CircleParticleEffect extends VelocityParticleEffect {

	public CircleParticleEffect() {
		
	}
	
	protected Vector about = upVector;
	
	protected double radius = 1;
	protected double rotation = 0;

	protected CircleParticleEffect cloneInto(CircleParticleEffect cloned) {
		super.cloneInto(cloned);
		
		cloned.setAbout(about)
				.setRadius(radius)
				.setRotation(rotation);
		
		return cloned;
	}
	

	/*===============================*/
	/*        GETTERS/SETTERS        */
	/*===============================*/
	
	public double getRadius() {
		return radius;
	}
	public CircleParticleEffect setRadius(double radius) {
		this.radius = radius;
		return this;
	}
	
	public Vector getAbout() {
		return about;
	}
	public CircleParticleEffect setAbout(Vector about) {
		this.about = about;
		return this;
	}
	
	public double getRotation() {
		return rotation;
	}
	/**
	 * @param rotation The rotation in degrees
	 */
	public CircleParticleEffect setRotation(double rotation) {
		this.rotation = rotation;
		return this;
	}
}
