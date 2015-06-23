/*
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.log4j.Logger;

*//**
 * Servlet implementation class osm
 *//*
@WebServlet("/osm")
public class osm extends HttpServlet {
	private   final long serialVersionUID = 1L;
    
	private String url = "";
	private String xmlResult = "";
	private int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	private String minLon = "";
	private String minLat = "";
	private String maxLon = "";
	private String maxLat = "";
	private String type = "";
	
	private DocumentBuilderFactory builderFactory, builderFactoryCreator;
	private DocumentBuilder builder, builderCreator;
	private Document document, documentCreate;
	private Element osmroot, root;
	private HashMap hashMapNode;
	
	private String osmFile_path ="GET_OPENSTREETMAP_KML.xml";
	private String osmFilePathStreet= "OPENSTREETMAP_STREET.xml";
	private String osmFilePathNode = "OPENSTREETMAP_NODE.xml";
	private String osmFilePathAll="OPENSTREETMAP_ALL.xml";
	
    *//**
     * @see HttpServlet#HttpServlet()
     *//*
    public osm() {
        super();
        // TODO Auto-generated constructor stub
    }

	*//**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 *//*
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		minLon = request.getParameter("longN");
		minLat = request.getParameter("latE");
		maxLon = request.getParameter("longS");
		maxLat = request.getParameter("latW");
		type = request.getParameter("type");
		
		System.out.println(minLon+" "+minLat+" "+maxLon+" "+maxLat+" "+type);
		
		url = "bbox=" + minLon+","+minLat+","+maxLon+","+maxLat;
		
		outputToOSM(url);
    	System.out.println("~~~");
//    	XMLtoJSON(xmlResult);
//    	System.out.println("^^^");
    	
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
    	NodeList nodeElements = rootElement.getElementsByTagName("node"); 
    	NodeList wayElements = rootElement.getElementsByTagName("way");
    	NodeList relationElements = rootElement.getElementsByTagName("relation");
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
    	
    	if (type.equalsIgnoreCase("roads") || type.equalsIgnoreCase("road")){
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
    		String xmlNodes = "";
    		
    		System.out.println(" @@@ ");
    		try {
				nodeExtraction(nodeElements);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}else{
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
		
    	PrintWriter out = response.getWriter();
    	out.println(xmlResult);
    	
	}

	*//**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 *//*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	public   void initHashMapNode(NodeList nodeElements){
		hashMapNode = new HashMap();
		for (int i = 0 ; i < nodeElements.getLength(); i++) {
			Element node = (Element)nodeElements.item(i);
			String id = node.getAttribute("id");
			hashMapNode.put(id, node);
		}
	}
	
	public   void nodeExtraction(NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmBuildings = documentCreate.createElement("Document");
    	
    	osmBuildings.setAttribute("xmlns", "http://www.myphotos.org");
    	osmBuildings.setAttribute("xmlns:kml", "http://www.opengis.net/kml");
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
	            	if(k.contains("state") || k.contains("states") && (k.contains("addr") || k.contains("tiger"))){
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
	            
	            Element address = documentCreate.createElement("Address");
	            kmlname.appendChild(documentCreate.createTextNode(buildingName));
	            osmBuliding.appendChild(address);
	            
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
	            
	            Element kml = documentCreate.createElement("Point");
	       //     kml.setAttribute("srsDimension", "2");
	       //     kml.setAttribute("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
	            Element kmlcoordinate = documentCreate.createElement("coordinates");
				//Reverse lat lon
	            //String coordinate = ""+node.getAttribute("lat")+","+node.getAttribute("lon");
				String coordinate = ""+node.getAttribute("lon")+","+node.getAttribute("lat");
	            kmlcoordinate.appendChild(documentCreate.createTextNode(coordinate));
	            kml.appendChild(kmlcoordinate);
	            osmBuliding.appendChild(kml);
	            osmBuildings.appendChild(osmBuliding);
			}
			
			
		}
    	root.appendChild(osmBuildings);
    	CreateFile();
    //	System.out.println(documentCreate.toString());
	//	System.out.println(root.toString());
    }
    
    public   void wayExtraction(NodeList wayElements, NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmStreets = documentCreate.createElement("Document");
    	
    	osmStreets.setAttribute("xmlns", "http://www.myphotos.org");
    	osmStreets.setAttribute("xmlns:kml", "http://www.opengis.net/kml");
    	osmStreets.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    	
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
        		   //reverse lat lon
				   //coordinate += lat+","+lon+",0";
				   
				   coordinate += lon+","+lat+",0";
        		   firstNd = false;
        	   }
           }
   
          
           for(int j = 0; j < nodeElements.getLength(); ++ j){
        	   Element node = (Element)nodeElements.item(j); 
        	   String id = node.getAttribute("id");
        	   if(Ids.contains(id)){
        		   String lat = node.getAttribute("lat");
        		   String lon = node.getAttribute("lon");
        		   if(!firstNd)
        			   coordinate += " ";
        		   coordinate += lat+","+lon+",0";
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
    
    public   void relationExtraction(NodeList wayElements, NodeList nodeElements) throws FileNotFoundException, TransformerException{
    	Element osmStreets = documentCreate.createElement("Document");
    	
    	osmStreets.setAttribute("xmlns", "http://www.myphotos.org");
    	osmStreets.setAttribute("xmlns:kml", "http://www.opengis.net/kml");
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
        		   //reverse lat lon
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
	
	private   void outputToOSM(String urls) throws ClientProtocolException, IOException {

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
	
	private   void output(String urls) throws ClientProtocolException, IOException {

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
	
	private void CreateFile() throws FileNotFoundException, TransformerException{
    	
    	PrintWriter pw;
    	if(type.equalsIgnoreCase("road") || type.equalsIgnoreCase("roads")){
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
*/