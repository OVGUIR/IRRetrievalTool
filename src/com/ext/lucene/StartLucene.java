package com.ext.lucene;

	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileReader;
	import java.io.IOException;
	import java.nio.file.Paths;
	import org.apache.lucene.analysis.standard.StandardAnalyzer;
	import org.apache.lucene.document.Document;
	import org.apache.lucene.document.TextField;
	import org.apache.lucene.index.DirectoryReader;
	import org.apache.lucene.index.IndexReader;
	import org.apache.lucene.index.IndexWriter;
	import org.apache.lucene.index.IndexWriterConfig;
	import org.apache.lucene.index.IndexWriterConfig.OpenMode;
	import org.apache.lucene.queryparser.classic.ParseException;
	import org.apache.lucene.queryparser.classic.QueryParser;
	import org.apache.lucene.search.IndexSearcher;
	import org.apache.lucene.search.Query;
	import org.apache.lucene.search.TopDocs;
	import org.apache.lucene.store.Directory;
	import org.apache.lucene.store.FSDirectory;

	public class StartLucene {

		public static void main(String[] args) throws IOException, ParseException {
			// New index
			StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
			String inputFilePath = "C:\\Users\\AKMANI\\Desktop\\lucene\\input\\text1.txt";
			String outputDir = "C:\\Users\\AKMANI\\Desktop\\lucene\\output";
			String inputquery = "Hello";
			File file = new File(inputFilePath);

			Directory directory = FSDirectory.open(Paths.get(outputDir));
			IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
			config.setOpenMode(OpenMode.CREATE);
			// Create a writer
			IndexWriter writer = new IndexWriter(directory, config);

			Document document = new Document();
			try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {

				document.add(new TextField("content", br));
				writer.addDocument(document);
				writer.close();
				System.out.println("document   "+document);

			} catch (IOException e) {
				e.printStackTrace();
			}
			

			// Now let's try to search for Hello
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser("content", standardAnalyzer);
			Query query = parser.parse(inputquery);
			TopDocs results = searcher.search(query, 5);
			System.out.println("Hits for Hello -->" + results.totalHits);

			// case insensitive search
			query = parser.parse("hello");
			results = searcher.search(query, 5);
			System.out.println("Hits for hello -->" + results.totalHits);

			// search for a value not indexed
			query = parser.parse("Hi there");
			results = searcher.search(query, 5);
			System.out.println("Hits for Hi there -->" + results.totalHits);
		}
	
}