package kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Loader {

	private String filename;
	
	
	public Loader(String filename) 
	{
		this.filename = filename;
	}
	
	public Data getData() throws IOException
	{
		Data data;
		
		//open file for reading
		File file = new File("kmeans/" + filename);
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		//Grab the first line of our data files, contains number of elements and point dimensions
		String line = in.readLine();
		int dimension;
		int numOfPoints;
		String[] tokens;
		
		//if first line was null, close and exit
		if(line == null) {in.close(); return null;}
		
		//Split line into tokens
		tokens = line.split(" ");
		numOfPoints = Integer.parseInt(tokens[0]);
		dimension = Integer.parseInt(tokens[1]);
		
		//Start up a container for the points and data
		double[] p;
		data = new Data(filename.substring(0, (filename.length()-4)), dimension);
		
		//loop through the text file
		for(int i = 1; i <= numOfPoints; i++) 
		{
			//read one line which is one point; tokenize
			line = in.readLine();
			tokens = line.split(" ");
			p = new double[dimension];
			
			//loop through each token and assign coordinate points
			for(int j = 0; j < p.length;j++) {p[j] = Double.parseDouble(tokens[j]);}
			
			//add point to data
			data.addPoint(new Point(p));
			
			
		}
		
		
		
		//close file
		in.close();
		
		return data;
		
		
	}
	
	
}
