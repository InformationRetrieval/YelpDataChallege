package com.yelpdata.ir.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class readExcel {
	
	public static HashMap<String , Double> kewWords = new HashMap<>();
	
	
	public static void readBusinessCategory(HashMap<String, String> mapBidText){
		
		ArrayList<String> bID = new ArrayList<String>();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sample");
		
		try
		{
			JSONParser parser = new JSONParser();
			JSONArray a = (JSONArray) parser.parse(new FileReader("E:\\Study\\Fall 2014\\IR\\Project\\yelp_academic_dataset_business.json"));
			int rownum = 0;
			String cat = null;
			for (Object o : a){
				JSONObject p = (JSONObject) o;
				int column = 0;
				String business_id = p.get("business_id").toString();
				//System.out.println("BusinessID = "+business_id );
				//System.out.println("MapBID = +"+mapBidText.toString());
				
				if (mapBidText.containsKey(business_id) && p.get("categories").toString().contains("Restaurants")){
					//System.out.println("In if");
					HSSFRow row = sheet.createRow(rownum++);
					Cell bidCell = row.createCell(column++);
					bidCell.setCellValue(business_id);
					Cell catCell = row.createCell(column++);
					catCell.setCellValue(p.get("categories").toString());
					Cell textCell = row.createCell(column++);
					textCell.setCellValue(mapBidText.get(business_id));
					 cat =cat + "|"+p.get("categories").toString();
				}
			}
			
			FileOutputStream out = new FileOutputStream(new File("final.xls"));
			workbook.write(out);
			out.close();
			
			Set<String>uniqueValues = uniqueWordExtraction(cat.replaceAll("[\\[\\]]", ""));
			for (String term : uniqueValues){
				System.out.println(term);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]) throws Exception{
		String path = "filteredRecords.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		HashMap<String,String> mapBidTextFinal = new HashMap<String,String>();
		while(true){
			row = sheet.getRow(counter);
			HashMap<String,String> mapBidText = new HashMap<String,String>();
			if(row != null){
				String bId = row.getCell(1).getStringCellValue();
				String text = row.getCell(0).getStringCellValue();
				if(mapBidText.containsKey(bId))
					mapBidText.put(bId,mapBidText.get(bId)+text);
				else
					mapBidText.put(bId,text) ;
			}
			
			else 
				break;
			
			
			Iterator it = mapBidText.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        String tempText = pairs.getValue().toString();
		        mapBidTextFinal.put(pairs.getKey().toString(), removeStopWords(tempText));
		      //  System.out.println(pairs.getKey());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    
		    
		    
		    
			//String result = "This is the test for removing the stop word from the given text. I hope this method is of use.";
			//Set<String> uniqueValues = uniqueWordExtraction(result);
			
			//System.out.println("terms = "+finalText);
			counter++;
		}
		readBusinessCategory(mapBidTextFinal);
	}
	
	public static String removeStopWords(String textFile) throws Exception {
	    //CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
		
		StandardAnalyzer analyser = new StandardAnalyzer();
	    TokenStream tokenStream = new StandardTokenizer(new StringReader(textFile.trim()));
	    tokenStream = new StopFilter(tokenStream, analyser.STOP_WORDS_SET);
	    
	    List<String> stopWords = Arrays.asList("Yeah","The","At","Its","I");
	    
	    tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(stopWords));
	    
	    StringBuilder sb = new StringBuilder();
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	    tokenStream.reset();
	    while (tokenStream.incrementToken()) {
	        String term = charTermAttribute.toString();
	        sb.append(term + " ");
	    }
	    return sb.toString();
	}
	
	
	public static Set<String> uniqueWordExtraction(String textValue){
		
		//Set<String> unique = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[-.,:;?!~\\s]+")));
		String result = "restuarant,restuarant";
		Set<String> unique = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[,|]")));
		/*	String finalText= null;
		for (String term : uniqueValues){
			//System.out.println("terms = "+term);
			finalText = finalText + " " +  term;
		}
		return finalText;*/
		return unique;
	}
}