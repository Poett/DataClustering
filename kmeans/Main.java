package kmeans;

import java.io.IOException;

import kmeans.Clustering.ExternalIndexer;

public class Main {

	
	static int k;
	static String filename = null;
	static int maxIterations;
	static double threshold;
	static int runs;
	
	public static void main(String[] args) {
		
		try {	
			
			process(args);	

			
			Clustering data = new Clustering(filename);
		
			
			data.setExternalValidation(ExternalIndexer.Jaccard);
			data.clusterData(maxIterations, threshold, runs);
			
			data.setExternalValidation(ExternalIndexer.Rand);
			data.clusterData(maxIterations, threshold, runs);
			
			
			
			
		} catch (ArrayIndexOutOfBoundsException | IOException e) {
			e.printStackTrace();
			printUsage();
			System.exit(0);
		}
		
		
	}


	private static void process(String[] args) throws ArrayIndexOutOfBoundsException
	{
		if(args.length != 5 && args.length != 0 && args.length != 4) {printUsage();}
		int i = 0;
		
		filename = args[i++];
		if(!filename.endsWith(".txt")) {printUsage();}
		
		if(args.length == 5) {
			k = Integer.parseInt(args[i++]);}
		
		maxIterations = Integer.parseInt(args[i++]);
		threshold = Double.parseDouble(args[i++]);
		runs = Integer.parseInt(args[i++]);
	}
	
	private static void printUsage() 
	{
		System.out.println("\n"
				+ "Usage: java kmeans/Main <filename> [%k %i %t %r] \n"
				+ "\n"
				+ "where: \n"
				+ "\t %k \t <K: number of clusters (positive integer greater than one) > \n"
				+ "\t %i \t <I: maximum number of iterations (positive integer)> \n"
				+ "\t %t \t <T: convergence threshold (non-negative real)> \n"
				+ "\t %r \t <R: number of runs (positive integer) > \n");
		
		System.exit(0);
	}
}
