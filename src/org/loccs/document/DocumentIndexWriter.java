package org.loccs.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
 
public class DocumentIndexWriter {
	
	protected String directory = "./";
	
	public DocumentIndexWriter() {
		
	}
	
	public void setIndexDirectory(String directory) {
		this.directory = directory;
	}
	
	public boolean build(DocumentCollection collection, String glob, Analyzer analyzer) {
		try {
			Directory dir = FSDirectory.open(new File(directory));
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
			config.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, config);
			Vector<String> filenames = collection.refreshDocuments(glob);
			for (int i = 0; i < filenames.size(); i++) 
				addDocument(writer, filenames.get(i));
			
			writer.close();
		} catch (IOException e) {
			System.out.println("DocumentIndexWriter: Fail to build index on " + directory);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected boolean addDocument(IndexWriter writer, String filename) {
		Document document = new Document();
		document.add(new StringField("filename", filename, Field.Store.YES));
		try {
			FileInputStream input = new FileInputStream(filename);
			String content = IOUtils.toString(input);
			document.add(new TextField("content", content, Field.Store.YES));
			
			writer.addDocument(document);
			input.close();
		} catch (IOException e) {
			System.out.println("DocumentIndexWriter: Fail to add document " + filename);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		
		DocumentCollection collection = new DocumentCollection();
		collection.addPath("D:\\Source Code\\loccs\\data\\documents\\news");
		
		DocumentIndexWriter writer = new DocumentIndexWriter();
		writer.setIndexDirectory("D:\\Source Code\\loccs\\data\\index\\news");
		
		writer.build(collection, "*.*", new SimpleAnalyzer());
	}

}
