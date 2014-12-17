package com.yelpdata.ir.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.yelpdata.ir.utils.Utilities;

public class BuildFeature {
	
	public static String getWords(String text) {
	    //List<String> words = new ArrayList<String>();
		String words = null;
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
	    breakIterator.setText(text);
	    int lastIndex = breakIterator.first();
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
	           // words.add(text.substring(firstIndex, lastIndex));
	        	words = words + " "+ text.substring(firstIndex, lastIndex);
	        }
	    }

	    return words;
	}

	public static String uniqueWordExtraction(String textValue){
		Set<String> uniqueValues = new HashSet<String>(Arrays.asList(textValue.toLowerCase().split("[\\s]+")));
		String finalText= "";
		for (String term : uniqueValues){
			//System.out.println("terms = "+term);
			finalText = finalText + " " +  term;
		}
		return finalText;
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	
	public static void writeDataSet(String finalText,FileWriter writer) throws IOException{
		
		PrintWriter catWriter = new PrintWriter("categories.txt", "UTF-8");
		ArrayList<String> categori = new ArrayList<String>();
		
		
		//Read each review from the file
		String pathBidCat = "BidCatMap.xls";
		FileInputStream fileBidCat = new FileInputStream(new File(pathBidCat));
		
		HashMap<String,String> BidCatfrmFile = new HashMap<String,String>();
		
		HSSFWorkbook workbookBidCat = new HSSFWorkbook(fileBidCat);
		HSSFSheet sheetBiCat = workbookBidCat.getSheetAt(0);
		HSSFRow rowBidCat = null;
		int iterator = 1;
		String CAT = null;
		while(true){
			rowBidCat = sheetBiCat.getRow(iterator);
				if(rowBidCat !=null){
					String BID = rowBidCat.getCell(1).getStringCellValue();
					CAT = rowBidCat.getCell(2).getStringCellValue();
					
					BidCatfrmFile.put(BID, CAT);
				}
				else
					break;
				iterator++;
		}
		
		
		
		
		//Read each review from the file
		String path = "filteredRecords.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		
		//FileWriter writer = new FileWriter("FeatureSet.csv");
		while(true){
			row = sheet.getRow(counter);
			
			if(row !=null){
				String bId = row.getCell(1).getStringCellValue();
				String reviewText = row.getCell(0).getStringCellValue().toLowerCase();
				
				if(counter > 2001)
					break;
				
				if(BidCatfrmFile.get(bId) != null){
					
					Set<String> cats = Utilities.uniqueCategoriesExtraction(BidCatfrmFile.get(bId).replaceAll("[\\[\\]]", "").replaceAll("\"", ""));
					
					for(String cat: cats){
						if(!Utilities.checkDelCat(cat))
							continue;
					
						
						writer.write(System.getProperty( "line.separator" ));
						StringTokenizer st = new StringTokenizer(finalText);
						while(st.hasMoreElements()){
							String featureValue = st.nextToken();
							if(featureValue != null ){
								if(reviewText.contains(featureValue)){
									writer.append('1');
									writer.append(',');
								}
								else{
									writer.append('0');
									writer.append(',');
								}
							}
						}
//						catWriter.println(cat);
						cat = cat.trim();
						writer.append(cat.trim());
					}
				}
			}
			else
				break;
			counter++;
		}
		catWriter.close();
		
	}
	
	
	
	/*public static void main(String[] args) throws IOException {
		
		String path = "categoryClubData.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		int columCounter = 1;
		String concatText = null;
		ArrayList<List<String>> feature = new ArrayList<>();
		
		while(true){	
			
			row = sheet.getRow(counter);
			
			if(row != null){
				String text = row.getCell(2).getStringCellValue();
				concatText =concatText + " "+ getWords(text);
			}
			else
				break;
			counter++;
			
		}
		//Get the unique words for building feature vector
		String finalUniqueFeature = uniqueWordExtraction(concatText);
		
		
		//Write header to the file
		FileWriter writer = new FileWriter("FeatureSet.csv");
		StringTokenizer st = new StringTokenizer(finalUniqueFeature);
		String dataValues = null;
		while(st.hasMoreElements()){
			String featureString = st.nextToken();
			if(!isNumeric(featureString)){
				dataValues  = dataValues + " "+featureString;
				writer.append(featureString);
				writer.append(',');
			}
		}
		writer.append("Result");
		
		//Write the data from the reviews to the dataset
		writeDataSet(dataValues,writer);
		
		
		writer.flush();
	    writer.close();
	
	}*/
	
public static void build() throws IOException {
		
		String path = "categoryClubData.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		int columCounter = 1;
		String concatText = "";
		ArrayList<List<String>> feature = new ArrayList<List<String>>();
		
		while(true){	
			
			row = sheet.getRow(counter);
			
			if(row != null){
				String text = row.getCell(2).getStringCellValue();
				concatText =concatText + " "+ getWords(text);
			}
			else
				break;
			counter++;
			
		}
		//Get the unique words for building feature vector
		String finalUniqueFeature = uniqueWordExtraction(concatText);
		
		
		//Write header to the file
		FileWriter writer = new FileWriter("FeatureSet.csv");
		StringTokenizer st = new StringTokenizer(finalUniqueFeature);
		String dataValues = "";
		while(st.hasMoreElements()){
			String featureString = st.nextToken();
			featureString = featureString.replaceAll("'", "|");
			if(!isNumeric(featureString)){
				dataValues  = dataValues + " "+featureString;
				writer.append(featureString);
				writer.append(',');
			}
		}
		writer.append("Result");
		
		//Write the data from the reviews to the dataset
		writeDataSet(dataValues,writer);
		
		
		writer.flush();
	    writer.close();
	
	}

}
