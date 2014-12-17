package com.yelpdata.ir.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.yelpdata.ir.project.CreateDataset;

public class Utilities {

	
	public static String removeStopWords(String textFile) throws Exception {
	    //CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		
		StandardAnalyzer analyser = new StandardAnalyzer();
	    TokenStream tokenStream = new StandardTokenizer(new StringReader(textFile.trim()));
	    tokenStream = new StopFilter(tokenStream, analyser.STOP_WORDS_SET);
	    
	    BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"));
	    String line = null;
	    List<String> stopw = new ArrayList<String>();
	    int i =0;
	    line = br.readLine();
	   while(line != null){
		   stopw.add(line);
		   line = br.readLine();
		}
	    
	    
	  //  List<String> stopWords = ArraysasList(stopw);
	    
	    tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(stopw));
	    
	    StringBuilder sb = new StringBuilder();
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	    tokenStream.reset();
	    while (tokenStream.incrementToken()) {
	        String term = charTermAttribute.toString();
	        if(term.length() > 2)
	        	sb.append(term + " ");
	    }
	    return sb.toString();
	}
	
	
	public static String uniqueWordExtraction(String textValue){
		Set<String> uniqueValues = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[.,:;?!~\\s]+")));
		String finalText= "";
		for (String term : uniqueValues){
			//System.out.println("terms = "+term);
			finalText = finalText + " " +  term;
		}
		return finalText;
	}
	
	public static Set<String> uniqueCategoriesExtraction(String textValue){
		
		//System.out.println(Arrays.asList(textValue.toLowerCase().toString()));
		Set<String> unique = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[,|]")));
		//System.out.println(unique.size());
		for (String term : unique){
			//System.out.println("terms = "+term);
		}
		return unique;
	}
	
	
	public static boolean checkDelCat(String cate){
		
		
		for (String categories : CreateDataset.delCat){
				
			if(categories.equalsIgnoreCase(cate.trim()))
				return true;
			
		}
		return false;
	}
	
	public static boolean catInowPrecision(String catg){
		
		for (String categories : CreateDataset.lowPrecisionCategories){
			
			if(categories.equalsIgnoreCase(catg.trim()))
				return true;
			
		}
		return false;
	}
	
}
	

