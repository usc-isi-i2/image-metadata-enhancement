package edu.isi.esextension.vectorsim.similarity;

import java.util.Arrays;

public class CosineSimilarity  {

	public static double cosineSimilarity(Double[] vectorA, Double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    
	    System.out.println(Arrays.toString(vectorA) + Arrays.toString(vectorB) + String.valueOf(dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))));
	    return dotProduct*100 / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}
