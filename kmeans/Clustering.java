package kmeans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;



public class Clustering {

	//protected ArrayList<Cluster> clusters;
	protected Cluster[] clusters;
	protected int dimension;
	protected String data_name;
	protected Point[] points;
	
	//validation
	protected int true_k;
	
	//enums
	protected Initializer initializer = Initializer.RandomInitial;
	protected ExternalIndexer indexer = null;
	protected InternalValidator validator = null;
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	
	public Clustering(String filename) throws IOException
	{
		data_name = filename.substring(0, (filename.length()-4)); //Grab data name from filename without '.txt'
		
		//open file for reading
		File file = new File("kmeans/input/" + filename);
		BufferedReader in = new BufferedReader(new FileReader(file));

		//Grab the first line of our data files, contains number of elements and point dimensions and true clusters
		String line = in.readLine();
		//if first line was null, close and exit
		if(line == null) {in.close(); return;}

		//Split first line into tokens
		int numOfPoints;
		String[] tokens;
		
		tokens = line.trim().split("[\t ]+");
		numOfPoints = Integer.parseInt(tokens[0]);
		dimension = Integer.parseInt(tokens[1]) - 1;
		true_k = Integer.parseInt(tokens[2]);

		//Start up a container for the points and data
		points = new Point[numOfPoints];
		ExternalIndexer.clustering = new int[numOfPoints];
		ExternalIndexer.validation = new int[numOfPoints];
		double[] p;

		//loop through the text file
		for(int i = 0; i < numOfPoints; i++) 
		{
			//read one line which is one point; tokenize
			line = in.readLine();
			tokens = line.trim().split("[\t ]+");
			p = new double[dimension];

			int j;
			//loop through each token and assign coordinate points
			for(j = 0; j < p.length;j++) {p[j] = Double.parseDouble(tokens[j]);}


			points[i] = new Point(p);
			
			ExternalIndexer.validation[i] = Integer.parseInt(tokens[j]);


		}



		//close file
		in.close();

	}
	
	
	////////////////////
	//Public Methods
	////////////////////
	
	
	public Cluster[] clusterData(int k, int maxIterations, double threshold, int runs) throws IOException
	{
		
		//'(initializer) Name'
		String output = "(" + initializer.toString() + ") " + data_name;
		
		if(indexer != null) {output = output + " " + indexer.name();}
		
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("kmeans/output/" + output + ".txt")));

		Cluster[] bestClusters = null;
		double bestInitialSSE = 0;
		double tempInitialSSE = 0;
		double bestFinalSSE = Double.MAX_VALUE;
		int bestNumOfIterations = maxIterations;
		double bestIndex = Double.MIN_VALUE;
		
		double index = 0;
		double sse = 0;
		double prevSSE;
		double deltaSSE;
		int j;

		System.out.print("Run ");
		
		//Do the number of runs
		for(int i = 0; i < runs; i++) 
		{
			
			
			System.out.print("... " + (i + 1));
			
			sse = initializer.initialize(this, k); //initializes the cluster and returns the sse
			
			tempInitialSSE = sse;
			deltaSSE = threshold + 1; //initializes it larger than threshold
			j = 0;

			
			//Do max iterations or until threshold has met
			while( (j < maxIterations) && (deltaSSE > threshold) ) 
			{
				
				//Find new centroids
				prevSSE = sse;
				sse = minimize(); //get new SSE
				deltaSSE = (prevSSE - sse)/prevSSE; //Find delta SSE
				//iterate
				j++;
			}
					

			//Save best run information
			if(indexer != null) 
			{
				index = indexer.index(this);
				if(index > bestIndex) 
				{
					bestIndex = index;
					bestNumOfIterations = j;
					bestFinalSSE = sse;
					bestClusters = this.clusters;
				}
			}
			else if(sse < bestFinalSSE) 
			{
				bestNumOfIterations = j;
				bestInitialSSE = tempInitialSSE;
				bestFinalSSE = sse;
				bestClusters = this.clusters;
			}
		}
		System.out.println();
		
		writer.write("\t" + data_name + "\r");
		
		if(indexer != null) {writer.write("Index:\t" + df.format(bestIndex) + "\r");}

		writer.write("SSE:\t" + bestFinalSSE + "\r");
		writer.close();
		
		
		//Set final data and return best
		clusters = bestClusters;
		return bestClusters;

	}
	
	public Cluster[] clusterData(int maxIterations, double threshold, int runs) throws IOException
	{
		return clusterData(true_k, maxIterations, threshold, runs);
	}
	
	
	public void setInitializer(Initializer initializer) {this.initializer = initializer;}
	
	public void setExternalValidation(ExternalIndexer indexer) {this.indexer = indexer;}
	
	public void setInternalValidation(InternalValidator validator) {this.validator = validator;}
	
	/////////////////
	//Private Methods
	/////////////////
	
	private Cluster getCluster(Point p) 
	{
		for(Cluster c : clusters) 
		{
			if(c.hasPoint(p)) {return c;}
		}
		
		return null;
	}
	
	private Cluster getNeighbor(Point p) 
	{
		
		double lowest;
		Cluster neighbor = null;
		Cluster closest;
		double temp;

		
		closest = clusters[0];
		lowest = Double.MAX_VALUE;
		
		for(int i = 1; i < clusters.length; i++) 
		{
			temp = Point.squaredDistance(p, clusters[i].getCentroid());
			if(temp < lowest) {lowest = temp; neighbor = closest; closest = clusters[i];}
		}
		
		return neighbor;
	}
	
	private double getSSE() 
	{
		double sum = 0;
		for(Cluster c : clusters) 
		{
			sum += c.getSSE();
		}
		return sum;
	}
	
	private double assignPoints() 
	{
		double lowest;
		Cluster closest;
		double temp;
		int indexOf = 0;
		
		for(int i = 0; i < points.length; i++) 
		{		
			lowest = Double.MAX_VALUE;
			closest = null;
			
			for(int j = 0; j < clusters.length; j++) 
			{
				temp = Point.squaredDistance(points[i], clusters[j].getCentroid());
				if (temp < lowest) {lowest = temp; indexOf = j;} //If new closest cluster is found, update
			}
			
			closest = clusters[indexOf];
			closest.addPoint(points[i]); //After all clusters checked, add Point p to closest
			ExternalIndexer.clustering[i] = indexOf; //Add cluster label for validation
		}
		
		
		return getSSE();
	}
	
	private double calculateBGSS() 
	{
		double BGSS = 0;
		
		//Calculate the average of all data
		double[] average = new double[dimension];
		for(int i = 0; i < dimension; i++) 
		{
			double sum = 0;
			
			for(Point p : points) 
			{
				sum += p.cords[i];
			}
			
			average[i] = (sum/points.length);
			
		}
		
		Point a = new Point(average);
		
		for(Cluster c : clusters) 
		{
			BGSS += c.size() * (Point.squaredDistance(a, c.getCentroid()));
		}
		
		return BGSS;
	}
	
	private double minimize() 
	{
		
		for(Cluster c : clusters) 
		{
			c.setCentroid(c.getMean());
			c.clear();
		}
		
		return assignPoints();
		
	}


	//////////
	//Enums
	//////////
	
	//Validation Enums
	public enum ExternalIndexer{
		Jaccard
		{
			public double index(Clustering c) 
			{
				calc(c.points, c.clusters, c.true_k);
				return (double)tp/(tp + fn + fp);
				
			}
		}, 
		Rand
		{
			public double index(Clustering c) 
			{
				calc(c.points, c.clusters, c.true_k);
				return (double)(tp + tn)/n;
				
			}
		};
		
		private static int tp;
		private static int fn;
		private static int fp; 
		private static int tn;
		private static int n;
		protected static int[] clustering;
		protected static int[] validation;
		
		public abstract double index(Clustering c);
		private static void calc(Point[] points, Cluster[] clusters, int true_k) 
		{
			
			n = (int) (((double) points.length/2) * (points.length-1));

			int squaredSum = 0;
			int squaredSumClusters = 0;
			int squaredSumPartitions = 0;
			
			int[][] N = new int[clusters.length][true_k]; //all values initialized as 0
			int[] m = new int[true_k]; //array that holds number of points in each partition
			
			//Loop through all the validity labels and increment corresponding index in N
			for(int i = 0; i < points.length; i++) 
			{
				N[clustering[i]][validation[i]]++;
				m[validation[i]]++; //incremement the corresponding partition's number of elements
			}
			
			//Get the squaredSumPartitions for False Negatives
			for(int i : m) {squaredSumPartitions += i * i;}
			
			//Nested loops, loops for each cluster and each partition
			for(int i = 0; i < clusters.length; i++) 
			{
				
				squaredSumClusters += clusters[i].getPoints().size() * clusters[i].getPoints().size();
						
				for(int j = 0; j < true_k; j++) 
				{
					squaredSum = squaredSum + (N[i][j] * N[i][j]);
				}
				
				
			}
			
			//Calculate the pairs and store into return packet
			
			tp = (squaredSum - points.length) / 2;
			
			fn = (squaredSumPartitions - squaredSum)/2;
			fp = (squaredSumClusters - squaredSum)/2;
			tp = n - (tp + fn + fp);
		}

	}
	public enum InternalValidator{
		Silhouette{

			public double index(Clustering c) {
				double a = 0;
				double b = 0;
				double sum = 0;
				
				for(int i = 0; i < c.points.length; i++) 
				{
					Point p = c.points[i];
					
					//Get the a and b clusters
					Cluster neighbor = c.getNeighbor(p);
					Cluster cluster = c.getCluster(p);
					
					//Calculate the total error in reference to point P
					for(Point v : cluster.getPoints()) {a = a + Point.squaredDistance(p, v);}
					for(Point v : neighbor.getPoints()) {b = b + Point.squaredDistance(p, v);}
					
					//Average out error for a and b
					a = (a / cluster.getPoints().size());
					b = (b / neighbor.getPoints().size());
					
					//Add the silhouette for P in silhouettes
					sum += ( (b-a) / Math.max(a, b) );
				}

				
				return (sum / c.points.length);
			}},
		CalinskiHarabasz{

			public double index(Clustering c) {
				double WGSS = c.getSSE();
				double BGSS = c.calculateBGSS();

				return ( (c.points.length - c.clusters.length ) / (c.clusters.length - 1) ) * ( (BGSS) / (WGSS) );
			}};

		public abstract double index(Clustering c);
	}

	//Initializer Enums
	public enum Initializer{
		RandomInitial{
			public double initialize(Clustering c, int k)
			{
				c.clusters = new Cluster[k];
				
				//Random initial centroids
				Random rand = new Random();
				int pindex;
				for(int i = 0; i < k; i++) 
				{
					//index of a random point in existing points
					pindex = rand.nextInt(c.points.length);

					//set random point as centroid
					c.clusters[i] = new Cluster(c.dimension, c.points[pindex]);
				}

				//Assign initial points
				return c.assignPoints();
			}
		}, 
		RandomPartition{
			public double initialize(Clustering c, int k) 
			{
				//Make a new array for clusters
				c.clusters = new Cluster[k]; //clusters = new ArrayList<>(k);
				Point temp = c.points[0];
				
				for(int i = 0; i < k; i++) 
				{
					c.clusters[i] = new Cluster(c.dimension, temp);
				}


				//Assign points to random cluster
				Random rand = new Random();
				int cindex;
				for(Point p : c.points) 
				{
					cindex = rand.nextInt(k);
					
					c.clusters[cindex].addPoint(p);
				}
				
				
				//Recalculate the the centroids and return the SSE
				
				
				double SSE = 0;
				for(Cluster cl : c.clusters) 
				{
					cl.setCentroid(cl.getMean());
					
					SSE += cl.getSSE();
				}
				return SSE;
			}
		};

		public abstract double initialize(Clustering c, int k);

	}

}
