package org.loccs.office.spreadsheet;

public abstract class Cell {

	public Cell() {

	}
	
	public abstract String getStringValue();
	
	public abstract double getNumericValue();
	
	public abstract boolean setStringValue(String value);
	
	public abstract boolean setNumericValue(double value);

}
