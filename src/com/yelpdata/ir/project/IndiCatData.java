package com.yelpdata.ir.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import com.yelpdata.ir.utils.CreateExcel;
import com.yelpdata.ir.utils.Utilities;

public class IndiCatData {

	public static HashMap<String,ArrayList<String>> BidCatMap = new HashMap<String,ArrayList<String>>();
	
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
				
				
//				if(!BidCatMap.containsKey(bId)){
//					ArrayList<String> tempCatList = new ArrayList<String>();
//					BidCatMap.put(bId, tempCatList);
//				}
				
				//System.out.println("Bid : "+bId);
				//System.out.println("categories : "+categories);
				//System.out.println("text : "+text);
				
				Set<String> cats = Utilities.uniqueCategoriesExtraction(categories.replaceAll("[\\[\\]]", "").replaceAll("\"", ""));
			
				
				for(String cat : cats){
					if(Utilities.checkDelCat(cat)){
						if(!BidCatMap.containsKey(bId)){
							ArrayList<String> tempCatList = new ArrayList<String>();
							tempCatList.add(cat);
							BidCatMap.put(bId, tempCatList);
						}
						else{
							ArrayList<String> tempList = BidCatMap.get(bId);
							//System.out.println("Cat ignored : "+cat);
							tempList.add(cat);
							BidCatMap.put(bId, tempList);
						}
					}
					
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
		CreateExcel.writeListHashToExcel(BidCatMap,"BidCatMap.xls");
		
	} 
}
