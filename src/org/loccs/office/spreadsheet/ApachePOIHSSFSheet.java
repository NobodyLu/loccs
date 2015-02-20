package org.loccs.office.spreadsheet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class ApachePOIHSSFSheet extends Sheet {
	
	protected HSSFSheet sheet = null;

	public ApachePOIHSSFSheet() {
		
	}
	
	
	public ApachePOIHSSFSheet(HSSFSheet sheet) {
		this.sheet = sheet;
	}	
	

	@Override
	public Cell getCell(int row, int column) {
		if (sheet == null)
			return null;
		
		HSSFRow hssfRow = sheet.getRow(row);
		if (hssfRow == null)
			hssfRow = sheet.createRow(row);
		if (hssfRow == null)
			return null;
		
		HSSFCell cell = hssfRow.getCell(column);
		if (cell == null)
			cell = hssfRow.createCell(column);
		if (cell == null)
			return null;
		
		return new ApachePOIHSSFCell(cell);
	}

}
