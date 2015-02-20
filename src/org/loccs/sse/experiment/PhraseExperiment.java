package org.loccs.sse.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.loccs.document.DocumentCollection;
import org.loccs.document.DocumentIndexSearcher;
import org.loccs.document.DocumentIndexWriter;
import org.loccs.office.spreadsheet.ApachePOIHSSF;
import org.loccs.office.spreadsheet.Cell;
import org.loccs.office.spreadsheet.Sheet;
import org.loccs.office.spreadsheet.SpreadSheet;

public class PhraseExperiment {
	
	protected String documentDirectory = "D:\\Source Code\\loccs\\data\\documents\\rfc1000";
	protected String indexDirectory = "D:\\Source Code\\loccs\\data\\index\\rfc1000";
	
	protected String statisticsFile = "D:\\Source Code\\loccs\\results\\document\\phrase\\statistics.xls";
	
	protected static int MAX_PHRASE_LENGTH = 16;
	
	protected Analyzer analyzer = new SimpleAnalyzer();
	
	protected int[] phraseCounts = new int[MAX_PHRASE_LENGTH];
	
	List<Map<String, Integer>> phraseMaps = new ArrayList<Map<String, Integer>>();
	
	
	
	protected HashMap<String, Integer> phraseMap1 = new HashMap<String, Integer>();
	protected HashMap<String, Integer> phraseMap2 = new HashMap<String, Integer>();
	
	
	public PhraseExperiment() {
		for (int i = 2; i <= MAX_PHRASE_LENGTH; i++) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			phraseMaps.add(map);
		}
	}
	
	public void buildIndex() {
		DocumentCollection collection = new DocumentCollection();
		collection.addPath(documentDirectory);
		
		DocumentIndexWriter writer = new DocumentIndexWriter();
		writer.setIndexDirectory(indexDirectory);
		
		writer.build(collection, "*.*", analyzer);
	}
	
	public void statistics(int sheet)  {
		DocumentIndexSearcher searcher = new DocumentIndexSearcher();
		if (!searcher.open(indexDirectory)) 
			return;
		
		int n = searcher.getDocumentCount();
		int block = n / 10;
		int[] counts = new int[n];
		
		Set<String> phrases = new HashSet<String>();
		
		SpreadSheet spread = new ApachePOIHSSF();
		if (!spread.open(statisticsFile)) {
			System.out.println("Fail to open statistics file.");
			return;
		}
		Sheet sheet1 = spread.getSheet(sheet);
		Sheet sheet2 = spread.getSheet(sheet + 1);
		
		for (int i = 2; i <= MAX_PHRASE_LENGTH; i++) {
			
			System.out.println("Stat phrases of length " + i);
			
			Cell cell;
			
			for (int j = 1; j <= n; j++) {
				cell = sheet2.getCell(j + 1, 1);
				cell.setNumericValue(j);
				counts[j - 1] = 0;
				if (j % block == 0) {
					cell = sheet1.getCell(1, j / block + 1);
					cell.setNumericValue(j);
				}
			}
			
			phrases.clear();
			cell = sheet1.getCell(i, 1);
			cell.setNumericValue(i);
			cell = sheet2.getCell(1, i);
			cell.setNumericValue(i);
			
			for (int j = 1; j <= n; j++) {
				Vector<String> words = searcher.getDocumentWords(analyzer, j - 1);
				
				int size = words.size();
				
				for (int k = 0; k <= (size - i); k++) {
					String phrase = constructPhrase(words, k, i);
					if (!phrases.contains(phrase)) {
						phrases.add(phrase);
						int count = searcher.getDocumentCountContainsPhrase(words, k, i);
						if (count == 0)
							System.out.println("Fail to find phrase: " + phrase);
						else
							counts[count - 1]++;
					}
				}
				
				if (j % block == 0) {
					cell = sheet1.getCell(i, j / block + 1);
					cell.setNumericValue(phrases.size());
				}
			}
			
			for (int j = 0; j < n; j++) {
				cell = sheet2.getCell(j + 2, i);
				cell.setNumericValue(counts[j]);
			}
		}
		
		spread.save();
		spread.close();
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
		System.out.println("");
	}
	
	protected Map<String, Integer> getPhraseMap(int length) {
		if ((length < 2) || (length > MAX_PHRASE_LENGTH))
			return null;
		
		return phraseMaps.get(length - 2);
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
		
		experiment.statistics(2);
	}

}
