package com.ext.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;


public class Indexer 
{
 public IndexWriter writer = null;
 public File indexDirectory = null;
 public File sourceDirectory = null;
 public static int count = 0;

 	public Indexer(String sourceFilePath,String indexFilePath,String createorupdate) throws FileNotFoundException, CorruptIndexException, IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException 
 	{ 
 		try 
 		{
 			long startTime = System.currentTimeMillis();
 			if(createorupdate.equalsIgnoreCase("create")||createorupdate.equalsIgnoreCase("update"))
 			createIndexWriter(indexFilePath,createorupdate,sourceFilePath); 
 			//checkFileValidity(sourceFilePath); 
 			System.out.println("total no of files "+createorupdate+"d: "+count);
 			closeIndexWriter(); 
 			MyQueryParser.searchIndexWithQueryParser(indexFilePath);
 			long endTime = System.currentTimeMillis();
 			System.out.println("Total time Taken in milliseconds: "+(endTime-startTime));
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
 	public void createIndexWriter(String indexFilePath,String createorupdate,String sourceFilePath) 
 	{
 		try 
 		{
 			indexDirectory = new File(indexFilePath);
 			sourceDirectory = new File(sourceFilePath);
 			if (!indexDirectory.exists()) 
 			{  
 				indexDirectory.mkdir();  
 			} 
 			if (!sourceDirectory.exists()) 
 			{  
 				System.out.println("Please provide the data file for indexing"); 
 				System.exit(0);
 			} 
 			FSDirectory dir = FSDirectory.open(indexDirectory.toPath()); 
 			//System.out.println("dir    "+dir);
 			Analyzer analyzer = new MyStemmingAnalyzer(); 
 			IndexWriterConfig config = new IndexWriterConfig(analyzer);
 			//config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
 			//IndexDeletionPolicy deletionPolicy = config.getIndexDeletionPolicy();
 			//config.setIndexDeletionPolicy(IndexDeletionPolicy);
 			writer = new IndexWriter(dir, config);
 			if (createorupdate.equalsIgnoreCase("create")) {
 				// Create a new index in the directory, removing any
 				// previously indexed documents:
 				config.setOpenMode(OpenMode.CREATE);
 				checkFileValidity(sourceFilePath);
 				
 			} else if (createorupdate.equalsIgnoreCase("update"))  {
 				// Add new documents to an existing index:
 				config.setOpenMode(OpenMode.APPEND);
 				checkFileValidity(sourceFilePath);
 			}
 			else
 				System.out.println("Search the existing Index");
 			 
 		} 
 		catch (Exception ex) 
 		{ 
 			System.out.println("Sorry cannot get the index writer");
 		}
 	}

 	private void updateDocument(File file) throws IOException {
 	      Document document = new Document();

 	      //update indexes for file contents
 	      writer.updateDocument(
 	         new Term(LuceneConstants.FILE_NAME,
 	         file.getName()),document); 
 	      //writer.close();
 	   } 
 	
 //Filters out the files that can be indexed.

 	public void checkFileValidity(String sourceFilePath) 
 		{
 		
 		File[] filesToIndex = new File[200000]; // suppose there are 200000 files at max 
 		filesToIndex = new File(sourceFilePath).listFiles(); 
 		if(filesToIndex.equals(null))
 		System.out.println("Please provide the data for indexing");
 		else {
 		for (File file : filesToIndex) 
 		{
 			
 			try 
 			{ 
 				//to check whether the file is a readable file or not.
 				if(file.isDirectory())
 				{
 					checkFileValidity(file.toString());
 					
 				}
 				else if(!file.isDirectory()) {
 					
 					if (!file.isHidden() && file.exists() && file.canRead() && file.length() > 0.0 
					 && file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
 					System.out.println(file.getName());
 					//System.out.println("------------if  .txt -----------");
 					indexTextFiles(file);
 					}
 				
 					if (!file.isHidden() && file.exists() && file.canRead() && file.length() > 0.0 
 					&& file.isFile() && file.getName().toLowerCase().endsWith(".html")) 
 					{
 					System.out.println(file.getName());
 					//System.out.println("------------ if  .html -----------");
 					indexHTMLFiles(file);
 					}
 				}
 				else {
 					System.out.println(" Not a valid file format ");
 				}
 				
 			} 
 			catch (Exception e) 
 			{
 
 				System.out.println("Sorry cannot index " + file.getAbsolutePath());
 			}
 		}
 		}
 		}
 	/**
 	 * writes file to index
 	 * @param file : file to index
 	 * @return 
 	 * @throws CorruptIndexException
 	 * @throws IOException
 	 */
 	public void indexTextFiles(File file) throws CorruptIndexException, IOException 
 	{
 		/*		Fix the indexing Field.Index.NOT_ANALYZED     */
// 		FileReader fr = null;
// 		fr = new FileReader(File f);
 		Document doc = new Document(); 
 		//updateDocument(file,doc);
 		doc.add(new TextField("content", new FileReader(file)));
 		doc.add(new TextField("title", new FileReader(file)));
 		doc.add(new StringField(LuceneConstants.FILE_NAME, file.getName(),Field.Store.YES));
 		doc.add(new StringField(LuceneConstants.FILE_PATH,file.getCanonicalPath(),Field.Store.YES));
 		System.out.println("   mode  "+writer.getConfig().getOpenMode());
 		if (doc != null) {
 		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			System.out.println("adding " );
			writer.addDocument(doc);
			count++;		
		} else if (writer.getConfig().getOpenMode() == OpenMode.APPEND) {
			System.out.println("updating " + file);
			writer.updateDocument(new Term(LuceneConstants.FILE_NAME,
		    file.getName()),doc);
			count++;
		}
		else{}
 		}
 	
 	}

 	public void indexHTMLFiles(File file) throws CorruptIndexException, IOException 
    {
 		/*		Fix the indexing Field.Index.NOT_ANALYZED     */
// 		FileReader fr = null;
// 		fr = new FileReader(File f);
    	org.apache.lucene.document.Document doc = new Document();
    	org.jsoup.nodes.Document document = Jsoup.parse(file, "utf-8");
    	String title = document.title();
 		System.out.println("title*****"+document.title());
 		System.out.println("******"+document.body().toString());
 		System.out.println("******"+document.body());
 		//updateDocument(file,doc);
 		//Document doc new Document();
		doc.add(new TextField("title", new FileReader(file)));
 		doc.add(new TextField("body", file.getName(),Field.Store.YES));
 		doc.add(new StringField(LuceneConstants.FILE_NAME, file.getName(),Field.Store.YES));
 		doc.add(new StringField(LuceneConstants.FILE_PATH,file.getCanonicalPath(),Field.Store.YES));
 		if (doc != null) {
 		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			System.out.println("adding "+file);
			writer.addDocument(doc);
			count++;		
		} else if (writer.getConfig().getOpenMode() == OpenMode.APPEND) {
			System.out.println("updating " + file);
			writer.updateDocument(new Term(LuceneConstants.FILE_NAME,
		    file.getName()),doc);
			count++;
		}
		else{}
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
    	   Scanner systemscanner = new Scanner(System.in);
    	   System.out.print("Select the below option for indexing\n");
    	   System.out.print("1.create\n2.update\n3.exit\n");
    	   System.out.print("Anything other than the above options will select the available index as default\n");
    	   String createorupdate = systemscanner.next();
    	   if (createorupdate.equalsIgnoreCase("exit")) System.exit(0);
    	   System.out.println("Option selected: "+createorupdate);
           Indexer indxr1 = new Indexer("C:\\Users\\AKMANI\\Desktop\\lucene\\data\\","C:\\Users\\AKMANI\\Desktop\\lucene\\index\\",createorupdate);
                //  NOTE: we can easily repeat the same process for all 50 directories ... no need to discuss how ???
       }

}
