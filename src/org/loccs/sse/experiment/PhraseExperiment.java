package org.loccs.sse.experiment;

import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.loccs.document.DocumentCollection;
import org.loccs.document.DocumentIndexSearcher;
import org.loccs.document.DocumentIndexWriter;

public class PhraseExperiment {
	
	protected String documentDirectory = "D:\\Source Code\\loccs\\data\\documents\\news";
	protected String indexDirectory = "D:\\Source Code\\loccs\\data\\index\\news";
	
	protected Analyzer analyzer = new SimpleAnalyzer();
	
	public PhraseExperiment() {
		
	}
	
	public void buildIndex() {
		DocumentCollection collection = new DocumentCollection();
		collection.addPath(documentDirectory);
		
		DocumentIndexWriter writer = new DocumentIndexWriter();
		writer.setIndexDirectory(indexDirectory);
		
		writer.build(collection, "*.*", analyzer);
	}
	
	public void SeperateAsTwoWordsExperiment() {
		DocumentIndexSearcher searcher = new DocumentIndexSearcher();
		
		if (!searcher.open(indexDirectory)) 
			return;
		
		int count = searcher.getDocumentCount();
		
		for (int i = 0; i < count; i++) {
			Vector<String> words = searcher.getDocumentWords(analyzer, i);
		}
	}

	public static void main(String[] args) {
		PhraseExperiment experiment = new PhraseExperiment();
		
		//experiment.buildIndex();
		
		experiment.SeperateAsTwoWordsExperiment();
	}

}
