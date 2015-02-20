package org.loccs.office.spreadsheet;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class ApachePOIHSSFCell extends Cell {
	
	protected HSSFCell cell = null;

	public ApachePOIHSSFCell() {

	}
	
	public ApachePOIHSSFCell(HSSFCell cell) {
		this.cell = cell;
	}	

	@Override
	public String getStringValue() {
		if (cell == null)
			return null;
		else
			return cell.getStringCellValue();
	}

	@Override
	public double getNumericValue() {
		if (cell == null)
			return 0;
		else
			return cell.getNumericCellValue();
	}

	@Override
	public boolean setStringValue(String value) {
		if (cell == null)
		{
			return false;
		}else
		{
			cell.setCellValue(value);
			return true;
		}
	}

	@Override
	public boolean setNumericValue(double value) {
		if (cell == null)
		{
			return false;
		}else
		{
			cell.setCellValue(value);
			return true;
		}
	}

}
