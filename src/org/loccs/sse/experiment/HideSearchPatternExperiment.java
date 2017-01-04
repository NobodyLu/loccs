package org.loccs.sse.experiment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

public class HideSearchPatternExperiment {
	
	protected String documentDirectory = "D:\\Source Code\\loccs\\data\\documents\\rfclong";
	protected String indexDirectory = "D:\\Source Code\\loccs\\data\\index\\rfclong";
	
	protected String wordCountStatisticsFile = "D:\\Source Code\\loccs\\results\\document\\hidesp\\wordcount.xls";
	protected String groupStatisticsFile = "D:\\Source Code\\loccs\\results\\document\\hidesp\\group.xls";
	protected int wordCountSheet = 2;
	
	protected Analyzer analyzer = new SimpleAnalyzer();
	
	protected DocumentCollection collection = null;
	
	public void buildIndex(int percentage) {
		collection = new DocumentCollection();
		collection.addPath(documentDirectory);
		
		DocumentIndexWriter writer = new DocumentIndexWriter();
		writer.setIndexDirectory(indexDirectory);
		
		writer.build(collection, "*.*", analyzer, percentage);		
	}
	
	public void wordCountExperiment()
	{
		SpreadSheet spread = new ApachePOIHSSF();
		if (!spread.open(wordCountStatisticsFile)) {
			System.out.println("Fail to open statistics file.");
			return;
		}
		Sheet sheet = spread.getSheet(wordCountSheet);
		
		for (int i = 1; i <= 10; i++) {
			buildIndex(i * 10);
			
			DocumentIndexSearcher searcher = new DocumentIndexSearcher();
			if (!searcher.open(indexDirectory)) 
				return;
			
			Cell cell;
			cell = sheet.getCell(1 + i, 1);
			cell.setStringValue(Integer.toString(i * 10));
			
			cell = sheet.getCell(1 + i, 2);
			cell.setStringValue(Integer.toString(collection.getTotalFileSize(i * 10)));
			
			cell = sheet.getCell(1 + i, 3);
			cell.setStringValue(Integer.toString(searcher.getAllWords().size()));
			
			searcher.close();
		}
		
		spread.save();
		spread.close();
	}
	
	public void groupExperiment() {
		for (int i = 1; i <= 10; i++) {
			buildIndex(i * 10);
			DocumentIndexSearcher searcher = new DocumentIndexSearcher();
			if (!searcher.open(indexDirectory)) 
				return;
			
			Map<String, Set<String>> groups = new HashMap<String, Set<String>>();
			
			Vector<String> words = searcher.getAllWords(); 
			for (int j = 0; j < words.size(); j++) {
				String word = words.get(j);
				String results = searcher.searchWordForDocID(word).toString();
				if (groups.containsKey(results)) {
					Set<String> groupWords = groups.get(results);
					groupWords.add(word);
				}else {
					Set<String> groupWords = new HashSet<String>();
					groupWords.add(word);
					groups.put(results, groupWords);
				}
			}
			
			searcher.close();
			
			int threshold = 20;
			int good = 0;
			for (Entry<String, Set<String>> entry : groups.entrySet()) {
				//System.out.println("Results : " + entry.getKey() + ", Count: " + entry.getValue().size() +   ", Words : " + entry.getValue());
				int count = entry.getValue().size();
				if (count > threshold)
					good += count;
			}
			
			System.out.println("Percentage " + i * 10 + ", group count: " + groups.size() + ", good count: " + good);
		}
	}

	public static void main(String[] args) {
		HideSearchPatternExperiment experiment = new HideSearchPatternExperiment();
		
		//experiment.wordCountExperiment();
		experiment.groupExperiment();
	}

}
