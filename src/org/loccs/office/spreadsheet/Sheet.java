package org.loccs.office.spreadsheet;

public abstract class Sheet {

	public Sheet() {

	}
	
	public abstract Cell getCell(int row, int column);
}
