/*package edu.isi.karma.ugde.osm;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList; 
import org.xml.sax.SAXException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


//http://stackoverflow.com/questions/2223020/convert-an-org-w3c-dom-node-into-a-string
public class osmkml {
    
	private static final long serialVersionUID = 1L;
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	//-118.283877,34.032609,-118.261645,34.036114
	private static String minLon = "-118.283877";
	private static String minLat = "34.032609";
	private static String maxLon = "-118.261645";
	private static String maxLat = "34.036114";
	private static String type = "node";
	
	static String url = "bbox=" + minLon+","+minLat+","+maxLon+","+maxLat;
	private static String osmFile_path ="GET_OPENSTREETMAP.xml";
	private static String osmFilePathStreet ="GET_OPENSTREETMAP_Street.xml";
	private static String osmFilePathNode ="GET_OPENSTREETMAP_Node.xml";
	private static String osmFilePathAll ="GET_OPENSTREETMAP_All.json";
	private static String xmlResult = "";
	private static String jsonOutput=null;
	
	private static DocumentBuilderFactory builderFactory, builderFactoryCreator;
	private static DocumentBuilder builder, builderCreator;
	private static Document document, documentCreate;
	private static Element root;

    public static void main(String[] args) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException, TransformerException
    {
    	outputToOSM(url);
    	System.out.println("~~~");
//    	XMLtoJSON(xmlResult);
//    	System.out.println("^^^");
    	
    	builderFactory = DocumentBuilderFactory.newInstance();
    	builder = builderFactory.newDocumentBuilder();
    	document = builder.parse(osmFile_path); 
    	Element rootElement = document.getDocumentElement();
    	NodeList nodeElements = rootElement.getElementsByTagName("node"); 
    	NodeList wayElements = rootElement.getElementsByTagName("way");
    	
    	builderFactoryCreator = DocumentBuilderFactory.newInstance();
    	builderCreator = builderFactoryCreator.newDocumentBuilder();
    	documentCreate = builderCreator.newDocument();
    	root = documentCreate.createElement("osm");
    	documentCreate.appendChild(root);
    	
    	if (type.equalsIgnoreCase("ways") || type.equalsIgnoreCase("way")){
    		String xmlWays = "";
    		System.out.println(" --- ");
    		wayExtraction(wayElements, nodeElements);
    		    		
    	}else if(type.equalsIgnoreCase("node") || type.equalsIgnoreCase("nodes")){
    		String xmlNodes = "";
    		
    		System.out.println(" @@@ ");
    		nodeExtraction(nodeElements);
    		
    	}else{
    		XMLtoJSON(xmlResult);
    		
    		System.out.println(" === ");
    		wayExtraction(wayElements, nodeElements);
    		nodeExtraction(nodeElements);
    		
    	}
    }
    
    public static void nodeExtraction(NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmBuildings = documentCreate.createElement("buildings");
    	
    	osmBuildings.setAttribute("xmlns", "http://www.myphotos.org");
    	osmBuildings.setAttribute("xmlns:gml", "http://www.opengis.net/gml");
    	osmBuildings.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    	
    	System.out.println(" # of nodes = " + nodeElements.getLength());
    	
    	for (int i = 0 ; i < nodeElements.getLength(); i++) {
			Element node = (Element)nodeElements.item(i);
			NodeList tagElements = node.getElementsByTagName("tag");
			boolean isbuildings = true;
			if(tagElements.getLength() == 0)
				isbuildings = false;
			for(int j = 0; j < tagElements.getLength(); ++ j){
        	   Element tag = (Element)tagElements.item(j); 
        	   String k = tag.getAttribute("k");
        	   String v = tag.getAttribute("v");
        	   if(k.contains("way")){
        		   isbuildings = false;
        		   break;
        	   }
        	}
			
			if(isbuildings){
				Element osmBuliding = documentCreate.createElement("building");
				osmBuliding.setAttribute("id", node.getAttribute("id"));
	            String buildingName = " ? ";
	            
	            for(int j = 0; j < tagElements.getLength(); ++ j){
	            	Element tag = (Element)tagElements.item(j);
	            	String k = tag.getAttribute("k");
	            	String v = tag.getAttribute("v");
	            	if(k.contains("name") || k.contains("source")){
	            		buildingName = v;
	            		break;
	            	}
	            }
	            
	            osmBuliding.setAttribute("name", buildingName);
	            
	            System.out.println(node.getAttribute("id") + " " + buildingName);
	            
	            Element gml = documentCreate.createElement("gml:Point");
	            gml.setAttribute("srsDimension", "2");
	            gml.setAttribute("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
	            Element gmlcoordinate = documentCreate.createElement("gml:coordinates");
				
				//Reversing lat long output format
	            //String coordinate = ""+node.getAttribute("lat")+","+node.getAttribute("lon");
				
				String coordinate = ""+node.getAttribute("lon")+","+node.getAttribute("lat");
				
	            gmlcoordinate.appendChild(documentCreate.createTextNode(coordinate));
	            gml.appendChild(gmlcoordinate);
	            osmBuliding.appendChild(gml);
	            osmBuildings.appendChild(osmBuliding);
			}
			
			
		}
    	root.appendChild(osmBuildings);
    	CreateFile();
    //	System.out.println(documentCreate.toString());
	//	System.out.println(root.toString());
    }
    
    public static void wayExtraction(NodeList wayElements, NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmStreets = documentCreate.createElement("streets");
    	
    	osmStreets.setAttribute("xmlns", "http://www.myphotos.org");
    	osmStreets.setAttribute("xmlns:gml", "http://www.opengis.net/gml");
    	osmStreets.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    	
    	System.out.println(" # of ways = " + wayElements.getLength());
    	
		for (int i = 0 ; i < wayElements.getLength(); i++) { 
           Element way = (Element)wayElements.item(i); 
           NodeList ndElements = way.getElementsByTagName("nd");
           NodeList tagElements = way.getElementsByTagName("tag");
           
           String wayName = " ? ", wayType = " ? ";
           String wayId = way.getAttribute("id");
           for(int j = 0; j < tagElements.getLength(); ++ j){
        	   Element tag = (Element)tagElements.item(j); 
        	   String k = tag.getAttribute("k");
        	   String v = tag.getAttribute("v");
        	   if(k.contains("way")){
        		   wayType = k;
        	   }
        	   if(k.contains("name")){
        		   wayName = v;
        	   }
           }
           
           Element osmStreet = documentCreate.createElement("street");
           osmStreet.setAttribute("id", wayId);
           osmStreet.setAttribute("name", wayName);
           osmStreet.setAttribute("type", wayType);
           
           System.out.println(wayId + " " + wayName + " " + wayType);
           
           Element gml = documentCreate.createElement("gml:LineString");
           gml.setAttribute("srsDimension", "2");
           gml.setAttribute("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
           Element gmlcoordinate = documentCreate.createElement("gml:coordinates");
           boolean firstNd = true;
           String coordinate = "";
           Set<String> Ids = new HashSet<String>();
           for(int j = 0; j < ndElements.getLength(); ++ j){
        	   Element nd = (Element)ndElements.item(j); 
        	   String id = nd.getAttribute("ref");
        	   Ids.add(id);            	              	   
           }
           
           for(int j = 0; j < nodeElements.getLength(); ++ j){
        	   Element node = (Element)nodeElements.item(j); 
        	   String id = node.getAttribute("id");
        	   if(Ids.contains(id)){
        		   String lat = node.getAttribute("lat");
        		   String lon = node.getAttribute("lon");
        		   if(!firstNd)
        			   coordinate += " ";
        		   //Reversing lat and long output format
				   //coordinate += lat+","+lon;
				   
				   coordinate += lon+","+lat;
				   
				   
        		   firstNd = false;
        	   }
           }
           gmlcoordinate.appendChild(documentCreate.createTextNode(coordinate));
           gml.appendChild(gmlcoordinate);
           osmStreet.appendChild(gml);
           osmStreets.appendChild(osmStreet);
        } 
		
		root.appendChild(osmStreets);
	//	System.out.println(documentCreate.toString());
		
		CreateFile();
		
	//	System.out.println(root.toString());
    }
    
    private static void XMLtoJSON(String xmlResult) throws IOException{
    	try {
            JSONObject xmlJSONObj = XML.toJSONObject(xmlResult);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            FileOutputStream fout = new FileOutputStream(osmFilePathAll);
    		OutputStream bout = new BufferedOutputStream(fout);
    		OutputStreamWriter out = new OutputStreamWriter(bout, "utf-8");
    		out.write(jsonPrettyPrintString);
    		out.close();
            //System.out.println(jsonPrettyPrintString);
        } catch (JSONException je) {
            System.out.println(je.toString());
        }
    }
    
    private static void outputToOSM(String urls) throws ClientProtocolException, IOException {

		//this.url = "'http://open.mapquestapi.com/xapi/api/0.6/map?bbox=" + url; //Extract normal size data;
		url = "http://open.mapquestapi.com/xapi/api/0.6/map?" + urls; //Extract bigger size data;
		
		System.out.println("this.url="+url);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		xmlResult = EntityUtils.toString(entity);
		FileOutputStream fout = new FileOutputStream(osmFile_path);
		OutputStream bout = new BufferedOutputStream(fout);
		OutputStreamWriter out = new OutputStreamWriter(bout, "utf-8");
		
		out.write(xmlResult);
		out.close();
    }
    
    private static void CreateFile() throws FileNotFoundException, TransformerException{
    	
    	PrintWriter pw;
    	if(type.equalsIgnoreCase("ways") || type.equalsIgnoreCase("way")){
    		 pw = new PrintWriter(new FileOutputStream(osmFilePathStreet));		   		
    	}else if(type.equalsIgnoreCase("node") || type.equalsIgnoreCase("nodes")){
    		 pw = new PrintWriter(new FileOutputStream(osmFilePathNode));   		
    	}else{
    		 pw = new PrintWriter(new FileOutputStream(osmFilePathAll));   	
    	}
    	
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(documentCreate);
        transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        xmlResult = writer.toString();
   //     System.out.println(xmlResult);
   //     System.out.println(""+result.toString());
   //     StringWriter writer = new StringWriter();
   //     result = new StreamResult(writer);
   //     transformer.transform(source, result);
    }
    
}

//test data: http://open.mapquestapi.com/xapi/api/0.6/map?bbox=-118.300313,34.033629,-118.286451,34.036368
*/