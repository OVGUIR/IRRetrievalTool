package com.ext.lucene;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

public class MyQueryParser {
	public static void searchIndexWithQueryParser(String indexfilepath) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException{
		
		//String[] fields = SearchFields.getSearchFields();
		//String[] fields = {"filename", "contents", "description"};
		Query query =null;
		int hitsPerPage = 10;
		File indexDirectory = new File(indexfilepath);
		//System.out.println("    indexfilepath     "+indexfilepath);
		FSDirectory dir = FSDirectory.open(indexDirectory.toPath());
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new MyStemmingAnalyzer();
		String querystring = "swim";
		MultiFieldQueryParser parser  = new MultiFieldQueryParser(SearchFields.getSearchFields(), analyzer);
		parser.setAllowLeadingWildcard(true);
		query = parser.parse(querystring);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        System.out.println("Query string: " + querystring );
        System.out.println("Found " + hits.length + " hits."); 
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document document = searcher.doc(docId);
            System.out.println("doc found"+document);
            documents.add(document);
           
          //  System.out.println((i + 1) + ". " + d.get("content") + "\t" + d.get("title"));
        }
//        for (int i = 0; i < documents.size(); ++i) {
//        	System.out.println(documents);
//        }
        }


}