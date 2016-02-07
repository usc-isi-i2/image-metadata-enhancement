package org.elasticsearch.metadatasearch.similarity;


public class CosineSimilarity  {

	

	public static float cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));	}
}
