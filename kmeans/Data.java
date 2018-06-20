package kmeans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Data {

	private ArrayList<Point> points;
	private int dimension;
	private HashMap<Point, HashMap<Point, Double>> clusters;
	private double sse;
	private String data_name;
	
	
	public Data(String name, int dimension) 
	{
		this.data_name = name;
		points = new ArrayList<>();
		this.dimension = dimension;
		sse = 0;
		
	}
	
	
	
	public void addPoint(Point p) 
	{
		if (p.getDimension() != this.dimension) 
		{
			return;
		}
		
		points.add(p);
	}
	
	
	public void clusterData(int k, int maxIterations, double threshold, int runs) throws IOException
	{
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("kmeans/" + data_name + "_output.txt")));
		
		
		
		double prevSSE;
		double deltaSSE;
		int j;
		double bestSSE = -1;
		int bestRun = 0;
		
		//Do the number of runs
		for(int i = 0; i < runs; i++) 
		{
			writer.write("Run " + (i + 1) + "\n"
					+ "-----------------\n");
			
			initialize(k);
			sse = newSSE();
			prevSSE = sse;
			deltaSSE = threshold + 1;
			j = 0;
			
			
			//Do max iterations or until threshold has met
			while( (j < maxIterations) && (deltaSSE > threshold) ) 
			{
				writer.write("Iteration " + (j + 1) + ": SSE = " + sse + "\n");
				//Find new centroids
				minimize();
				//Find delta SSE
				prevSSE = sse;
				sse = newSSE();
				deltaSSE = (prevSSE - sse)/prevSSE;
				
				//iterate
				j++;
			}
			
			if(bestSSE < 0 || sse < bestSSE) 
			{
				bestSSE = sse;
				bestRun = i + 1;
			}
		}
		
		writer.write("\nBest SSE: " + bestSSE + " - Run " + bestRun);
		writer.close();
		
	}
	
	private void initialize(int k) 
	{
		clusters = new HashMap<>(points.size());
		
		//Random initial centroids
		Random rand = new Random();
		int pindex;
		for(int i = 0; i < k; i++) 
		{
			//index of a random point in existing points
			pindex = rand.nextInt(points.size());
			
			//sets keys for initial, empty clusters
			clusters.put(points.get(pindex), new HashMap<>());
		}
		
		//Assign initial points
		assignPoints();
	}
	
	private void assignPoints() 
	{
		
		Set<Point> centroids = clusters.keySet();
		
		//Exit if no clusters assigned
		if (centroids.isEmpty()) return;
		
		//Start iterator and prepare to assign clusters
		Iterator<Point> it;
		Point closest;
		Point itPoint;
		double temp;
		double lowest;
		
		//For each point, calculate the closest centroid
		for(Point p : points) 
		{
			it = centroids.iterator();
			closest = it.next();
			
			//Initialize lowest
			lowest = Point.squaredDistance(p, closest);
			
			//Loop through other centroids to find lowest if possible
			while(it.hasNext()) 
			{
				//Save information while pushing to next centroid
				itPoint = it.next();
				temp = Point.squaredDistance(p, itPoint);
				
				//Change lowest if a new lowest is found
				if(temp < lowest) {lowest = temp; closest = itPoint;}
			}
			
			//Get the cluster's hashmap and put in the point along with squared error
			clusters.get(closest).put(p, lowest);
			
		}
	}
	
	private double newSSE() 
	{
		
		double newSSE = 0;
		
		//For each cluster, calculate the squared error for each of the points in the cluster
		for(Point c : clusters.keySet()) 
		{
			for(Double d : clusters.get(c).values()) 
			{
				newSSE += d.doubleValue();
			}
		}
		
		return newSSE;
	}
	
	private void minimize() 
	{
		ArrayList<Point> minimized = new ArrayList<>();
		double[] cords = new double[dimension];
		double tempSum;
		Iterator<Point> iterator;
		//Go through each point and find the mean location
		for(HashMap<Point, Double> c : clusters.values()) //For each cluster...
		{
			//For each point in the cluster, find the mean per dimension
			
			for(int i = 0; i < dimension; i ++)
			{
				iterator = c.keySet().iterator();
				tempSum = 0;
				while(iterator.hasNext()) 
				{
					//Adds all the cords per dimension
					tempSum += iterator.next().cords[i];
				}
				cords[i] = tempSum / (c.keySet().size());
			}
			
			//Add new point to minimized group
			minimized.add(new Point(cords));

		}
		
		//Recalculate clusters
		clusters.clear();
		for(Point p  : minimized) {clusters.put(p, new HashMap<>());}
		assignPoints();
	
	}
}
