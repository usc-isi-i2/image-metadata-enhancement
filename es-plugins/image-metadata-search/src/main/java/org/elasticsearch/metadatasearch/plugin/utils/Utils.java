package org.elasticsearch.metadatasearch.plugin.utils;

public class Utils {
	
	private static final String DELIMITER = "a";
	
	public static String hashArrayToString(int a[])
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<a.length;i++)
				sb.append(i+1).append(DELIMITER).append(a[i]).append(" ");
		return sb.toString().trim();
	}
	
	public static String hashTermToString(int i,int h)
	{
		return Integer.toString(i) + DELIMITER+ Integer.toString(h);
	}
	

}
