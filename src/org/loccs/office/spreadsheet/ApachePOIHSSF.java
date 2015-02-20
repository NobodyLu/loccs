package org.loccs.office.spreadsheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ApachePOIHSSF extends SpreadSheet {
	
	protected HSSFWorkbook workbook = null;
	protected String filename = "";

	public ApachePOIHSSF() {
		
	}

	@Override
	public boolean create() {
		workbook = new HSSFWorkbook();
		workbook.createSheet("sheet1");
		return true;
	}

	@Override
	public boolean open(String filename) {
		try {
			
			FileInputStream file = new FileInputStream(new File(filename));
			workbook = new HSSFWorkbook(file);
			this.filename = filename;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public int getSheetCount() {
		if (workbook != null)
			return workbook.getNumberOfSheets();
		else
			return 0;
	}

	@Override
	public Sheet getSheet(int index) {
		if (workbook == null)
			return null;
		
		HSSFSheet sheet = workbook.getSheetAt(index);
		return new ApachePOIHSSFSheet(sheet);
	}

	@Override
	public Sheet createSheet(String name) {
		if (workbook == null)
			return null;

		HSSFSheet sheet = workbook.createSheet(name);
		return new ApachePOIHSSFSheet(sheet);
	}

	@Override
	public boolean save() {
		if (workbook == null || filename.isEmpty())
			return false;
		
		try {
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean saveAs(String filename) {
		this.filename = filename;	
		return save();
	}

	@Override
	public void close() {
		workbook = null;
		filename = "";
	}

}
