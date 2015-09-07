package i5.las2peer.services.servicePackage.utils.semanticTagger;

import i5.las2peer.services.servicePackage.models.SemanticData;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * @author sathvik
 */

public class SemanticTagger {
    private String text;
    private ArrayList<String> tags;
    public String TAG_ME_KEY = "174baff695d027d98674a1ebcf84d50c";
    public String TAGME_URL = "http://tagme.di.unipi.it/tag";

    /**
     * 
     * @param text
     */
    public SemanticTagger(String text) {
	this.text = text;
    }

    /**
     * 
     * @param tagstr
     */
    public void setTags(String tagstr) {
	tags = new ArrayList<String>(Arrays.asList(tagstr.split(",")));
    }

    public HashMultiset<String> getTokens() {
	return HashMultiset.create(Splitter.on(",").omitEmptyStrings().split(getSemanticData().getTags()));
    }

    /**
     * 
     * @return
     */
    // TODO: Move extraction of DbCategories to SemanticData class.
    public SemanticData getSemanticData() {

	JsonArray annotations = new JsonArray();
	SemanticData annotationObj = null;

	if (text != null && text.length() > 0) {

	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(TAGME_URL);
	    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    nvps.add(new BasicNameValuePair("key", TAG_ME_KEY));
	    nvps.add(new BasicNameValuePair("text", text));
	    nvps.add(new BasicNameValuePair("include_categories", "true"));

	    CloseableHttpResponse response = null;

	    try {
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		response = httpclient.execute(httpPost);
		// System.out.println(response.getStatusLine());

		HttpEntity entity = response.getEntity();
		String responseString = EntityUtils.toString(entity, "UTF-8");

		JsonParser parser = new JsonParser();
		JsonElement jelement = parser.parse(responseString);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray jarray = jobject.getAsJsonArray("annotations");

		JsonObject jobj = null;
		StringBuilder tags = new StringBuilder();
		for (int i = 0; i < jarray.size(); i++) {
		    jobject = jarray.get(i).getAsJsonObject();
		    JsonPrimitive rho = jobject.getAsJsonPrimitive("rho");
		    JsonPrimitive title = jobject.getAsJsonPrimitive("title");

		    JsonArray categories = jobject.getAsJsonArray("dbpedia_categories");

		    if (Float.parseFloat(rho.getAsString()) >= 0.1) {
			jobj = new JsonObject();
			jobj.add("tags", categories);
			jobj.add("title", title);
			jobj.add("rho", rho);
			annotations.add(jobj);
			if (categories != null && categories.size() > 0) {
			    for (JsonElement jelem : categories) {
				if (jelem != null) {
				    tags.append(jelem.getAsString());
				    tags.append(" , ");
				}
			    }
			}
		    }
		}

		// System.out.println("ANNOTATIONS " + annotations);
		// System.out.println("TAGS " + tags);

		if (annotations != null && tags != null) {
		    annotationObj = new SemanticData(annotations.toString(), tags.toString());
		}

		// Release all the underlying resource.
		EntityUtils.consume(entity);

	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		try {
		    response.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}

	    }
	}
	return annotationObj;
    }
}
