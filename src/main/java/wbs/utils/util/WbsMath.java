package wbs.utils.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class WbsMath {
	private WbsMath() {}

	/*==========*/
	// Misc
	/*==========*/
	
	/**
	 * Round the given double to specified amount of decimal places.
	 * Possibly undefined behaviour at large numbers rounded to many
	 * decimal places.
	 * @param number The number to round
	 * @param decimalPlaces The amount of decimal places to round to
	 * @return The rounded double
	 */
	public static double roundTo(double number, int decimalPlaces) {
		return Math.round(number * (Math.pow(10, decimalPlaces)))/Math.pow(10, decimalPlaces);
	}
	
	/**
	 * Returns true with a chance of a specified percentage
	 * @param percent The percent chance of success
	 * @return True with a percent% chance.
	 */
	public static boolean chance(double percent) {
		if (percent <= 0) {
			return false;
		} else if (percent >= 100) {
			return true;
		} else {
			return (Math.random() < percent/100);
		}
	}
	
	/*==========*/
	// Geometry
	/*==========*/
	
	/**
	 * Rotate a list of Vectors based on a vector.
	 * This method assumes the given vectors are populated around the "from"
	 * and rotates from that vector to the given "with" vector.
	 * @param toRotate The vectors to rotate
	 * @param with The vector of rotation
	 * @return A copy of toRotate, rotated.
	 */
	public static List<Vector> rotateFrom(List<Vector> toRotate, Vector from, Vector with) {
		Vector aboutVector = with.clone().crossProduct(from);
		
		double angle = from.angle(with);
		
		return rotateVectors(toRotate, aboutVector, Math.toDegrees(angle));
	}

	public static Vector rotateFrom(Vector toRotate, Vector from, Vector with) {
		Vector aboutVector = with.clone().crossProduct(from);

		double angle = from.angle(with);

		return rotateVector(toRotate, aboutVector, Math.toDegrees(angle));
	}
	
	public static List<Vector> rotateVectors(List<Vector> toRotate, Vector about, double degrees) {
		ArrayList<Vector> rotatedList = new ArrayList<>();
		if (degrees == 0) {
			return toRotate;
		}
		
		double theta = Math.toRadians(degrees);
		
		/*
		 *  Implementing Rodrigues' Rotation Formula
		 *  v_rot = vcos(theta) + (k x v)sin(theta) + k(k . v)(1 - cos(theta))
		 *  where v is the vector to rotate, k is the unit vector about which to rotate.
		 *  Note that x here denotes cross product and . denotes scalar product.
		 *  
		 *  In this case, v = rotatedVector, k = about
		 */
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		Vector aboutUnit = about.clone().normalize(); // k
		Vector component1, component2, component3; // The three terms to be added
		
		for (Vector rotatedVector : toRotate) {
			component1 = rotatedVector.clone().multiply(cosTheta);
			component2 = aboutUnit.clone().crossProduct(rotatedVector).multiply(sinTheta);
			component3 = aboutUnit.clone().multiply(aboutUnit.dot(rotatedVector)).multiply(1 - cosTheta);

			rotatedVector = component1.add(component2).add(component3);
			
			rotatedList.add(rotatedVector);
		}

		return rotatedList;
	}
	
	public static Vector rotateVector(Vector toRotate, Vector about, double degrees) {
		Vector rotated;
		if (degrees == 0) {
			return toRotate.clone();
		}
		
		double theta = Math.toRadians(degrees);
		
		/*
		 *  Implementing Rodrigues' Rotation Formula
		 *  v_rot = vcos(theta) 
		 *  		+ (k x v)sin(theta) 
		 *  		+ k(k . v)(1 - cos(theta))
		 *  where v is the vector to rotate, k is the unit vector about which to rotate.
		 *  Note that x here denotes cross product and . denotes scalar product.
		 */

		Vector aboutUnit = about.clone().normalize(); // k
		rotated = toRotate.clone(); // v
		
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		
		Vector component1, component2, component3; // The three terms to be added
		
		component1 = rotated.clone().multiply(cosTheta);
		component2 = aboutUnit.clone().crossProduct(rotated).multiply(sinTheta);
		component3 = aboutUnit.clone().multiply(aboutUnit.dot(rotated)).multiply(1 - cosTheta);
		
		rotated = component1;
		rotated.add(component2);
		rotated.add(component3);
		
		return rotated;
	}
	
	private static final Vector origin = new Vector(0, 0, 0);
	
	/**
	 * Get a list of points in a line between two vectors
	 * evenly distributed based on the amount of points.
	 * @param pointsInLine The amount of points to generate. Negative values
	 * will generate that many points per unit.
	 * @param start The starting position
	 * @param finish The finishing position
	 * @return The list of points
	 */
	public static ArrayList<Vector> getLine(int pointsInLine, Vector start, Vector finish) {
		ArrayList<Vector> points = new ArrayList<>();

		if (pointsInLine < 0) {
			pointsInLine = (int) Math.abs(pointsInLine * finish.length());
		}
		
		Vector startToFinish = finish.clone().subtract(start);
		Vector direction = scaleVector(startToFinish, startToFinish.length() / pointsInLine);

		Vector step = start.clone();
		points.add(step);
		for (int i = 0; i < pointsInLine; i++) {
			step = step.clone().add(direction);
			points.add(step);
		}

		return points;
	}
	
	/**
	 * Get a list of points in a line, starting at the origin, and
	 * ending at finish, evenly distributed based on the amount of
	 * points.
	 * @param pointsInLine The amount of points to generate. Negative values
	 * will generate that many points per unit.
	 * @param finish The end of the line
	 * @return The points in the line, as vectors from the origin.
	 * The first point is always the origin.
	 */
	public static ArrayList<Vector> getLine(int pointsInLine, Vector finish) {
		ArrayList<Vector> points = new ArrayList<>();

		if (pointsInLine < 0) {
			pointsInLine = (int) Math.abs(pointsInLine * finish.length());
		}
		
		Vector startToFinish = (finish.clone());
		Vector direction = scaleVector(startToFinish, finish.length() / pointsInLine);
		

		Vector step = origin.clone();
		points.add(step);
		for (int i = 0; i < pointsInLine; i++) {
			step = step.clone().add(direction);
			points.add(step);
		}
		
		return points;
	}

	public static Vector getRandomPointOn2Disc(double radius) {
		double theta = Math.random() * 2 * Math.PI;
		double rand = Math.random() * radius*radius;
		
		double x, z;
		
		x = Math.sqrt(rand) * Math.cos(theta);
		z = Math.sqrt(rand) * Math.sin(theta);

		return new Vector(x, 0, z);
	}
	
	
	public static Location getRandomPointOn2Disc(Location loc, double radius) {
		return loc.clone().add(getRandomPointOn2Disc(radius));
	}
	
	/**
	 * Translate a list of vectors to a new origin,
	 * and return it as a list of locations about that
	 * origin.
	 * @param origin The origin about which the vectors define locations
	 * @param vectors The set of vectors to translate and
	 * turn into Locations.
	 * @return The set of locations about the new origin.
	 */
	public static ArrayList<Location> offsetPoints(Location origin, ArrayList<Vector> vectors) {
		ArrayList<Location> locations = new ArrayList<>();
		
		for (Vector vec : vectors) {
			locations.add(origin.clone().add(vec));
		}
		
		return locations;
	}

	public static final double PHI = Math.PI * (3. - Math.sqrt(5));

	public static ArrayList<Vector> getFibonacciSphere(int amount, double radius) {
		ArrayList<Vector> points = new ArrayList<>();

		Vector offset = new Vector(0, -radius, 0);

		for (int i = 0; i < amount; i++) {
			double y = 1 - (i / ((float)(amount - 1))) * 2;
			double tempRadius = Math.sqrt(1 - y * y);

			double theta = PHI * i;

			double x = Math.cos(theta) * tempRadius;
			double z = Math.sin(theta) * tempRadius;

			points.add(new Vector(x, y + 1, z).multiply(radius).add(offset));
		}

		return points;
	}

	public static Vector reflectVector(Vector vector, Vector normal) {
		vector = vector.clone();
		normal = normal.clone();

		return vector.subtract(normal.clone().multiply(2).multiply(vector.dot(normal)));
	}

	/**
	 * Get points in a ring in the X-Z plane
	 * @param n The number of points in the ring
	 * @param radius The radius of the ring
	 * @param rotation The rotation in degrees
	 * @return An ArrayList containing all points in the ring
	 */
	public static ArrayList<Vector> get2Ring(int n, double radius, double rotation) {
		ArrayList<Vector> points = new ArrayList<>();
		if (n <= 0) {
			return points;
		}
		rotation = Math.toRadians(rotation);
		
		Vector addVec;
		double x, z, theta = rotation;
		double angle = 2* Math.PI / n;
		for (int i = 0; i < n; i++) {
			x = (radius * Math.cos(theta));
			z = (radius * Math.sin(theta));

			addVec = new Vector(x, 0, z);
			points.add(addVec);
			
			theta += angle;
		}
		
		return points;
	}
	
	/**
	 * Get the points in a ring around the given vector.
	 * @param n The number of points in the ring
	 * @param radius The radius of the ring
	 * @param about The vector about which to draw the ring
	 * @param rotation The rotation in degrees
	 * @return An ArrayList containing all points in the ring
	 */
	public static ArrayList<Vector> get3Ring(int n, double radius, Vector about, double rotation) {
		ArrayList<Vector> points = new ArrayList<>();
		if (n <= 0) {
			return points;
		}
		
		rotation = Math.toRadians(rotation);
		
		double x, y, z, theta = rotation;
		double angle = (2* Math.PI / n);
		
		double cx,cz;
		cx = about.getX();
		cz = about.getZ();
		
		Vector v = about.clone().normalize();

		double ax,ay,az;
		ax = cz;
		ay = 0;
		az = -cx;
		
		Vector a = new Vector(ax, ay, az); // Create a perpendicular vector to v
		a.normalize(); // as a unit vector
		Vector b = a.clone().crossProduct(v); // Get the vector perpendicular to v and a (to define a plane with a)
		b.normalize(); // as a unit vector

		Vector addVec;
		
		for (int i = 0; i < n; i++) {
			
			double cosAngle = Math.cos(theta);
			double sinAngle = Math.sin(theta);
			
			x = (radius*cosAngle*a.getX()) + (radius*sinAngle*b.getX());
			y = (radius*cosAngle*a.getY()) + (radius*sinAngle*b.getY());
			z = (radius*cosAngle*a.getZ()) + (radius*sinAngle*b.getZ());
			
			addVec = new Vector(x, y, z);
			points.add(addVec);
			
			theta = (theta + angle) % (2*Math.PI);
			
		}
		return points;
	}

	public static ArrayList<Vector> getRandom2Disc(int n, double radius) {
		ArrayList<Vector> points = new ArrayList<>();
		if (n <= 0) {
			return points;
		}
		
		for (int i = 0; i < n; i++) {
			points.add(getRandomPointOn2Disc(radius));
		}
		
		return points;
	}
	
	/**
	 * Get a disc filled in by concentric circles in the X-Z plane.
	 * @param n The amount of points on the outer ring (also informs
	 * point density on the discs surface)
	 * @param radius The radius of the disc
	 * @param rotation The rotation of the disc in degrees around the
	 * Y axis
	 * @return A disc of points
	 */
	public static ArrayList<Vector> get2Disc(int n, double radius, double rotation) {
		ArrayList<Vector> points = new ArrayList<>();
		if (n <= 0) {
			return points;
		}
		
		// The distance between each point on the ring
		double segmentLength = 2 * Math.PI * radius / n;
		segmentLength /= 2; // Makes it a bit more dense

		double currentRadius = radius;
		
		while (currentRadius > 0) {
			// How many points distributed with the same segment size
			n = (int) (currentRadius * 2 * Math.PI / segmentLength); 
			
			points.addAll(get2Ring(n, currentRadius, rotation));

			currentRadius -= segmentLength;
		}
		
		return points;
	}
	
	/**
	 * Get a disc filled in by concentric circles in 3 space
	 * @param n The amount of points on the outer ring (also informs
	 * point density on the discs surface)
	 * @param radius The radius of the disc
	 * @param about The vector about which to draw the disc
	 * @param rotation The rotation of the disc in degrees around the about axis
	 * @return A disc of points
	 */
	public static ArrayList<Vector> get3Disc(int n, double radius, Vector about, double rotation) {
		double radiusReduction = radius / n / (2 * Math.PI);
		ArrayList<Vector> points = new ArrayList<>();

		double currentRadius = radius;
		
		while (currentRadius > 0) {
			currentRadius -= radiusReduction;
			points.addAll(get3Ring(n, currentRadius, about, rotation));
		}
		
		return points;
	}

	/*==========*/
	// Vectors
	/*==========*/
	
	/**
	 * Gets a unit vector in the direction the specified entity is facing.
	 * @param entity The entity to get the facing vector from
	 * @return The facing vector
	 */
	public static Vector getFacingVector(Entity entity) {
		return getFacingVector(entity, 1);
	}

	/**
	 * Gets a vector in the direction the specified entity is facing.
	 * @param entity The entity to get the facing vector from
	 * @param magnitude The length of the facing vector
	 * @return The facing vector with the specified magnitude
	 */
	public static Vector getFacingVector(Entity entity, double magnitude) {
		double x, y, z;
		double pitch = Math.toRadians(entity.getLocation().getPitch());
		double yaw = Math.toRadians(entity.getLocation().getYaw());

		y = (magnitude * Math.sin(0 - pitch));

		double planeMagnitude = Math.min(magnitude, Math.abs(y / Math.tan(0 - pitch)));

		x = planeMagnitude * Math.cos(yaw + (Math.PI/2));
		z = planeMagnitude * Math.sin(yaw + (Math.PI/2));

		return new Vector(x, y, z);
	}
	
	/**
	 * Get a copy of the given vector scaled to the desired magnitude.
	 * @param original The vector to be cloned and then scaled
	 * @param magnitude The magnitude of the resulting vector
	 * @return A copy of the vector with the desired magnitude
	 */
	public static Vector scaleVector(Vector original, double magnitude) {
		return (original.clone().normalize().multiply(magnitude));
	}

	/**
	 * Gets a random unit vector that may point in any direction
	 * @return The vector with random direction
	 */
	public static Vector randomVector() {
		return randomVector(1);
	}
	
	/**
	 * Gets a random vector that may point in any direction
	 * @param magnitude The magnitude of the resulting vector
	 * @return The vector with random direction
	 */
	public static Vector randomVector(double magnitude) {
		double x, y, z;
		x = 2 * Math.random() - 1;
		y = 2 * Math.random() - 1;
		z = 2 * Math.random() - 1;
		
		double scale = Math.sqrt((x*x) + (y*y) + (z*z)) / magnitude;
		
		x/=scale;
		y/=scale;
		z/=scale;
		
		return new Vector(x, y, z);
	}

	public static int parseIntBetween(String input, int min, int max) throws IllegalArgumentException {
		int returnVal = Integer.parseInt(input);

		if (returnVal > max || returnVal < min) {
			throw new IllegalArgumentException();
		}

		return returnVal;
	}

	public static double parseDoubleBetween(String input, double min, double max) throws IllegalArgumentException {
		double returnVal = Double.parseDouble(input);

		if (returnVal > max || returnVal < min) {
			throw new IllegalArgumentException();
		}

		return returnVal;
	}
}
