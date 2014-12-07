package com.yelpdata.ir.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class CreateExcel {

	
	public static void writeHashMAptoExcel(HashMap<String,String> h,String fileName) throws IOException{
		
		
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
	    
	    FileOutputStream out = new FileOutputStream(new File(fileName));
	    CatClub.write(out);
		out.close();
		
	}
	
}
