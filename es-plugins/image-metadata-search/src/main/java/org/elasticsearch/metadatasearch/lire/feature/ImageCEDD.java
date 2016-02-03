package org.elasticsearch.metadatasearch.lire.feature;

import org.elasticsearch.metadatasearch.similarity.CosineSimilarity;

import net.semanticmetadata.lire.imageanalysis.CEDD;

public class ImageCEDD extends CEDD implements ImageLireFeature{
	
	 public void setHistogramFromDoubleArray(double[] h)
	    {
	         for (int i = 0; i < h.length; i++) {
	             histogram[i] = (byte) h[i];
	         }
	         
	    }

	@Override
	public float getSimilarity(ImageLireFeature ilf) {
		 return CosineSimilarity.cosineSimilarity(this.getDoubleHistogram(), ilf.getDoubleHistogram());
		
	}
}
