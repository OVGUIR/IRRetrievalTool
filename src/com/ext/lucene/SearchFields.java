package com.ext.lucene;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/* Search multiple fields concurrently (multifield search): not only search the
document’s text (body tag), but also its title */

public class SearchFields {
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
	System.out.println("allfields	");
	for(int i=0;i<n;i++) {
		allfields[i] = scanner2.next();
		System.out.println(i+"\t"+allfields[i]);
	}
	return allfields;
}
}
