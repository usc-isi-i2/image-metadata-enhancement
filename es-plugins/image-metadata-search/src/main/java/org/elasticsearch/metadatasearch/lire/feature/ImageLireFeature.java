package org.elasticsearch.metadatasearch.lire.feature;

import net.semanticmetadata.lire.imageanalysis.LireFeature;

public interface ImageLireFeature  extends LireFeature
{
	 public void setHistogramFromDoubleArray(double[] h);
	 
	 public float getSimilarity(ImageLireFeature ilf);

}
