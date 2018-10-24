package kmeans;

import java.io.IOException;

public class NormalClustering extends Clustering{

	
	public NormalClustering(String filename) throws IOException
	{
		super(filename);
		this.data_name = "Normalized_" + data_name;
		normalize();
	}
	

	private void normalize() 
	{
		
		
		//Nested loop - for each coordinate, look for min-max then normalize
		for(int i = 0; i < dimension; i++) 
		{
			
			double lowest = Double.MAX_VALUE;
			double highest = Double.MIN_VALUE;
			
			for(Point p : points) 
			{
				if (p.cords[i] > highest) {highest = p.cords[i];}
				if(p.cords[i] < lowest) {lowest = p.cords[i];}
			}
			
			for(Point p : points) 
			{
				if((highest - lowest) == 0) {p.cords[i] = 0;} //if highest and lowest are the same, then all the data are the same; default to 0
				else {p.cords[i] = (p.cords[i] - lowest)/(highest-lowest);}
			}
			
			
		}
	}

}
