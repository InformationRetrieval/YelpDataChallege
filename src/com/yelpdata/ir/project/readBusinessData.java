package com.yelpdata.ir.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class readBusinessData {
	
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
			
			//Set<String> uniqueValues = uniqueCategoriesExtraction(cat.replaceAll("[\\[\\]]", ""));
		
			//Print Unique Categories
			/*for (String term : uniqueValues){
				System.out.println(term);
			}*/
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
}
