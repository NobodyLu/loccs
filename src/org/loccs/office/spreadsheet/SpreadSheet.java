package org.loccs.office.spreadsheet;

public abstract class SpreadSheet {
	
	public SpreadSheet() {

	}
	
	public abstract boolean create();
	
	public abstract boolean open(String filename);
	
	public abstract int getSheetCount();
	
	public abstract Sheet getSheet(int index);
	
	public abstract Sheet createSheet(String name);
	
	public abstract boolean save();
	
	public abstract boolean saveAs(String filename);
	
	public abstract void close();
}
