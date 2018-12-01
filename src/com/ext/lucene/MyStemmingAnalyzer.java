package com.ext.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

/* Consider the English language and use a stemmer for it (e.g. Porter Stemmer) */

public class MyStemmingAnalyzer extends Analyzer{
	@Override
	protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
		Tokenizer source = new ClassicTokenizer();
		TokenStream filter = new StandardFilter(source);
		filter = new LowerCaseFilter(filter);
		//filter = new SynonymFilter(filter,getSynonymsMap(),false);
		return new Analyzer.TokenStreamComponents(source,new PorterStemFilter(filter));
	}
	private SynonymMap getSynonymsMap(){
		try {
			SynonymMap.Builder builder = new SynonymMap.Builder(true);
			builder.add(new CharsRef("finished"), new CharsRef("completed"),true);
			builder.add(new CharsRef("finished"), new CharsRef("over"),true);
			builder.add(new CharsRef("finished"), new CharsRef("ended"),true);
			SynonymMap mySynonymMap = builder.build(); 
			return mySynonymMap;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}