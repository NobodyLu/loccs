package org.loccs.document;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class DocumentIndexSearcher {
	
	protected IndexReader reader = null;
	protected IndexSearcher searcher = null;
	
	public DocumentIndexSearcher() {
		
	}
	
	public boolean open(String directory) {
		try {
			Directory dir = FSDirectory.open(new File(directory));
			reader = DirectoryReader.open(dir);
			searcher = new IndexSearcher(reader);
		} catch (IOException e) {
			System.out.println("DocumentIndexSearcher: Fail to open index directory " + directory);
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public int getDocumentCount() {
		return reader.numDocs();
	}
	
	public Vector<String> getAllWords() {
		Vector<String> words = new Vector<String>();
		try {
			Fields fields = MultiFields.getFields(reader);
			if (fields != null) {
				Terms terms = fields.terms("content");
				if (terms != null) {
					TermsEnum termsEnum = terms.iterator(null);
					BytesRef bytesRef;
					while ((bytesRef = termsEnum.next()) != null) {
						words.add(bytesRef.utf8ToString());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("DocumentIndexSearcher: Fail to get all terms.");
			e.printStackTrace();
		}
		
		return words;
	}
	
	public Vector<String> searchWord(String word) {
		Vector<String> results = new Vector<String>();
		
		Query query = new TermQuery(new Term("content", word.toLowerCase()));
		try {
			TopDocs docs = searcher.search(query, reader.numDocs());
			for (ScoreDoc doc : docs.scoreDocs) {
				Document document = searcher.doc(doc.doc);
				results.add(document.get("filename"));
			}
		} catch (IOException e) {
			System.out.println("DocumentIndexSearcher: Fail to search word " + word);
			e.printStackTrace();
		}
		
		return results;
	}
	
	public Vector<String> searchPhrase(String[] phrase) {
		Vector<String> results = new Vector<String>();
		
		String full = "";
		
		PhraseQuery query = new PhraseQuery();
		for (int i = 0; i < phrase.length; i++) {
			query.add(new Term("content", phrase[i].toLowerCase()));
			full = full + phrase[i] + " ";
		}
		
		try {
			TopDocs docs = searcher.search(query, reader.numDocs());
			for (ScoreDoc doc : docs.scoreDocs) {
				Document document = searcher.doc(doc.doc);
				results.add(document.get("filename"));
			}
		} catch (IOException e) {
			System.out.println("DocumentIndexSearcher: Fail to search phrase " + full);
			e.printStackTrace();
		}		
		
		return results;
	}
	
	public Vector<String> getDocumentWords(Analyzer analyzer, int doc) {
		Vector<String> words = new Vector<String>();
		
		try {
			Document document = searcher.doc(doc);
			TokenStream stream = document.getField("content").tokenStream(analyzer, null);
			CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				words.add(charTermAttribute.toString());
			}
			
		} catch (IOException e) {
			System.out.println("DocumentIndexSearcher: Fail to get document words.");
			e.printStackTrace();
		}
		
		return words;
	}
	
	public void close() {
		
		searcher = null;
		
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
				System.out.println("DocumentIndexSearcher: Fail to close.");
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		DocumentIndexSearcher searcher = new DocumentIndexSearcher();
		if (!searcher.open("D:\\Source Code\\loccs\\data\\index\\news")) 
			return;
		
		System.out.println("Document count: " + searcher.getDocumentCount());
		
		Vector<String> words = searcher.getAllWords();
		System.out.println("Word count: " + words.size());
		
		Vector<String> results = searcher.searchWord("google");
		System.out.println("Results count: " + results.size());
		for (int i = 0; i < results.size(); i++)
			System.out.println(results.get(i));
		System.out.println("");
		
		String[] phrase = new String[2];
		phrase[0] = "were";
		phrase[1] = "also";
		results = searcher.searchPhrase(phrase);
		System.out.println("Results count: " + results.size());
		for (int i = 0; i < results.size(); i++)
			System.out.println(results.get(i));		
		
		System.out.println("");
		words = searcher.getDocumentWords(new SimpleAnalyzer(), 0);
		System.out.println("Word count: " + words.size());
		for (int i = 0; i < words.size(); i++) 
			System.out.print(words.get(i) + " ");
		System.out.println("");
		
		searcher.close();
	}

}
