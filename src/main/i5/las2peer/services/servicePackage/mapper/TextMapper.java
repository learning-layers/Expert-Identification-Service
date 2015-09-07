/**
 * 
 */
package i5.las2peer.services.servicePackage.mapper;

import i5.las2peer.services.servicePackage.models.Token;
import i5.las2peer.services.servicePackage.utils.Application;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @author sathvik
 * 
 *         This class is used to create maps required for modeling strategy.
 *         It creates token frequency map and (Term frequency - Inverse resource
 *         frequency)TF-IRF map
 *
 */
public class TextMapper {

    private Log log = LogFactory.getLog(TextMapper.class);

    private HashMap<Object, Integer> word2totalFreq;
    private HashMap<Long, Long> postId2userId;
    private Map<Long, Double> postid2tfirf;

    private HashMultimap<Long, Token> postid2Tokens = HashMultimap.create();
    private int noOfDocs;

    private static String dataIndexBasePath = "luceneIndex/%s/data";

    public TextMapper(int docsCount) throws IOException {
	clear();

	word2totalFreq = new HashMap<Object, Integer>();
	postId2userId = new HashMap<Long, Long>();
	postid2tfirf = new HashMap<Long, Double>();
	postid2Tokens = HashMultimap.create();

	noOfDocs = docsCount;

    }

    /**
     * 
     * @param docs
     *            Documents that are relevant to the query terms, obtained by
     *            lucene document hits.
     * @param queryString
     *            String representing the query.
     * @param filepath
     *            Path of the index file to search for the matched documents for
     *            the query string.
     * 
     * @throws IOException
     *             Exception thrown if the index file is not present in the
     *             given path.
     */
    public void buildMaps(TopDocs docs, String queryString, String filepath) throws IOException {
	addTokenFreqMap(docs, filepath, queryString);
	createInverseResFreqMap();
    }

    /**
     * 
     * @param docs
     *            Documents that are relevant to the query terms, obtained by
     *            lucene document hits.
     * @param filepath
     *            Path of the index file to search for the matched documents for
     *            the query string.
     * @throws IOException
     *             Exception thrown if the index file is not present in the
     *             given path.
     */
    private void addTokenFreqMap(TopDocs docs, String filepath, String queryString) throws IOException {
	IndexSearcher dataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(dataIndexBasePath, filepath))
		.toPath())));

	HashMultiset queryTerms = HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(queryString));

	for (ScoreDoc scoreDoc : docs.scoreDocs) {
	    Document doc = dataSearcher.doc(scoreDoc.doc);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

	    String text = doc.get("searchableText");

	    Multiset<String> bagOfWords = HashMultiset.create(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(text));
	    Token token;

	    for (Object word : queryTerms.elementSet()) {
		log.info("WORD:: " + word);
		int count = bagOfWords.count(word);
		int totalWordfreq = word2totalFreq.get((String) word) != null ? word2totalFreq.get((String) word) + count : 0;

		word2totalFreq.put((String) word, totalWordfreq);

		token = new Token(postId, text);
		token.setFrequnecy(count);
		token.setName((String) word);

		log.info("POST ID:: " + postId);
		postid2Tokens.put(postId, token);
		if (userId > 0) {
		    postId2userId.put(postId, userId);
		}
	    }

	}

    }

    /**
     * This method creates map of post to TF-IDF. In this case Term frequency
     * Inverse resource frequency (TFIRF)
     * 
     * @see <a href="https://en.wikipedia.org/wiki/Tf–idf">tf-idf</a>
     * 
     */
    private void createInverseResFreqMap() {
	// log.info("Creating Inverse Freq Map...");
	try {
	    for (Map.Entry entry : postid2Tokens.entries()) {
		Long postid = (Long) entry.getKey();
		Set<Token> tokens = postid2Tokens.get(postid);
		double tfirf = 0; // TFIRF of individual token combined for
				  // entire post.
		for (Token token : tokens) {
		    int termFreq = token.getFreq();

		    int tokenCount = word2totalFreq.containsKey(token.getName()) ? word2totalFreq.get(token.getName()) : 0;
		    double irfweight = 1;
		    if (tokenCount > 0) {
			irfweight = Application.round((double) Math.log(noOfDocs / word2totalFreq.get(token.getName())), 2);
		    }
		    tfirf = tfirf + (termFreq * irfweight);

		    token.setTfIrf(termFreq * irfweight);
		}
		log.info("Creating TFIRF Map...");
		postid2tfirf.put(postid, tfirf);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	// log.info("Creating Inverse Freq Map completed...");
    }

    public HashMap<Long, Long> getPostId2UserIdMap() {
	return postId2userId;
    }

    public Map<Long, Double> getTfIrfMap() {
	return postid2tfirf;
    }

    public HashMultimap<Long, Token> getTokenMap() {
	return postid2Tokens;
    }

    /**
     * Clears all the map created.
     */
    private void clear() {
	if (word2totalFreq != null && word2totalFreq.isEmpty() == false)
	    word2totalFreq.clear();
	if (postId2userId != null && postId2userId.isEmpty() == false)
	    postId2userId.clear();
	if (postid2tfirf != null && postid2tfirf.isEmpty() == false)
	    postid2tfirf.clear();
	if (postid2Tokens != null && postid2Tokens.isEmpty() == false)
	    postid2Tokens.clear();

    }
}
