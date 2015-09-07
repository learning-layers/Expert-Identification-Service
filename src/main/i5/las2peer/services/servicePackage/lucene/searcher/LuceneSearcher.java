package i5.las2peer.services.servicePackage.lucene.searcher;

import i5.las2peer.services.servicePackage.models.SemanticToken;
import i5.las2peer.services.servicePackage.models.Token;
import i5.las2peer.services.servicePackage.utils.semanticTagger.SemanticTagger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;

import com.google.common.collect.HashMultimap;

/**
 * This class uses Apache Lucene searcher to search for the documents containing
 * the query terms.
 * Indexes are searched in the folder where it was original created.
 * This is also used to create Question to Answers map and posts to author map.
 * 
 * 
 * @author sathvik
 *
 */
public class LuceneSearcher {
    private IndexSearcher dataSearcher = null;
    private IndexSearcher semanticDataSearcher = null;

    private QueryParser dataParser = null;
    private String queryString = null;

    private HashMultimap<Long, Token> postid2Tokens = HashMultimap.create();
    private HashMultimap<Long, Long> parentId2postIds = HashMultimap.create();

    private HashMap<Long, Long> parentId2postId = new HashMap<Long, Long>();

    private HashMap<Long, Long> postId2userId;
    private QueryParser semanticDataParser;

    private int maxNoOfResults = Integer.MAX_VALUE;

    private static String dataIndexBasePath = "luceneIndex/%s/data";

