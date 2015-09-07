/**
 * 
 */
package i5.las2peer.services.servicePackage.utils.semanticTagger;

import i5.las2peer.services.servicePackage.database.DatabaseHandler;
import i5.las2peer.services.servicePackage.utils.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author sathvik
 *
 */
public class TagExtractor {
    String expertId;
    DatabaseHandler dbHandler;
    String expertCollectionId;

    int maxCount = 5;

    public TagExtractor(DatabaseHandler dbHandler, String expertCollectionId, String expertId) {
	this.dbHandler = dbHandler;
	this.expertId = expertId;
	this.expertCollectionId = expertCollectionId;
    }

    public String getTags() {

	String experts = this.dbHandler.getExperts(this.expertCollectionId);
	ArrayList<String> labels = getRelatedPosts(experts);
	ArrayList<String> reqTags = new ArrayList<String>();

	try {
	    HashMap<String, Integer> tag2count = new HashMap<String, Integer>();
	    if (labels != null && labels.size() > 0) {
		for (String label : labels) {
		    if (label != null) {
			String tags = this.dbHandler.getSemanticTags(label);
			// System.out.println("TAGS::  " + tags);
			// Count the tag frequency in all the posts.
			if (tags != null && tags.length() > 0) {
			    String[] tagAr = tags.split(",");
			    if (tagAr != null && tagAr.length > 0) {
				for (String tag : tagAr) {
				    if (tag != null && tag.length() > 0 && Application.getFilterWords().contains(tag) == false) {
					if (tag2count.containsKey(tag)) {
					    tag2count.put(tag, tag2count.get(tag) + 1);
					} else {
					    tag2count.put(tag, 1);
					}
				    }
				}
			    }
			}
		    }

		}

		// Sort on tag frequency.
		LinkedHashMap<String, Integer> tag2countSorted = Application.sortByValue(tag2count);
		Iterator<String> it = tag2countSorted.keySet().iterator();

		int count = 0;
		while (it.hasNext() && count < maxCount) {
		    reqTags.add(it.next());
		    count++;
		}

	    }
	} catch (Exception e) {
	    System.out.print(e.toString());
	}
	// System.out.println("TAGS::  " + reqTags);
	return Joiner.on(",").join(reqTags);

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
