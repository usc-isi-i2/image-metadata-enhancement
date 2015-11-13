package edu.isi.esextension.vectorsim.script;
import org.elasticsearch.script.AbstractDoubleSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;

import edu.isi.esextension.vectorsim.similarity.CosineSimilarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VectorSimScriptFactory implements NativeScriptFactory
{
	public static final String SCRIPT_NAME = "vector-similarity-plugin";
	public static final String SCRIPT_VECTOR_FIELD_NAME = "vector";
	public static final String SCRIPT_VECTOR_COMPARISON_TYPE_PARAMNAME = "metric";
	public static final String SCRIPT_VECTOR_COMPARISON_FIELD_PARAMNAME = "field";
	public static  String DOC_VECTOR_FIELD_NAME;
	
	
	public enum Similarity_Measure {
		COSINE
	}
	
	private class VectorSimScript extends AbstractDoubleSearchScript
	{
		private Double features[];
		private Similarity_Measure simMeasure;
		
		
		public  VectorSimScript(Map<String, Object> params) {
			this.simMeasure = Similarity_Measure.valueOf(((String)params.get(SCRIPT_VECTOR_COMPARISON_TYPE_PARAMNAME)).toUpperCase());
			@SuppressWarnings("unchecked")
			ArrayList<Double> feats = (ArrayList<Double>) params.get(SCRIPT_VECTOR_FIELD_NAME);
			int i=0;
			this.features = new Double[feats.size()];
			for (Double feat : feats)
				this.features[i++] = feat;
			DOC_VECTOR_FIELD_NAME = (String)params.get(SCRIPT_VECTOR_COMPARISON_FIELD_PARAMNAME);
		}
		

		@Override
		public double runAsDouble() {
			List<Double> docVectorList =  (List<Double>) source().get(DOC_VECTOR_FIELD_NAME);

			Double docVector[] = new Double[docVectorList.size()];
			docVectorList.toArray(docVector);
					switch (simMeasure) {
			
			case COSINE:
				return CosineSimilarity.cosineSimilarity(docVector, features);

			}
			return 0.0;
		}
	}
	
	public ExecutableScript newScript(Map<String, Object> params) {
		return new VectorSimScript(params);
	}
	
	



}
