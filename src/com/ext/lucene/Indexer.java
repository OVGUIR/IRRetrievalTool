package com.ext.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Indexer 
{
 public IndexWriter writer = null;
 public File indexDirectory = null;
 public static int count = 0;

 	public Indexer(String sourceFilePath,String indexFilePath) throws FileNotFoundException, CorruptIndexException, IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException 
 	{ 
 		try 
 		{
 			createIndexWriter(indexFilePath); 
 			checkFileValidity(sourceFilePath); 
 			System.out.println("total no of files indexed: "+count);
 			closeIndexWriter(); 
 			MyQueryParser.searchIndexWithQueryParser(indexFilePath);
 		} 
 		catch (ParseException e) 
 		{ 
 			System.out.println("Corrupt:Sorry task cannot be completed"); 
 		}
 		
 	}
 /**
 * IndexWriter writes the data to the index.
 * analyzer : its a standard analyzer, in this case it filters out englishStopWords and also analyses TFIDF
 */
 	public void createIndexWriter(String indexFilePath) 
 	{
 		try 
 		{
 			indexDirectory = new File(indexFilePath); //here "indexDirectory" is the physical directory address like: "C:\dataset\" etc.
 			if (!indexDirectory.exists()) 
 			{  
 				indexDirectory.mkdir();  
 			} 
 			FSDirectory dir = FSDirectory.open(indexDirectory.toPath()); 
 			//System.out.println("dir    "+dir);
 			Analyzer analyzer = new MyStemmingAnalyzer(); 
 			IndexWriterConfig config = new IndexWriterConfig(analyzer);
 			writer = new IndexWriter(dir, config); 
 		} 
 		catch (Exception ex) 
 		{ 
 			System.out.println("Sorry cannot get the index writer");
 		}
 	}

 //Filters out the files that can be indexed.

 	public void checkFileValidity(String sourceFilePath) 
 		{
 		
 		File[] filesToIndex = new File[200000]; // suppose there are 200000 files at max 
 		filesToIndex = new File(sourceFilePath).listFiles(); 
 		for (File file : filesToIndex) 
 		{
 			
 			try 
 			{ 
 				//to check whether the file is a readable file or not.
 				if(file.isDirectory())
 				{
 					checkFileValidity(file.toString());
 					
 				}
 				if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && file.length() > 0.0 
					 && file.isFile() && file.getName().toLowerCase().endsWith(".txt")) 
 				{
 					count++;
 					System.out.println(file.getName());
 					//System.out.println("------------if  .txt -----------");
 					indexTextFiles(file);
 				}
 				if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && file.length() > 0.0 
 					&& file.isFile() && file.getName().toLowerCase().endsWith(".html")) 
 	 			{
 					count++;
 					System.out.println(file.getName());
 					//System.out.println("------------ if  .html -----------");
 	 				indexTextFiles(file);
 	 			}
 				
 			} 
 			catch (Exception e) 
 			{
 
 				System.out.println("Sorry cannot index " + file.getAbsolutePath());
 			}
 		}
 		}
 	/**
 	 * writes file to index
 	 * @param file : file to index
 	 * @throws CorruptIndexException
 	 * @throws IOException
 	 */
 	public void indexTextFiles(File file) throws CorruptIndexException, IOException 
 	{
 		/*		Fix the indexing Field.Index.NOT_ANALYZED     */
// 		FileReader fr = null;
// 		fr = new FileReader(File f);
 		Document doc = new Document(); 
 		doc.add(new TextField("content", new FileReader(file))); 
 		doc.add(new StringField(LuceneConstants.FILE_NAME, file.getName(),Field.Store.YES));
 		doc.add(new StringField(LuceneConstants.FILE_PATH,file.getCanonicalPath(),Field.Store.YES));
 		if (doc != null) 
 		{
 			writer.addDocument(doc); 
 		}
 	}

	// Closes the IndexWriter
 	public void closeIndexWriter() 
 	{
 		try 
 		{ 
 			//writer.optimize(); 
 			writer.close(); 
 		} 
 		catch (Exception e) 
 		{
 			System.out.println("Indexer Cannot be closed");
 		}
 	}

//Main function to call the indexer
       public static void main(String[] args) throws FileNotFoundException, CorruptIndexException, IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException 
       {
                 Indexer indxr1 = new Indexer("C:\\Users\\AKMANI\\Desktop\\lucene\\data\\","C:\\Users\\AKMANI\\Desktop\\lucene\\index\\");
                //  NOTE: we can easily repeat the same process for all 50 directories ... no need to discuss how ???
       }

}
