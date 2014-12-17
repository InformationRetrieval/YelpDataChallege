package com.yelpdata.ir.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

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

import com.yelpdata.ir.utils.Utilities;


public class readExcel {
	
	//Global map containing category and coresponding reviews
	
	
	public static HashMap<String , Double> kewWords = new HashMap<String , Double>();
	public static ArrayList<String> delCat = new ArrayList<String>();
	public static ArrayList<String> lowPrecisionCategories = new ArrayList<String>();
	
	
public static void main(String args[]) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("deletedCat"));
		String line = null;
		line = br.readLine();
		while(line != null){
			   delCat.add(line);
			   line = br.readLine();
		}
		
		br = new BufferedReader(new FileReader("lowPrecisionCategories"));
		line = null;
		line = br.readLine();
		while(line != null){
			lowPrecisionCategories.add(line);
			   line = br.readLine();
		}

//		//System.out.println(delCat.toString());
//		
//		String path = "filteredRecords.xls";
//		FileInputStream file = new FileInputStream(new File(path));
//		
//		HSSFWorkbook workbook = new HSSFWorkbook(file);
//		
//		HSSFSheet sheet = workbook.getSheetAt(0);
//		HSSFRow row = null;
//		int counter = 1;
//		HashMap<String,String> mapBidTextFinal = new HashMap<String,String>();
//		while(true){
//			row = sheet.getRow(counter);
//			HashMap<String,String> mapBidText = new HashMap<String,String>();
//			if(row != null){
//				String bId = row.getCell(1).getStringCellValue();
//				String text = row.getCell(0).getStringCellValue().toLowerCase();
//				if(mapBidText.containsKey(bId))
//					mapBidText.put(bId,mapBidText.get(bId)+text);
//				else
//					mapBidText.put(bId,text) ;
//			}
//			
//			else 
//				break;
//			
//			
//			Iterator it = mapBidText.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Map.Entry pairs = (Map.Entry)it.next();
//		        String tempText = pairs.getValue().toString();
//		        mapBidTextFinal.put(pairs.getKey().toString(), Utilities.removeStopWords(tempText));
//		      //  System.out.println(pairs.getKey());
//		        it.remove(); // avoids a ConcurrentModificationException
//		    } 
//		
//			//String result = "This is the test for removing the stop word from the given text. I hope this method is of use.";
//			//Set<String> uniqueValues = uniqueWordExtraction(result);
//			
//			//System.out.println("terms = "+finalText);
//			counter++;
//		}
		
//		readBusinessCategory(mapBidTextFinal);
//		IndiCatData.generateData();
		ClubCatData.clubCategoryData();
//		System.out.println("Club Category Completed!!");
		BuildFeature.build();
//		System.out.println(Utilities.checkDelCat("restaurants"));
	}
}
	

	
	

