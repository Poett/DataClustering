package kmeans;

import java.util.ArrayList;

public class Cluster {
	
	
	private Point centroid;
	private ArrayList<Point> points;
	private double SSE;
	public final int dimension;
	
	
	public Cluster(int dimension, Point centroid)
	{
		this.dimension = dimension;
		this.centroid = new Point(centroid.cords);
		points = new ArrayList<Point>();
	}
	
	
	public void addPoint(Point p) 
	{
		if(p.dimension != dimension) {return;}
		
		points.add(p);
		SSE = SSE + Point.squaredDistance(centroid, p);
	}
	
	public void removePoint(Point p) 
	{points.remove(p);}
	
	public ArrayList<Point> getPoints()
	{return points;}
	
	public boolean hasPoint(Point p) 
	{
		return points.contains(p);
	}
	
	public Point getCentroid() 
	{return centroid;}
	
	public double getSSE() 
	{return SSE;}
	
	public void clear() 
	{points.clear(); SSE = 0;}
	
	public int size() 
	{return points.size();}
	
	public void setCentroid(double[] cords) 
	{
		this.centroid = new Point(cords);
	}
	
	public double[] getMean() 
	{
		
		double[] newC = new double[dimension];
		
		//Go through all points and find mean location
		for(int i = 0; i < dimension; i++) 
		{
			double sum = 0;
			
			//Add the sum per dimension
			for(Point p : points) 
			{
				sum += p.cords[i];
			}
			
			//find the average and store in new coordinates
			newC[i] = (sum/points.size());
		}
		
		return newC;
		
	}
	
	private double calculateSSE() 
	{
		SSE = 0;
		
		for(Point p : points) 
		{
			SSE += Point.squaredDistance(p, centroid);
		}
		
		return SSE;
	}

}
