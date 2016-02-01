package org.elasticsearch.metadatasearch.lire.feature;

import org.elasticsearch.metadatasearch.similarity.CosineSimilarity;

import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.LireFeature;

public class ImageCEDD extends CEDD implements ImageLireFeature{
	
	 public void setHistogramFromDoubleArray(double[] h)
	    {
	         for (int i = 0; i < h.length; i++) {
	             histogram[i] = (byte) h[i];
	         }
	         
	    }
	 
	 @Override
	 public float getDistance(LireFeature vd) {
		 return CosineSimilarity.cosineSimilarity(this.getDoubleHistogram(), vd.getDoubleHistogram());
	 }
}
