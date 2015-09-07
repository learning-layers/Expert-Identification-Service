package i5.las2peer.services.servicePackage.textProcessor;

import java.util.HashMap;

/**
 * @author sathvik
 */

public class StopWordList {

    public HashMap<String, String[]> lang2stopwords = new HashMap<String, String[]>();
    private String[] lucene_stopwordlist = { "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it",
	    "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with" };

    private String[] estonian_stopwordlist = { "ja", "ning", "ehk", "või", "saa", "muidu", "kle", "ple", "krt", "tra", "tre", "kax", "irw",
	    "icc", "sex", "vtu", "fck", "omk", "vää", "rsk", "sle", "mle" };

    public StopWordList() {
	lang2stopwords.put("en", lucene_stopwordlist);
	lang2stopwords.put("et", estonian_stopwordlist);
    }

    public String[] getStopWords(String lang) {
	if (lang2stopwords.containsKey(lang)) {
	    return lang2stopwords.get(lang);
	}
	
	return null;
    }

}
