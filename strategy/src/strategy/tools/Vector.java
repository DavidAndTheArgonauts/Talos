package strategy.tools;

public class Vector
{

	private double vectorX;
	private double vectorY;

	public Vector (double x1, double y1, double x2, double y2)
	{
		vectorX = x2 - x1;
		vectorY = y2 - y1;
	}

	public Vector (double X, double Y)
	{
		vectorX = X;
		vectorY = Y;
	}

	public double size()
	{
		return Math.sqrt((vectorX*vectorX) + (vectorY*vectorY));
	}

	public static double size(Vector vec)
	{
		return Math.sqrt((vec.getX()*vec.getX()) + (vec.getY()*vec.getY()));
	}

	public double getX()
	{
		return vectorX;
	}

	public double getY()
	{
		return vectorY;
	}

	public static double angleVectors(Vector vec1, Vector vec2)
	{

		double angle1 = Math.atan2(vec1.getY(),
				vec1.getX());
		double angle2 = Math.atan2(vec2.getY(),
				vec2.getX());
		return angle1-angle2;
	}

	/***
	 * Given a start point and an end point, find the point that is distance away behind the start point on 
	 * the projection of the vector. Vector is defined as startVec->targetVec.
	 * @param startVec Start point.
	 * @param targetVec End point.
	 * @param distance Distance away from start point on vector.
	 * @return goalVec Goal coordinates that distance away from start point.
	 */
	public static Vector calculateProjection(Vector startVec, Vector targetVec, double distance )
	{
		double goalX;
		double goalY;

		// Calculate the vector from vec1 to vec2
		Vector vector =  new Vector(targetVec.getX(), targetVec.getY(), startVec.getX(), startVec.getY());

		// Normalising to unit vector
		double i = vector.size();

		double unitVectorX = vector.getX()/i;
		double unitVectorY = vector.getY()/i;

		// Calculate destination by adding multiples of the unit vector
		// Handling out of bounds

		goalX = (startVec.getX() + (distance*unitVectorX));	
		goalY = (startVec.getY() + (distance*unitVectorY));

		Vector goalVec = new Vector(goalX, goalY);

		return goalVec;	
	}

	
	/**
	 * Method to find the closest point on a given vector. The vector is projected out to infinity
	 * irrespective of the given true world end points on the line
	 * @param vec1 point one on the line
	 * @param vec2 point two on the line
	 * @param targetVector the point on the orthogonal projection of the line
	 * @return
	 */
	public static Vector closestPoint(Vector vec1, Vector vec2, Vector targetVector)
	{

		Vector deltaVector =  new Vector(vec2.getX(), vec2.getY(), vec1.getX(), vec1.getY());

		if ((deltaVector.getX() == 0) && (deltaVector.getY() == 0)) 
		{
			throw new IllegalArgumentException("p1 and p2 cannot be the same point");
		}

		double u = ((targetVector.getX() - vec1.getX()) * deltaVector.getX() + (targetVector.getY() - vec1.getY()) * deltaVector.getY())
					/ (deltaVector.getX() * deltaVector.getX() + deltaVector.getY() * deltaVector.getY());
		
		Vector closestPoint = new Vector(vec1.getX() + u * deltaVector.getX(), vec1.getY() + u * deltaVector.getY());

		return closestPoint;

	}


	public static double distPointLine(Vector vec1, Vector vec2, Vector targetVector)
	{
		Vector point = closestPoint(vec1, vec2, targetVector);
		Vector targetPoint = new Vector(point.getX(),point.getY(),targetVector.getX(),targetVector.getY());
		return targetPoint.size();
	}
	
	public String print()
	{
		return ("X: " + getX() + ", Y: " + getY());
	}
	    
}
