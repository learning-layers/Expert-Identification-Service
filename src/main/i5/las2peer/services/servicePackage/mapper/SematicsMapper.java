/**
 * 
 */
package i5.las2peer.services.servicePackage.mapper;

import i5.las2peer.services.servicePackage.database.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.models.SemanticToken;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.semanticTagger.SemanticTagger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */
public class SematicsMapper {
    HashMultiset<String> mEntities;
    HashMultiset<String> mQueryEntities;

    private HashMultimap<Long, SemanticToken> postId2EntityToken;
    private Map<Long, Double> postId2efirf;
    private HashMap<String, Integer> totalEntityFreq;

    private long totalNoResources = 0;
    private ConnectionSource mConnectionSource;

    private static String semanticsIndexBasePath = "luceneIndex/%s/semantics";

    public SematicsMapper(ConnectionSource connectionSrc) {
	mConnectionSource = connectionSrc;

	postId2EntityToken = HashMultimap.create();
	// postId2InverseSemanticEntity = HashMultimap.create();
	postId2efirf = new HashMap<Long, Double>();
	totalEntityFreq = new HashMap<String, Integer>();
    }

    public void buildIndex(TopDocs docs, String queryString, String filepath) {
	SemanticTagger tagger = new SemanticTagger(queryString);
	mQueryEntities = tagger.getTokens();

	try {
	    IndexSearcher dataSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(String.format(semanticsIndexBasePath,
		    filepath))
		    .toPath())));

	    Dao<SemanticTagEntity, Long> tagDao = DaoManager.createDao(mConnectionSource, SemanticTagEntity.class);
	    for (ScoreDoc scoreDoc : docs.scoreDocs) {

		Document doc = dataSearcher.doc(scoreDoc.doc);
		long postId = doc.get("postid") != null ? Long.parseLong(doc.get("postid")) : -1;
		SemanticTagEntity tag_entity = tagDao.queryForId(postId);

		if (tag_entity != null) {
		    addTagEntityFreqMap(tag_entity.getPostId(), tag_entity.getAnnotations());
		}
		totalNoResources = tagDao.countOf();

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	createEfirfMap();

    }

    private void addTagEntityFreqMap(long postid, String annotations) {
	mEntities = HashMultiset.create();

	if (annotations != null) {

	    // TODO:Use mapper to map from json to the class, instead of parsing
	    JsonParser parser = new JsonParser();
	    JsonArray jarr = (JsonArray) parser.parse(annotations);
	    JsonArray tagArr = null;
	    double confidenceVal = 0;
	    SemanticToken token;

	    HashMap<String, SemanticToken> tag2tagRes = new HashMap<String, SemanticToken>();
	    for (int i = 0; i < jarr.size(); i++) {
		JsonElement obj = ((JsonObject) jarr.get(i)).get("tags");
		// System.out.println(postid + " " + obj);
		if (obj != null && obj.isJsonNull() == false) {
		    tagArr = obj.getAsJsonArray();
		    confidenceVal = ((JsonObject) jarr.get(i)).get("rho").getAsDouble();
		    for (JsonElement elem : tagArr) {
			String entity = elem.getAsString();
			mEntities.add(entity);

			token = new SemanticToken(postid, annotations);
			token.setConfidenceVal(confidenceVal);
			token.setName(entity);
			if (tag2tagRes.containsKey(entity) == false) {
			    tag2tagRes.put(entity, token);
			} else {
			    SemanticToken temp = tag2tagRes.get(entity);
			    temp.setFrequnecy(temp.getFreq() + 1);
			}
		    }
		}
	    }

	    // Iterate the Query entities and create a map for only required
	    // entities.
	    int totalcount = 0;
	    for (String queryEntity : mQueryEntities.elementSet()) {
		if (totalEntityFreq.get(queryEntity) != null && tag2tagRes.get(queryEntity) != null) {
		    totalcount = totalEntityFreq.get(queryEntity) + tag2tagRes.get(queryEntity).getFreq();
		}

		// Used for IEFreq
		if (totalcount > 0) {
		    totalEntityFreq.put(queryEntity, totalcount);
		}

		postId2EntityToken.put(postid, tag2tagRes.get(queryEntity));
	    }

	    tag2tagRes.clear();
	}

    }

    private void createEfirfMap() {
	try {
	    for (Map.Entry entry : postId2EntityToken.entries()) {
		long postid = (long) entry.getKey();
		Set<SemanticToken> resources = (Set<SemanticToken>) postId2EntityToken.get(postid);
		double sum_efirf = 0; // EFIRF of individual resource combined,
				      // for
				      // the entire post.
		for (SemanticToken entityRes : resources) {
		    if (entityRes != null) {
			int entityFreq = entityRes.getFreq();
			double confidenceVal = entityRes.getConfidenceVal();
			double irfweight = 0;
			if (totalEntityFreq.containsKey(entityRes.getName())) {
			    irfweight = Application.round((double) Math.log(totalNoResources / totalEntityFreq.get(entityRes.getName())), 2);
			}
			// Each Entity is associated with confidence Value.
			sum_efirf = sum_efirf + (entityFreq * irfweight * confidenceVal);

			entityRes.setTfIrf(entityFreq * irfweight);
			// postId2InverseSemanticEntity.put(postid, entityRes);
		    }
		}
		postId2efirf.put(postid, sum_efirf);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Map<Long, Double> getEfirfMap() {
	return postId2efirf;
    }
}
