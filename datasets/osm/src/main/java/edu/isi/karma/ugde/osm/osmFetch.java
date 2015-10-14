package edu.isi.karma.ugde.osm;
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

import org.w3c.dom.CDATASection;
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

/*
*
* Java Program to download and extract building and street information from OSM data
*
*
*/

public class osmFetch {
	
	//The url to download OSM data
	private static String url = "";
	//The string of OSM data in xml format
	private static String xmlResult = "";
	private int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	private static String minLon = "";
	private static  String minLat = "";
	private static  String maxLon = "";
	private static  String maxLat = "";
	private static  String type = "";
	
	private static DocumentBuilderFactory builderFactory, builderFactoryCreator;
	private static DocumentBuilder builder, builderCreator;
	private static Document document, documentCreate;
	private static Element osmroot, root;
	//The hashMap used for fast retrieving building information needed in street information extraction, with key as node id and value as node entity
	private static HashMap hashMapNode;
	
	//The file path to save original OSM xml data
	private static String osmFile_path ="GET_OPENSTREETMAP_KML.xml";
	//The file path to save original OSM xml data only including street information
	private static String osmFilePathStreet= "OPENSTREETMAP_STREET.xml";
	//The file path to save original OSM xml data only including building information
	private static String osmFilePathNode = "OPENSTREETMAP_BUILDING.xml";
	//The file path to save complete original OSM xml data
	private static String osmFilePathAll="OPENSTREETMAP_ALL.xml";
	
		
	public static void main(String[] args) throws ClientProtocolException, IOException{
//		System.out.println(args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" "+args[4]);
		Scanner scanf = new Scanner(System.in);
//		System.out.println("The order of input parameters are longN, latE, longS, latW and type");
		//The order of input parameters are longN, latE, longS, latW and type
		minLon = args[0];
		minLat = args[1];
		maxLon = args[2];
		maxLat = args[3];
		type = args[4];
		
		System.out.println(minLon+" "+minLat+" "+maxLon+" "+maxLat+" "+type);
		
		url = "bbox=" + minLon+","+minLat+","+maxLon+","+maxLat;
		url += "&key=b8uBAGZowqebVTadpoak56zmANQtQyUA";
		System.out.println("HEYHEYHEY");
		
		//Download original OSM data
		outputToOSM(url);
    		System.out.println("~~~");
    	
	builderFactory = DocumentBuilderFactory.newInstance();
	try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	try {
			document = builder.parse(osmFile_path);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	Element rootElement = document.getDocumentElement();
	//NodeList datastructure to save node information of OSM data
    	NodeList nodeElements = rootElement.getElementsByTagName("node");
	//NodeList datastructure to save street information of OSM data 
    	NodeList wayElements = rootElement.getElementsByTagName("way");
	//NodeList datastructure to save relation information between buildings and streets of OSM data
    	NodeList relationElements = rootElement.getElementsByTagName("relation");
    	
	//Save all nodes information into hashmap
    	initHashMapNode(nodeElements);
    	
    	builderFactoryCreator = DocumentBuilderFactory.newInstance();
    	try {
			builderCreator = builderFactoryCreator.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	documentCreate = builderCreator.newDocument();
    	osmroot = documentCreate.createElement("osm");
    	root = documentCreate.createElement("kml");
    	root.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
    	osmroot.appendChild(root);
    	documentCreate.appendChild(osmroot);
    	
	//Invoke the street information extraction if query type is street
    	if (type.equalsIgnoreCase("streets") || type.equalsIgnoreCase("street")){
    		String xmlWays = "";
    		System.out.println(" --- ");
    		try {
				wayExtraction(wayElements, nodeElements);
	//			relationExtraction(relationElements);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		    		
    	}else if(type.equalsIgnoreCase("building") || type.equalsIgnoreCase("buildings")){
		//Invoke the building information extraction if query type is buildings
    		String xmlNodes = "";
    		
    		System.out.println(" @@@ ");
    		try {
				nodeExtraction(nodeElements);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}else{
		//Invoke both the street and building information extraction if query type is "all"
    		try {
				wayExtraction(wayElements, nodeElements);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
				nodeExtraction(nodeElements);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
		
 //   	PrintWriter out = response.getWriter();
    //	out.println(xmlResult);
	}
	
	/*
	*Function: Initialize the hashmap to save the node information with key as node id and value as node entity
	*Input: The list of all the node information of OSM data downloaded
	*Output: Null
	*/
	public static void initHashMapNode(NodeList nodeElements){
		hashMapNode = new HashMap();
		for (int i = 0 ; i < nodeElements.getLength(); i++) {
			Element node = (Element)nodeElements.item(i);
			String id = node.getAttribute("id");
			hashMapNode.put(id, node);
		}
	}
	
	/*
	*Function: Extract only building information among all the node information and then save it into xml file
	*Input: The list of all the node information of OSM data downloaded
	*Algorithm: Enumerate all the nodes in the list, only consider those that have tags and the k value of all the tags does not contain "way" substring as buildings
	*           Only extract state, city, county, and name information of the street;
	*	    To identify state name, find v value of tags whose k value contains both "state(s)" or "province(s)" and "addr" or "tiger" substring;
	*	    To identify city name, find v value of tags whose k value contains both "city" and "addr" or "tiger" substring;
	*	    To identify county name, find v value of tags whose k value contains both "county" and "addr" or "tiger" substring
	*Output: Null
	*/
	public static void nodeExtraction(NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmBuildings = documentCreate.createElement("Document");
    	
  /*  	osmBuildings.setAttribute("xmlns", "http://www.myphotos.org");
    	osmBuildings.setAttribute("xmlns:kml", "http://www.opengis.net/kml");
    	osmBuildings.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
 */   	
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
				Element osmBuliding = documentCreate.createElement("Placemark");
				osmBuliding.setAttribute("id", node.getAttribute("id"));
	            String buildingName = "";
	            String cityName = "";
	            String countyName = "";
	            String stateName = "";
	            
	            for(int j = 0; j < tagElements.getLength(); ++ j){
	            	Element tag = (Element)tagElements.item(j);
	            	String k = tag.getAttribute("k");
	            	String v = tag.getAttribute("v");
	            	if(k.contains("name") || k.contains("source")){
	            		buildingName = v;
	            	}
	            	if(k.contains("state") || k.contains("states") || k.contains("province") || k.contains("provinces") && (k.contains("addr") || k.contains("tiger"))){
	               			stateName = v;
	    	       	}
	    	       	if(k.contains("city") && (k.contains("addr") || k.contains("tiger"))){
	    	       		    cityName = v;
	    	       	}
	    	       	if(k.contains("county") && (k.contains("addr") || k.contains("tiger"))){
	    	       	       countyName = v;
	    	       	}
	            }
	            
	            Element kmlname = documentCreate.createElement("Name");
	            kmlname.appendChild(documentCreate.createTextNode(buildingName));
	            osmBuliding.appendChild(kmlname);
	            
	/*            Element address = documentCreate.createElement("Address");
	            kmlname.appendChild(documentCreate.createTextNode(buildingName));
	            osmBuliding.appendChild(address);
	*/            
	            Element cityname = documentCreate.createElement("CityName");
	            cityname.appendChild(documentCreate.createTextNode(cityName));
	            osmBuliding.appendChild(cityname);
	            Element countyname = documentCreate.createElement("CountyName");
	            countyname.appendChild(documentCreate.createTextNode(countyName));
	            osmBuliding.appendChild(countyname);
	            Element statename = documentCreate.createElement("StateName");
	            statename.appendChild(documentCreate.createTextNode(stateName));
	            osmBuliding.appendChild(statename);
	            
	            Element kmltype = documentCreate.createElement("Type");
	            kmltype.appendChild(documentCreate.createTextNode("building"));
	            osmBuliding.appendChild(kmltype);
	    //        osmBuliding.setAttribute("name", buildingName);
	            
	            System.out.println(node.getAttribute("id") + " " + buildingName);
	            
	       //     Element kml = documentCreate.createElement("Point");
	       //     kml.setAttribute("srsDimension", "2");
	       //     kml.setAttribute("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
//	            Element kmlcoordinate = documentCreate.createElement("coordinates");
	            CDATASection cdata = documentCreate.createCDATASection("");
	            Element kmlgeometry = documentCreate.createElement("KmlGeometry");
	            String pointValue = "<Point>";
	            
				//Reversing lat long output format
				//String coordinate = ""+node.getAttribute("lat")+","+node.getAttribute("lon");
				
				String coordinate = ""+node.getAttribute("lon")+","+node.getAttribute("lat");
				
//	            kmlcoordinate.appendChild(documentCreate.createTextNode(coordinate));
//	            kml.appendChild(kmlcoordinate);
	            pointValue += "<coordinates>"+coordinate+"</coordinates>";
	            pointValue += "</Point>";
	            cdata.appendData(pointValue);
	            kmlgeometry.appendChild(cdata);
	            osmBuliding.appendChild(kmlgeometry);
	            osmBuildings.appendChild(osmBuliding);
			}
			
			
		}
    	root.appendChild(osmBuildings);
	//Save the building information into xml file
    	CreateFile();
    }
    
    	/*
	*Function: Extract street information and then save it into xml file
	*Input: The list of all the street information of OSM data downloaded
	*Algorithm: Enumerate all the streets in the list, and for each street, enumerate all the nodes in its node list;
	*           Only extract state, city, county, name and type information of the street;
	*	    To identify state name, find v value of tags whose k value contains both "state(s)" or "province(s)" and "addr" or "tiger" substring;
	*	    To identify city name, find v value of tags whose k value contains both "city" and "addr" or "tiger" substring;
	*	    To identify county name, find v value of tags whose k value contains both "county" and "addr" or "tiger" substring
	*Output: Null
	*/
    public static void wayExtraction(NodeList wayElements, NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmStreets = documentCreate.createElement("Document");
    	
  /*  	osmStreets.setAttribute("xmlns", "http://www.myphotos.org");
    	osmStreets.setAttribute("xmlns:kml", "http://www.opengis.net/kml");
    	osmStreets.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
  */  	
    	System.out.println(" # of ways = " + wayElements.getLength());
    	
		for (int i = 0 ; i < wayElements.getLength(); i++) { 
           Element way = (Element)wayElements.item(i); 
           NodeList ndElements = way.getElementsByTagName("nd");
           NodeList tagElements = way.getElementsByTagName("tag");
           
           String wayName = "", wayType = "";
           String cityName = "";
           String countyName = "";
           String stateName = "";
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
        	   if(k.contains("state") || k.contains("states") || k.contains("province") || k.contains("provinces")
        			   && (k.contains("addr") || k.contains("tiger"))){
          			stateName = v;
		       }
		       if(k.contains("city") && (k.contains("addr") || k.contains("tiger"))){
		       	    cityName = v;
		       }
		       if(k.contains("county") && (k.contains("addr") || k.contains("tiger"))){
		            countyName = v;
		       }
           }
           
           Element osmStreet = documentCreate.createElement("Placemark");
           osmStreet.setAttribute("id", wayId);
           Element kmlname = documentCreate.createElement("Name");
           kmlname.appendChild(documentCreate.createTextNode(wayName));
           osmStreet.appendChild(kmlname);
           
           Element cityname = documentCreate.createElement("CityName");
           cityname.appendChild(documentCreate.createTextNode(cityName));
           osmStreet.appendChild(cityname);
           Element countyname = documentCreate.createElement("CountyName");
           countyname.appendChild(documentCreate.createTextNode(countyName));
           osmStreet.appendChild(countyname);
           Element statename = documentCreate.createElement("StateName");
           statename.appendChild(documentCreate.createTextNode(stateName));
           osmStreet.appendChild(statename);
           
           Element kmltype = documentCreate.createElement("Type");
           //http://examples.javacodegeeks.com/core-java/xml/dom/add-cdata-section-to-dom-document/
           //documentCreate.createCDATASection(arg0)
           
           kmltype.appendChild(documentCreate.createTextNode(wayType));
           osmStreet.appendChild(kmltype);
        //   osmStreet.setAttribute("type", wayType);
           
           System.out.println(wayId + " " + wayName + " " + wayType);
           
           CDATASection cdata = documentCreate.createCDATASection("");
           Element kmlgeometry = documentCreate.createElement("KmlGeometry");
   //        Element kml = documentCreate.createElement("LineString");
   //        kml.setAttribute("srsDimension", "2");
   //        kml.setAttribute("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
  //         Element kmlcoordinate = documentCreate.createElement("coordinates");
           boolean firstNd = true;
           String lineStringValue = "<LineString>";
           String coordinate = "";
           
           for(int j = 0; j < ndElements.getLength(); ++ j){
        	   Element nd = (Element)ndElements.item(j); 
        	   String id = nd.getAttribute("ref");
        //	   Ids.add(id);
        	   if(hashMapNode.containsKey(id)){
        		   Element node = (Element) hashMapNode.get(id);
        		   String lat = node.getAttribute("lat");
        		   String lon = node.getAttribute("lon");
        		   if(!firstNd)
        			   coordinate += " ";
        		   
				   //reversing lat long output format
				   //coordinate += lat+","+lon+",0";
				   
				   coordinate += lon+","+lat+",0";
        		   
				   firstNd = false;
        	   }
           }
           lineStringValue += "<coordinates>"+coordinate+"</coordinates>";
           lineStringValue += "</LineString>";
           
 //          kmlcoordinate.appendChild(documentCreate.createTextNode(coordinate));
 //          kml.appendChild(kmlcoordinate);
           cdata.appendData(lineStringValue);
//           cdata.appendChild(kml);
           kmlgeometry.appendChild(cdata);
           osmStreet.appendChild(kmlgeometry);
           osmStreets.appendChild(osmStreet);
        } 
		
		root.appendChild(osmStreets);
		//Save the street information into xml file
		CreateFile();
    }
    
    public static void relationExtraction(NodeList wayElements, NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmStreets = documentCreate.createElement("Document");
    	
    	System.out.println(" # of ways = " + wayElements.getLength());
    	
		for (int i = 0 ; i < wayElements.getLength(); i++) { 
           Element way = (Element)wayElements.item(i); 
           NodeList ndElements = way.getElementsByTagName("nd");
           NodeList tagElements = way.getElementsByTagName("tag");
           
           String wayName = "", wayType = "";
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
           
           Element osmStreet = documentCreate.createElement("Placemark");
           osmStreet.setAttribute("id", wayId);
           Element kmlname = documentCreate.createElement("Name");
           kmlname.appendChild(documentCreate.createTextNode(wayName));
           osmStreet.appendChild(kmlname);
           Element kmltype = documentCreate.createElement("Type");
           kmltype.appendChild(documentCreate.createTextNode(wayType));
           osmStreet.appendChild(kmltype);
        //   osmStreet.setAttribute("type", wayType);
           
           System.out.println(wayId + " " + wayName + " " + wayType);
           
           Element kml = documentCreate.createElement("LineString");
   //        kml.setAttribute("srsDimension", "2");
   //        kml.setAttribute("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
           Element kmlcoordinate = documentCreate.createElement("coordinates");
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
				   
				   //reversing output format
        		   //coordinate += lat+","+lon+",0";
				   
				   coordinate += lon+","+lat+",0";
				   
        		   firstNd = false;
        	   }
           }
           kmlcoordinate.appendChild(documentCreate.createTextNode(coordinate));
           kml.appendChild(kmlcoordinate);
           osmStreet.appendChild(kml);
           osmStreets.appendChild(osmStreet);
        } 
		
		root.appendChild(osmStreets);
		CreateFile();
    }
	
	/*
	*Function: Download the original OSM data of xml format and save it in the local folder
	*Input : The url of parameters for OSM xapi
	*Output : Null  
	*/
	private static void outputToOSM(String urls) throws ClientProtocolException, IOException {

		//this.url = "'http://open.mapquestapi.com/xapi/api/0.6/map?bbox=" + url; //Extract normal size data;
		url = "http://open.mapquestapi.com/xapi/api/0.6/map?" + urls; //Extract bigger size data;
		
		System.out.println("this.url="+url);
		DefaultHttpClient client = new DefaultHttpClient();
		//Use the Http Get method to download the OSM data from given 
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
	
	/*
	*Function: Save the extracted information into a local xml file
	*Input : Null
	*Output : Null  
	*/
	private static void CreateFile() throws FileNotFoundException, TransformerException{
    	
    	PrintWriter pw;
    	if(type.equalsIgnoreCase("street") || type.equalsIgnoreCase("streets")){
    		 pw = new PrintWriter(new FileOutputStream(osmFilePathStreet));		   		
    	}else if(type.equalsIgnoreCase("building") || type.equalsIgnoreCase("buildings")){
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
        System.out.println(xmlResult);
        pw.println(xmlResult);
        pw.flush();
        pw.close();
   //     System.out.println(""+result.toString());
   //     StringWriter writer = new StringWriter();
   //     result = new StreamResult(writer);
   //     transformer.transform(source, result);
    }
}
