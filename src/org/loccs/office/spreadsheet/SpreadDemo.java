package org.loccs.office.spreadsheet;

public class SpreadDemo {
	
	public SpreadDemo() {
		
	}

	public static void main(String[] args) {
		SpreadSheet spread = new ApachePOIHSSF();
		spread.create();
		System.out.println("Sheet count: "+ spread.getSheetCount());
		
		Sheet sheet = spread.getSheet(0);
		Cell cell = sheet.getCell(0, 0);
		cell.setStringValue("Hello");
		
		cell = sheet.getCell(0, 1);
		cell.setNumericValue(300);
		
		cell = sheet.getCell(5, 10);
		cell.setStringValue("test");
		
		spread.saveAs("test.xls");
	}

}
