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
	
	
	public static Vector calculateTarget(Vector vec1, Vector vec2, double distance )
	{
		double targetX;
		double targetY;

		// Calculate the vector from vec1 to vec2
		Vector vector =  new Vector(vec1.getX(), vec1.getY(), vec2.getX(), vec2.getY());

		// Normalising to unit vector
		double i = vector.size();

		double unitVectorX = vector.getX()/i;
		double unitVectorY = vector.getY()/i;

		// Calculate destination by adding multiples of the unit vector
		// Handling out of bounds
			
		targetX = (vec2.getX() + (distance*unitVectorX));	
		targetY = (vec2.getY() + (distance*unitVectorY));
			
		Vector targetVec = new Vector(targetX, targetY);
		
		return targetVec;	
	}
	
	public static Vector closestPoint(Vector vec1, Vector vec2, Vector targetVector)
	{

		Vector deltaVector =  new Vector(vec1.getX(), vec1.getY(), vec2.getX(), vec2.getY());

		if ((deltaVector.getX() == 0) && (deltaVector.getY() == 0)) 
		{
			throw new IllegalArgumentException("p1 and p2 cannot be the same point");
		}

		double u = ((targetVector.getX() - vec1.getX()) * deltaVector.getX() + (targetVector.getY() - vec1.getY()) * deltaVector.getY()) / (deltaVector.getX() * deltaVector.getX() + deltaVector.getY() * deltaVector.getY());

		Vector closestPoint;
		
		if (u < 0) 
		{
			closestPoint = vec1;
		}
		else if (u > 1) 
		{
			closestPoint = vec2;
		} 
		else
		{
			closestPoint = new Vector(vec1.getX() + u * deltaVector.getX(), vec2.getY() + u * deltaVector.getY());
		}

		return closestPoint;
		
	}
	

	public static double distPointLine(Vector vec1, Vector vec2, Vector targetVector)
	{
		Vector point = closestPoint(vec1, vec2, targetVector);
		return point.size();
	}
	
	public void print()
	{
		System.out.println("X: " + getX() + ", Y: " + getY());
	}

	    
}
