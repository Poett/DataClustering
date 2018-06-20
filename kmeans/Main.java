package kmeans;

import java.io.IOException;

public class Main {

	
	static int k = 1;
	static String filename = "ecoli.txt";
	static int maxIterations = 10;
	static double threshold = 0.001;
	static int runs = 1;
	
	public static void main(String[] args) {
		
		
		try {	
			
			process(args);	
			Loader l = new Loader(filename);
			Data data = l.getData();
			data.clusterData(k, maxIterations, threshold, runs);
			System.out.println("Finished clustering.");
			
		} catch (ArrayIndexOutOfBoundsException | IOException e) {
			printUsage();
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}


	private static void process(String[] args) throws ArrayIndexOutOfBoundsException
	{
		if(args.length != 5) {printUsage();}
		
		filename = args[0];
		if(!filename.endsWith(".txt")) {printUsage();}
		
		k = Integer.parseInt(args[1]);
		maxIterations = Integer.parseInt(args[2]);
		threshold = Double.parseDouble(args[3]);
		runs = Integer.parseInt(args[4]);
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
