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


public class readExcel {
	
	public static HashMap<String , Double> kewWords = new HashMap<>();
	
	
	public static void readBusinessCategory(HashMap<String, String> mapBidText){
		
		ArrayList<String> bID = new ArrayList<String>();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sample");
		
		try
		{
			JSONParser parser = new JSONParser();
			JSONArray a = (JSONArray) parser.parse(new FileReader("yelp_academic_dataset_business.json"));
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
			
			Set<String> uniqueValues = uniqueCategoriesExtraction(cat.replaceAll("[\\[\\]]", ""));
		
			//Print Unique Categories
			/*for (String term : uniqueValues){
				System.out.println(term);
			}*/
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	//Generate a row for each category from final.xls
	public static void generateData() throws IOException{
		String path = "final.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		
		HSSFWorkbook finalDataWB = new HSSFWorkbook();

		int docnumber = 1;
		int rowCounter = 1;
		HSSFSheet train = finalDataWB.createSheet("data"+docnumber);
		while(true){	
			
			row = sheet.getRow(counter);
			
			if(row != null){
				String bId = row.getCell(0).getStringCellValue();
				String categories = row.getCell(1).getStringCellValue();
				String text = row.getCell(2).getStringCellValue();
				
				//System.out.println("Bid : "+bId);
				//System.out.println("categories : "+categories);
				//System.out.println("text : "+text);
				
				Set<String> cats = uniqueCategoriesExtraction(categories.replaceAll("[\\[\\]]", ""));
				for(String cat : cats){				
					
					HSSFRow catRow = train.createRow(rowCounter++);
					Cell bidCell = catRow.createCell(0);
					bidCell.setCellValue(bId);
					Cell catCell = catRow.createCell(1);
					catCell.setCellValue(cat);
					Cell textCell = catRow.createCell(2);
					textCell.setCellValue(text);
					
					if(rowCounter == 65530){
						FileOutputStream out = new FileOutputStream(new File("data"+docnumber+".xls"));
						finalDataWB.write(out);
						out.close();
						docnumber++;
						finalDataWB = new HSSFWorkbook();
						train = finalDataWB.createSheet("data"+docnumber);
						rowCounter = 1;
					}
				}
				
			}
			else{
				if(rowCounter > 2){
					FileOutputStream out = new FileOutputStream(new File("data"+docnumber+".xls"));
					finalDataWB.write(out);
					out.close();
				}
				break;
			} 
				
			
			counter++;
		}
		
	} 
	
	
	public static void writeHashMAptoExcel(HashMap<String,String> h) throws IOException{
		
		
		HSSFWorkbook CatClub = new HSSFWorkbook();
		
		HSSFSheet training = CatClub.createSheet();
		int rowCounter = 1;
		
		Iterator it = h.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pairs = (Map.Entry)it.next();
	        String tempText = pairs.getValue().toString();
	        
	        
	        HSSFRow catRow = training.createRow(rowCounter++);
			Cell catCell = catRow.createCell(1);
			catCell.setCellValue(pairs.getKey().toString());
			Cell textCell = catRow.createCell(2);
			try{
				textCell.setCellValue(tempText);
			}
			catch(Exception e){
				System.out.println(tempText);
				continue;
			}
	      
	        it.remove(); // avoids a ConcurrentModificationException
	    } 
	    
	    FileOutputStream out = new FileOutputStream(new File("categoryClubData.xls"));
	    CatClub.write(out);
		out.close();
		
	}
	
	
	
	public static void clubCategoryData() throws IOException{
		String path = "data1.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		
		HSSFWorkbook clubCatData = new HSSFWorkbook();
		
		HashMap<String,String> mapCatText = new HashMap<String,String>();
		
		HashMap<String,String> mapCatTextFinal = new HashMap<String,String>();
		
		while(true){	
			
			row = sheet.getRow(counter);
			
			if(row != null){
				String categories = row.getCell(1).getStringCellValue();
				String text = row.getCell(2).getStringCellValue();
				
				if(mapCatText.containsKey(categories))
					mapCatText.put(categories,mapCatText.get(categories)+text);
				else
					mapCatText.put(categories,text) ;
			}
			
			else
				break;
			
			counter++;
				
		}
	/*	Iterator it = mapCatText.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        String tempText = pairs.getValue().toString();
	        mapCatTextFinal.put(pairs.getKey().toString(), tempText);
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    } */
	    
	    
	  writeHashMAptoExcel(getTopWords(mapCatText));
	}
	
	
	public static String getTop(HashMap<String,Integer> map){
		
		  ValueComparator bvc =  new ValueComparator(map);
	      TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
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
		    	if(count < 51){
		    		//System.out.println(keyVal.getKey().toString()+"   "+keyVal.getValue().toString());
		    		topWords = topWords + " " + keyVal.getKey().toString();
		    	}
		    	else
		    		break;
		    	count++;
		    }
	      return topWords;
	}
	
	public static HashMap<String, String> getTopWords(HashMap<String,String> map){
		/*HSSFWorkbook catTextBook = new HSSFWorkbook();
		HSSFSheet sheet = catTextBook.getSheetAt(0);
		HSSFRow row = null;
		int rowCount = 1;
		
		while(true){
			
			row = sheet.getRow(rowCount++);
			
			
			
			if(row != null){
				HashMap<String, Integer> wordCount = new HashMap<>();
				String text = row.getCell(2).getStringCellValue();
				Set<String> uniqueWords = uniqueWordExtraction(text);
				
				for(String word : uniqueWords){
					wordCount.put(word, 0);
				}
				
				StringTokenizer st = new StringTokenizer(text); 
				while(st.hasMoreTokens()) {
					String currentWord = st.nextToken();
					if(wordCount.containsKey(currentWord)){
						wordCount.put(currentWord, wordCount.get(currentWord)+1);
					}
					else{
						wordCount.put(currentWord, 1);
					}
				} 
				
			}
		}*/
		
		Iterator it = map.entrySet().iterator();
		HashMap<String, String> topCatWords = new HashMap<String,String>();
	    while (it.hasNext()) {
	    	HashMap<String, Integer> wordCount = new HashMap<>();
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
		    
	        System.out.println(pairs.getKey().toString());
	        System.out.println(wordCount.toString());
	        topCatWords.put(pairs.getKey().toString(), getTop(wordCount));
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
		return topCatWords; 
	}

	public static void main(String args[]) throws Exception{
		uniqueWordExtraction("my!!name is mayur.mayur's friend is aniket!");
		uniqueCategoriesExtraction("[\"Bakeries\",\"Food\",\"Breakfast & Brunch\",\"Coffee & Tea\",\"Restaurants\"]");
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
				String text = row.getCell(0).getStringCellValue().toLowerCase();
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
		generateData();
		clubCategoryData();
	}
	
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
		Set<String> uniqueValues = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[-.,:;?!~\\s]+")));
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
	

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
		{
		    List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
		    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
		    {
		        @Override
		        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
		        {
		            return (o1.getValue()).compareTo( o2.getValue() );
		        }
		    } );
		
		    Map<K, V> result = new LinkedHashMap<>();
		    for (Map.Entry<K, V> entry : list)
		    {
		        result.put( entry.getKey(), entry.getValue() );
		    }
		    return result;
		}
}




class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
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