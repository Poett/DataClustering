package kmeans;

public class Point {

	public double[] cords;
	public final int dimension;
	
	public Point(double[] cords) 
	{
		dimension = cords.length;
		this.cords = new double[cords.length];
		
		for(int i = 0; i < cords.length; i++) 
		{
			this.cords[i] = cords[i];
		}
	}
	
	public void setcords(double[] cords) 
	{
		this.cords = cords;
	}
	
	public int getDimension() 
	{
		return cords.length;
	}
	
	public static double squaredDistance(Point a, Point b)
	{
		if(a.getDimension() != b.getDimension()) {return -1;}
		
		double difference;
		double distance = 0;
		
		for(int i = 0; i < a.getDimension(); i++) 
		{
			difference = b.cords[i] - a.cords[i];
			distance += (difference * difference);
		}
		
		
		return distance;
	}
}
