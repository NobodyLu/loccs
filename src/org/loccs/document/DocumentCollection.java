package org.loccs.document;


import java.util.HashMap;
import java.util.Vector;

public class DocumentCollection {
	
	protected Vector<String> paths = new Vector<String>();
	protected HashMap<Integer, String> documents = new HashMap<Integer, String>();
	
	public DocumentCollection() {
		
	}
	
	public void addPath(String path) {
		if (!paths.contains(path))
			paths.add(path);
	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> getPaths() {
		return (Vector<String>) paths.clone();
	}
	
	public void refreshDocuments() {
		documents.clear();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
