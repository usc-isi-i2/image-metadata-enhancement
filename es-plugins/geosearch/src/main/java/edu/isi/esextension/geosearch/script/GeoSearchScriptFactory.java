package edu.isi.esextension.geosearch.script;

import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.AbstractDoubleSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
import java.util.Map;


public class GeoSearchScriptFactory implements NativeScriptFactory {

	private static final String GEO_PTS_FIELD_NAME = "geo.line";
	public static final String SCRIPT_NAME = "geoshape_search_nearest";
	
    private static class GeoSearchScript extends AbstractDoubleSearchScript {

        private Double lat;
        private Double lon;
        public GeoSearchScript(Map<String, Object> params)
        {
        	this.lat = ((Double)params.get("lat")).doubleValue();
        	this.lon = ((Double)params.get("lon")).doubleValue();
        }

        @Override
        public double runAsDouble() {
        	ScriptDocValues docValue = (ScriptDocValues) doc().get(GEO_PTS_FIELD_NAME);
        	String[] geoPts = ((ScriptDocValues.Strings) docValue).getValue().split(" ") ;
        	Double minDist = Double.MAX_VALUE;
        	for(int i=0;i<geoPts.length;i+=2)
        	{
        		Double d = distance(Double.parseDouble(geoPts[i]), this.lat , Double.parseDouble(geoPts[i+1]), this.lon, 0, 0);
        		if(d<minDist)
        			minDist = d;
        	}
        	return minDist;
        }
    }

	public ExecutableScript newScript(Map<String, Object> params) {
			return new GeoSearchScript(params);
		}
	
	private static double distance(double lat1, double lat2, double lon1,double lon2, double el1, double el2) {

          final int R = 6371; 
          Double latDistance = Math.toRadians(lat2 - lat1);
          Double lonDistance = Math.toRadians(lon2 - lon1);
          Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                  + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                  * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
          Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
          double distance = R * c * 1000; // convert to meters
          double height = el1 - el2;
          distance = Math.pow(distance, 2) + Math.pow(height, 2);
          return Math.sqrt(distance);
      }

}

