package org.loccs.document;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class DocumentCollection {
	
	protected Vector<String> paths = new Vector<String>();
	
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
	
	public Vector<String> refreshDocuments(String glob) {
		Vector<String> documents = new Vector<String>();
		
		for (int i = 0; i < paths.size(); i++) {
			Path path = Paths.get(paths.get(i));
			
			try {
				DirectoryStream<Path> stream = Files.newDirectoryStream(path, glob);
				for (Path file: stream) {
					documents.add(file.toString());
				}
			} catch (IOException e) {
				System.out.println("DocumentCollection: Fail to refresh path " + paths.get(i));
				e.printStackTrace();
			}
		}
		
		return documents;
	}
	
	public int getTotalFileSize(int percentage) {
		int size = 0;
		
		Vector<String> filenames = refreshDocuments("*.*");
		int count = filenames.size() * percentage / 100;
		for (int i = 0; i < count; i++) {
			File file =new File(filenames.get(i));
			size += file.length();
		}
		
		return size;
	}

	public static void main(String[] args) {
		DocumentCollection collection = new DocumentCollection();
		
		collection.addPath("D:\\Source Code\\test_data\\documents\\news");
		
		Vector<String> documents = collection.refreshDocuments("T*.txt");
		
		System.out.println("Documents count: " + documents.size());
		
		for (int i = 0; i < documents.size(); i++)
			System.out.println("File " + (i + 1) + ": " + documents.get(i));
		
	}

}