    /**
     * 
     * @param query
     * @param indexFilepath
     * @throws IOException
     */
    public LuceneSearcher(String query, String indexFilepath) throws IOException {
	dataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(dataIndexBasePath, indexFilepath)).toPath())));
	semanticDataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(dataIndexBasePath, indexFilepath))
		.toPath())));

	dataParser = new QueryParser("searchableText", new StandardAnalyzer());
	semanticDataParser = new QueryParser("searchableText", new StandardAnalyzer());

	queryString = query;

	postId2userId = new HashMap<Long, Long>();

    }

    /**
     * 
     * @param queryString
     *            A query string.
     * @param n
     *            Integer value for the maximum number of results to be returned
     *            by lucene searcher.
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public TopDocs performSearch(String queryString, int n) throws IOException, ParseException {
	Query query = dataParser.parse(queryString);
	TopDocs docs = dataSearcher.search(query, n);
	System.out.println("No of HITS:: " + docs.totalHits);
	maxNoOfResults = n;

	return docs;
    }

    public TopDocs performSemanticSearch() throws IOException, ParseException {
	SemanticTagger tagger = new SemanticTagger(queryString);
	String tags = tagger.getSemanticData().getTags().replaceAll(",", " ");
	tags = tags + queryString; // Add query terms to the semantic tags to
				   // search(Experimental, can remote
				   // queryString)
	TopDocs docs = null;
	System.out.println(tags);
	try {
	    Query terms = null;
	    if (tags != null && tags.length() > 0) {
		terms = semanticDataParser.parse(QueryParser.escape(tags));
		docs = semanticDataSearcher.search(terms, maxNoOfResults);
	    }
	} catch (ParseException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	// System.out.println("No of Semantic HITS:: " + docs.totalHits);

	return docs;
    }

    public int getTotalNumberOfDocs() {
	return dataSearcher.getIndexReader().numDocs();
    }

    /**
     * 
     * @param docs
     * @throws IOException
     */
    public void buildQnAMap(TopDocs docs, Date date) throws IOException {
	Token token = null;
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	Date fmtCreationDate = null;

	// This condition is specifically for Phpbb forums where parentId is not
	// the postId but another unique id.
	for (ScoreDoc scoreDoc : docs.scoreDocs) {
	    Document doc = dataSearcher.doc(scoreDoc.doc);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
	    long postTypeId = doc.get("postTypeId") != null ? Long.parseLong(doc.get("postTypeId")) : -1;

	    // If it is a question, for Phpbb forum
	    if (postTypeId == 1) {
		parentId2postId.put(parentId, postId);
	    }

	}

	for (ScoreDoc scoreDoc : docs.scoreDocs) {
	    Document doc = dataSearcher.doc(scoreDoc.doc);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
	    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

	    // String creationDate = doc.get("creationDate");
	    //
	    // if (creationDate != null) {
	    // try {
	    // // If timestamp, use this.
	    // // fmtCreationDate =new
	    // // java.util.Date(Long.parseLong(creationDate)*1000);
	    // fmtCreationDate = format.parse(creationDate);
	    // } catch (java.text.ParseException e) {
	    // e.printStackTrace();
	    // }
	    // }

	    String text = doc.get("searchableText");
	    // System.out.println(postId + " :: " + parentId);

	    if (postId > 0) {
		token = new Token(postId, text);
		token.setFrequnecy(0);
	    }

	    postid2Tokens.put(postId, token);

	    // If parentId is present and the post was created before the
	    // requested date add the value to the map.
	    if (parentId > 0 && fmtCreationDate != null) {
		// System.out.println("Parent Id > 0"+parentId);
		// This condition is specifically for Phpbb forums where
		// parentId is not the postId but another id.
		if (parentId2postId.size() > 0) {
		    parentId = parentId2postId.get(parentId); // Retrieve the id
		    // of the question.
		}
		parentId2postIds.put(parentId, postId);

	    }
	    // System.out.println("USERID::" + userId);
	    if (userId > 0) {
		postId2userId.put(postId, userId);
	    }

	}

	searchSemantics(date);

    }

    public void buildQnANetworkMap() throws IOException {
	IndexReader reader = dataSearcher.getIndexReader();
	Document doc = null;
	Token token = null;


	Bits liveDocs = MultiFields.getLiveDocs(reader);
	for (int i = 0; i < reader.maxDoc(); i++) {
	    if (liveDocs != null && !liveDocs.get(i))
		continue;

	    doc = reader.document(i);

	    long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
	    long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
	    long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;
	    String text = doc.get("searchableText");

	    if (postId > 0) {
		token = new Token(postId, text);
		token.setFrequnecy(0);
	    }

	    postid2Tokens.put(postId, token);
	    if (parentId > 0) {
		parentId2postIds.put(parentId, postId);
	    }
	    if (userId > 0) {
		postId2userId.put(postId, userId);
	    }
	}

    }

    private void searchSemantics(Date date) {

	System.out.println("Search in Semantics...");
	SemanticToken token = null;
	try {
	    TopDocs docs = performSemanticSearch();
	    for (ScoreDoc scoreDoc : docs.scoreDocs) {
		Document doc = dataSearcher.doc(scoreDoc.doc);

		long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
		long parentId = doc.get("parentid") != null ? Long.parseLong(doc.get("parentid")) : -1;
		long userId = doc.get("userid") != null ? Long.parseLong(doc.get("userid")) : -1;

		String text = doc.get("searchableText");
		// System.out.println(postId + " :: " + parentId);

		if (postId > 0) {
		    token = new SemanticToken(postId, text);
		    token.setFrequnecy(0);
		}

		postid2Tokens.put(postId, token);

		// If parentId is present and the post was created before
		// the
		// requested date add the value to the map.

		if (parentId > 0) {
		    parentId2postIds.put(parentId, postId);
		}

		// System.out.println("USERID::" + userId);
		if (userId > 0) {
		    postId2userId.put(postId, userId);
		}
	    }
	} catch (ParseException e1) {
	    e1.printStackTrace();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

    }

    public HashMap<Long, Long> getPostId2UserIdMap() {
	return postId2userId;
    }

    public Map<Long, Collection<Long>> getQnAMap() {
	return (Map<Long, Collection<Long>>) parentId2postIds.asMap();
    }

    public Document getDocument(int docId) throws IOException {
	return dataSearcher.doc(docId);
    }
}
