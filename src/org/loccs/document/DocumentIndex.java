package org.loccs.document;

public class DocumentIndex {
	
	protected String indexDirectory = "./"; 
	
	public DocumentIndex() {
		
	}
	
	public void setIndexDirectory(String directory) {
		indexDirectory = directory;
	}
	
	public boolean build(DocumentCollection collection) {
		
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
