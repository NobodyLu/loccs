package org.loccs.sse.experiment;

import java.util.HashMap;
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
	
	protected HashMap<String, Integer> phraseMap1 = new HashMap<String, Integer>();
	protected HashMap<String, Integer> phraseMap2 = new HashMap<String, Integer>();
	
	
	public PhraseExperiment() {
		
	}
	
	public void buildIndex() {
		DocumentCollection collection = new DocumentCollection();
		collection.addPath(documentDirectory);
		
		DocumentIndexWriter writer = new DocumentIndexWriter();
		writer.setIndexDirectory(indexDirectory);
		
		writer.build(collection, "*.*", analyzer);
	}
	
	public void SeperateAsWordsExperiment(int base, int max) {
		DocumentIndexSearcher searcher = new DocumentIndexSearcher();
		
		if (!searcher.open(indexDirectory)) 
			return;
		
		int count = searcher.getDocumentCount();
		
		int totalOkCount = 0;
		int totalFailCount = 0;
		int totalLengthBasePhraseCount = 0;
		
		for (int i = 0; i < count; i++) {
			Vector<String> words = searcher.getDocumentWords(analyzer, i);
			int size = words.size();
			
			int[] resultCounts = new int[size];
			
			int okCount = 0;
			int failCount = 0;
			
			System.out.println("Processing document " + (i + 1) + ", length: " + words.size());
			
			for (int j = 2; j <= base; j++) {
				for (int k = 0; k <= (size - j); k++) {
					int resultCount;
					
					String phrase = constructPhrase(words, k, j);
					if (phraseMap1.containsKey(phrase)) {
						resultCount = phraseMap1.get(phrase).intValue();
					}else {
						resultCount = searcher.getDocumentCountContainsPhrase(words, k, j);
						phraseMap1.put(phrase, new Integer(resultCount));
						okCount++;
						totalLengthBasePhraseCount++;
					}
					
					resultCounts[k] = resultCount;		
				}
			}

			totalOkCount += okCount;
			totalFailCount += failCount;			
			//System.out.println("Add new ok phrase (length 2) " + okCount);
			
			for (int j = (base + 1); j <= max; j++) {
				okCount = 0;
				failCount = 0;
				
				for (int k = 0; k <= (size - j); k++) {
					int resultCount;
					String phrase = constructPhrase(words, k, j);
					
					if (phraseMap1.containsKey(phrase)) {
						resultCount = phraseMap1.get(phrase).intValue();
						resultCounts[k] = resultCount;
					}else {
						if (phraseMap2.containsKey(phrase)) {
							resultCount = phraseMap2.get(phrase).intValue();
							resultCounts[k] = -1;
						} else {
							resultCount = searcher.getDocumentCountContainsPhrase(words, k, j);
							if ((resultCounts[k] == resultCount) && 
									(resultCounts[k + 1] == resultCount)) {
								phraseMap1.put(phrase, new Integer(resultCount));
								okCount++;
							}else {
								phraseMap2.put(phrase, new Integer(resultCount));
								failCount++;
								resultCounts[k] = -1;
							}
						}
					}
				}
				
				//System.out.println("Add new ok phrase (length " + j + ") " + okCount);
				//System.out.println("Add new fail phrase (length " + j + ") " + failCount);
				totalOkCount += okCount;
				totalFailCount += failCount;				
			}
		}
		
		System.out.println("Total length <= " + base + " phrase " + totalLengthBasePhraseCount);
		System.out.println("Total ok phrase " + totalOkCount + ", save " + (totalOkCount - totalLengthBasePhraseCount));
		System.out.println("Total fail phrase " + totalFailCount);		
	}
	
	protected String constructPhrase(Vector<String> phrase, int start, int length) {
		String full = "";
		
		for (int i = start; i < (start + length); i++)
			full = full + phrase.get(i) + " ";
		
		return full;
	}	

	public static void main(String[] args) {
		PhraseExperiment experiment = new PhraseExperiment();
		
		//experiment.buildIndex();
		
		experiment.SeperateAsWordsExperiment(2, 8);
	}

}
