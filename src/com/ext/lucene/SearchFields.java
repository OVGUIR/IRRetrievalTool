package com.ext.lucene;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
/*
 * Authors: Akhila Vockaligara Mani, Poorvi Mandyam Bhoolokam, Md Raahim Al Amin, Susmita Siddaramayya Mathapathi
 * This class invocates the method for searching multiple fields concurrently
 * The fields are fetched from a Properties.txt
 * The fileds are comma separated values where the last item ends with a comma-
 * example: content,body,title
 */
public class SearchFields {
	/**
	 * read the properties file to search multiple fields concurrently
	 * @return String array
	 * @throws IOException
	 */
	public static String[] getSearchFields() throws IOException {
		int n = 0;
		File file = new File((".\\src\\properties\\Properties.txt"));
		@SuppressWarnings("resource")
		Scanner scanner1 = new Scanner(file);
		scanner1.useDelimiter(",");
		@SuppressWarnings("resource")
		Scanner scanner2 = new Scanner(file);
		scanner2.useDelimiter(",");
		while(scanner1.hasNext()) {
			n = n+1;
		    scanner1.next();
		    }
		String[] allfields = new String[n];
	    for(int i=0;i<n;i++) {
	    	allfields[i] = scanner2.next();
	    	}
	    return allfields;
	    }
	}
