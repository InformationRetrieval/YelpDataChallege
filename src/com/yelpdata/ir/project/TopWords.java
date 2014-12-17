package com.yelpdata.ir.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.yelpdata.ir.utils.Utilities;

public class TopWords {
	//Get Top 40 words from HashMap Values
	public static String getTop(HashMap<String,Double> map, String catg){
			
		  //System.out.println("In get Top with "+catg);
		
		  ValueComparator bvc =  new ValueComparator(map);
	      TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
	      String topWords = "";
	      
	      sorted_map.putAll(map);
	      
		//HashMap<String, Integer> sorted = new HashMap<String,Integer>();
		 //System.out.println(sortByValue(map).toString());	
	     // System.out.println(map.toString());
		//sorted = (HashMap<String, Integer>) sortByValue(map);
			//System.out.println(sorted_map.toString());
	      Iterator it = sorted_map.entrySet().iterator();
	      int count = 1;
		    while (it.hasNext()) {
		    	Map.Entry keyVal = (Map.Entry)it.next();
		    	if(Utilities.catInowPrecision(catg)){
		    		if(count < 51){
			    		//System.out.println(keyVal.getKey().toString()+"   "+keyVal.getValue().toString());
			    		topWords = topWords + " " + keyVal.getKey().toString();
			    	}
		    		else
			    		break;
		    	}
		    	else{
		    		if(count < 11){
			    		//System.out.println(keyVal.getKey().toString()+"   "+keyVal.getValue().toString());
			    		topWords = topWords + " " + keyVal.getKey().toString();
			    	}
		    		else
			    		break;
		    	}
		    	count++;
		    }
		  //System.out.println("Top words found!");
		  return topWords;
		  
	}
	
	
	public static HashMap<String, String> getWordScores(HashMap<String,String> map) throws IOException{
		
		System.out.println("In get Word Scores");
		
		Iterator it = map.entrySet().iterator();
		HashMap<String, String> topCatWords = new HashMap<String,String>();
	    while (it.hasNext()) {
	    	HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
	    	HashMap<String,Double> tfIDF = new HashMap<String,Double>(); 
	    	Map.Entry pairs = (Map.Entry)it.next();
	        String tempText = pairs.getValue().toString();
	        StringTokenizer st = new StringTokenizer(tempText);
	        while(st.hasMoreElements()){
	        	String currentWord = st.nextToken();
	        	if(wordCount.containsKey(currentWord)){
					wordCount.put(currentWord, wordCount.get(currentWord)+1);
				}
				else{
					wordCount.put(currentWord, 1);
				}
				
	        }
	        Iterator itr = wordCount.entrySet().iterator();
	        while (itr.hasNext()) {
	        	Map.Entry KV = (Map.Entry)itr.next();
	        	
	        	double IDF = calculateIDF(pairs.getKey().toString(),KV.getKey().toString());
	        	double GIDF = calGlobalTF(pairs.getKey().toString(),KV.getKey().toString()); 
	        	
	        	tfIDF.put(KV.getKey().toString(),Integer.parseInt(KV.getValue().toString()) * IDF *GIDF );
	        	
	        }
	        
	       
		    
	        //System.out.println(pairs.getKey().toString());
	        //System.out.println(wordCount.toString());
	        topCatWords.put(pairs.getKey().toString(), getTop(tfIDF, pairs.getKey().toString()));
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
	    //System.out.println("Get word scores completed");
	    
		return topCatWords; 
	}
	
	
	public static double calculateIDF(String catName, String word){
		ArrayList<String> reviewsForCat = new ArrayList<String>();
		reviewsForCat = ClubCatData.globalCat.get(catName);
		int count =0;
		for(String review : reviewsForCat){
			if(review.contains(word)){
				count++;
			}
		}
		return (double)count/reviewsForCat.size();
	}
	
	public static double calGlobalTF(String cat, String word){
		Iterator itr = ClubCatData.mapCatText.entrySet().iterator();
		int gCount = 0;
        while (itr.hasNext()) {
        	Map.Entry KV = (Map.Entry)itr.next();
        	
        	if(KV.getValue().toString().contains(word)){
        		gCount++;
        	}
        }
        return (double)ClubCatData.mapCatText.size()/gCount;

	}
	
}




class ValueComparator implements Comparator<String> {
	
	Map<String, Double> base;
	public ValueComparator(Map<String, Double> base) {
	    this.base = base;
	}
	
	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
	    if (base.get(a) >= base.get(b)) {
	        return -1;
	    } else {
	        return 1;
	    } // returning 0 would merge keys
	}
}
