package com.yelpdata.ir.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.yelpdata.ir.utils.CreateExcel;
import com.yelpdata.ir.utils.Utilities;

public class ClubCatData {
	
	public static HashMap<String,String> mapCatText = new HashMap<String,String>();
	public static HashMap<String,ArrayList<String>> globalCat = new HashMap<String,ArrayList<String>>();
	
	public static void clubCategoryData() throws IOException{
		String path = "data1.xls";
		FileInputStream file = new FileInputStream(new File(path));
		
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		int counter = 1;
		
		HSSFWorkbook clubCatData = new HSSFWorkbook();
		
		
		
		HashMap<String,String> mapCatTextFinal = new HashMap<String,String>();
		
		
		int c = 0;
		while(true){	
			
			row = sheet.getRow(counter);
			
			if(row != null){
				String categories = row.getCell(1).getStringCellValue();
				String text = row.getCell(2).getStringCellValue();
				
				System.out.println("Cat : "+categories+" "+c++);
				
				/*
				if(!Utilities.checkDelCat(categories)){
					//System.out.println("Category Ignored : "+categories);
					continue;
				}
				*/
				
				//Store the cat and coreesoping reviews groups
				if(globalCat.containsKey(categories)){
					ArrayList<String> Listreview = globalCat.get(categories);
					Listreview.add(text);
					globalCat.put(categories,Listreview);
				}
				else{
					ArrayList<String> tempList = new ArrayList<String>();
					tempList.add(text);
					globalCat.put(categories, tempList);
				}
				
				if(Utilities.checkDelCat(categories)){
					if(mapCatText.containsKey(categories))
						mapCatText.put(categories,mapCatText.get(categories)+text);
					else
						mapCatText.put(categories,text) ;
				}
			}
			
			else
				break;
			counter++;
				
		}
		//System.out.println(globalCat.toString());
		/*	Iterator it = mapCatText.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        String tempText = pairs.getValue().toString();
	        mapCatTextFinal.put(pairs.getKey().toString(), tempText);
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    } */
	    
	  System.out.println("HashMap loaded!!");
	  CreateExcel.writeHashMAptoExcel(TopWords.getWordScores(mapCatText),"categoryClubData.xls");
	  System.out.println("categoryClubData.xls file written");
	  //CreateExcel.writeListHashToExcel(globalCat,"globalCat.xls");
	}
	
	
}
