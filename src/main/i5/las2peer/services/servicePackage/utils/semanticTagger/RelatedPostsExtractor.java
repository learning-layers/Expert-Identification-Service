/**
 * 
 */
package i5.las2peer.services.servicePackage.utils.semanticTagger;

import i5.las2peer.services.servicePackage.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author sathvik
 *
 */
public class RelatedPostsExtractor {
    String expertId;
    DatabaseHandler dbHandler;
    String expertCollectionId;

    int maxCount = 5;

    public RelatedPostsExtractor(DatabaseHandler dbHandler, String expertCollectionId, String expertId) {
	this.dbHandler = dbHandler;
	this.expertId = expertId;
	this.expertCollectionId = expertCollectionId;
    }

    public String getPosts() {
	String experts = this.dbHandler.getExperts(this.expertCollectionId);
	ArrayList<String> labels = getRelatedPosts(experts);

	StringBuilder posts = new StringBuilder();
	for (String postId : labels) {
	    posts.append(dbHandler.getPost(Long.parseLong(postId.trim())));
	    posts.append("<hr style=\"border-color:red\">");

	}

	// return Joiner.on(",").join(reqTags);
	return posts.toString();

    }

    private ArrayList<String> getRelatedPosts(String experts) {
	String relatedPosts = null;
	JsonParser jparser = new JsonParser();
	JsonArray jarr = (JsonArray) jparser.parse(experts);
	for (int i = 0; i < jarr.size(); i++) {
	    JsonObject jobj = jarr.get(i).getAsJsonObject();
	    if (jobj.get("userId").getAsString().equals(expertId)) {
		relatedPosts = jobj.get("relatedPosts").getAsString();
	    }
	}

	relatedPosts = relatedPosts.substring(1, relatedPosts.length() - 1);

	return new ArrayList<String>(Arrays.asList(relatedPosts.split(",")));
    }

}
