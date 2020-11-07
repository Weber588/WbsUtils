package wbs.utils.util.particles;

import org.bukkit.util.Vector;

import wbs.utils.util.WbsMath;

public class CuboidParticleEffect extends VelocityParticleEffect {

	public CuboidParticleEffect() {
		
	}
	
	private double x = 1, y = 1, z = 1;
	private double rotation = 0;
	private Vector about = upVector;

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
	public CuboidParticleEffect build() {
		points.clear();
		
		/*
		 *  Seed vertices: A+X+Y+Z, B+X-Y-Z, C-X+Y-Z, D-X-Y+Z
		 *  These vertices do not connect to each other, so 4 vertices with 3 edges
		 *  each is the whole cuboid
		 */
		Vector start = new Vector(x, y, z);
		
		Vector finish = new Vector(-x, y, z);
		
		double scaledX, scaledY, scaledZ; // Need to go size/2 away from the origin in each direction
		scaledX = x / 2;
		scaledY = y / 2;
		scaledZ = z / 2;
		
		double[] signs = {1, 1, 1};
		Vector rotatedStart, rotatedFinish;
		
		for (int i = 0; i < 4; i++) {
			start.setX(scaledX*signs[0]);
			start.setY(scaledY*signs[1]);
			start.setZ(scaledZ*signs[2]);
			
			rotatedStart = WbsMath.rotateVector(start, about, rotation);
			
			for (int j = 0; j < 3; j++) {
				signs[j] = -signs[j];
				finish.setX(scaledX*signs[0]);
				finish.setY(scaledY*signs[1]);
				finish.setZ(scaledZ*signs[2]);

				rotatedFinish = WbsMath.rotateVector(finish, about, rotation);
				
				points.addAll(WbsMath.getLine(amount, rotatedStart, rotatedFinish));
				
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
		/*
		// Vertex A
		start = new Vector(x, y, z);
		
		finish = new Vector(-x, y, z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(x, -y, z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(x, y, -z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		// Vertex B
		start = new Vector(-x, y, -z);
		
		finish = new Vector(x, y, -z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(-x, -y, -z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(-x, y, z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));
		
		// Vertex C
		start = new Vector(-x, -y, z);
		
		finish = new Vector(x, -y, z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(-x, y, z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(-x, -y, -z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));
		
		// Vertex D
		start = new Vector(x, -y, -z);
		
		finish = new Vector(-x, -y, -z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(x, y, -z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));

		finish = new Vector(x, -y, z);
		points.addAll(WbsMath.getLine(pointsInLine, start, finish));
		
		*/
		
		return this;
	}

	public CuboidParticleEffect setSpeed(double speed) {
		this.speed = speed;
		return this;
	}

	public CuboidParticleEffect setX(double x) {
		this.x = x;
		return this;
	}
	public CuboidParticleEffect setY(double y) {
		this.y = y;
		return this;
	}
	public CuboidParticleEffect setZ(double z) {
		this.z = z;
		return this;
	}

	public CuboidParticleEffect setRotation(double rotation) {
		this.rotation = rotation;
		return this;
	}
	public CuboidParticleEffect setAbout(Vector about) {
		this.about = about;
		return this;
	}
}
