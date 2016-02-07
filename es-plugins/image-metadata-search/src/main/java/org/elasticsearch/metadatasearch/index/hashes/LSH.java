package org.elasticsearch.metadatasearch.index.hashes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import info.debatty.java.lsh.LSHSuperBit;

public class LSH {
	public static int bands  = 100; //higher,more precision
	public static int maxHashValue  = 5; //higher , more reduction
	public static LSHSuperBit lsh ;
	public static int size  = 144;
	public static final String LSH_HASH_FILE = "/hash/lshHashFunctions.obj";
	
	public static void readHashFunctions() throws Exception
	{
        ObjectInputStream ois;
		try 
		{
			ois = new ObjectInputStream(LSH.class.getResourceAsStream(LSH_HASH_FILE));
			lsh = (LSHSuperBit) ois.readObject();
		}
		catch (IOException e)
		{
			lsh = new LSHSuperBit(maxHashValue, bands, size);
			   
		}
		
		
        
        
	}

}
