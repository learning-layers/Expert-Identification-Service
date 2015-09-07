/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.parsers.xmlparser.DataField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author sathvik
 *
 */
public class SeviAnnoParser implements IParser<List<Post>, List<User>> {
    private Log log = LogFactory.getLog(ERSJsonParser.class);

    private JAXBContext context;
    private String postsPath;
    private String usersDataPath;

    public SeviAnnoParser(String path) {
	try {
	    JsonElement jelement = new JsonParser().parse(path);
	    JsonObject jobject = jelement.getAsJsonObject();
	    postsPath = jobject.get("posts").getAsString();
	    usersDataPath = jobject.get("users").getAsString();
	} catch (JsonParseException e) {
	    e.printStackTrace();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IParser#getPosts()
     */
    @Override
    public List<Post> getPosts() {
	DataField parameter = null;

	try {

	    context = JAXBContext.newInstance(DataField.class);
	    Unmarshaller um = context.createUnmarshaller();
	    File file = new File("config/data_mapping.xml");
	    parameter = (DataField) um.unmarshal(file);

	} catch (JAXBException e) {
	    e.printStackTrace();
	}

	String jsonStr = null;
	try {
	    if (postsPath.startsWith("http://")) {
		jsonStr = getStringFromRemoteUrl(postsPath);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	String userObject = null;
	if (usersDataPath != null && usersDataPath.startsWith("http://")) {
	    try {
		userObject = getStringFromRemoteUrl(usersDataPath);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} else {
	    Path objectPath = Paths.get(usersDataPath);
	    try {
		userObject = new String(Files.readAllBytes(objectPath));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	List<Post> posts = new ArrayList<Post>();

	try {

	    JsonParser jsonParser = new JsonParser();
	    JsonArray jsonArr = (JsonArray) jsonParser.parse(jsonStr);

	    JsonArray SeViAnnoArr = (JsonArray) jsonParser.parse(userObject);

	    for (JsonElement elem : jsonArr) {
		JsonObject jsonObj = elem.getAsJsonObject();
		Post post = new Post();

		if (!TextUtils.isEmpty(parameter.getPostIdName())) {
		    post.setPostId(jsonObj.get(parameter.getPostIdName()).getAsString());
		}

		if (!TextUtils.isEmpty(parameter.postTypeIdFieldName)) {
		    post.setPostTypeId(jsonObj.get(parameter.postTypeIdFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(parameter.creationDateFieldName)) {
		    post.setCreationDate(jsonObj.get(parameter.creationDateFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(parameter.scoreFieldName)) {
		    post.setScore(jsonObj.get(parameter.scoreFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(parameter.bodyFieldName)) {

		    post.setBody(jsonObj.get(parameter.bodyFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(parameter.ownerUserIdFieldName)) {
		    String author = jsonObj.get("author").getAsJsonObject().get("name").getAsString();
		    post.setOwnerUserId(author);

		    // For rest of the system.
		    // post.setOwnerUserId(jsonObj.get(dataField.ownerUserIdFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(parameter.titleFieldName)) {
		    post.setTitle(jsonObj.get(parameter.titleFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(parameter.parentIdFieldName)) {
		    post.setParentId(jsonObj.get(parameter.parentIdFieldName).getAsString());
		}

		posts.add(post);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return posts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IParser#getUsers()
     */
    @Override
    public List<User> getUsers() {
	DataField parameter = null;

	try {
	    context = JAXBContext.newInstance(DataField.class);
	    Unmarshaller um = context.createUnmarshaller();
	    File file = new File("config/user_mapping.xml");
	    parameter = (DataField) um.unmarshal(file);

	} catch (JAXBException e) {
	    e.printStackTrace();
	}
	String jsonStr = null;
	try {
	    jsonStr = getStringFromRemoteUrl(usersDataPath);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	String userObject = null;
	if (usersDataPath != null) {
	    try {
		userObject = getStringFromRemoteUrl(usersDataPath);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	List<User> users = new ArrayList<User>();

	try {

	    JsonParser jsonParser = new JsonParser();
	    JsonArray jsonArr = (JsonArray) jsonParser.parse(jsonStr);

	    for (JsonElement elem : jsonArr) {
		JsonObject jsonObj = elem.getAsJsonObject();
		User user = new User();

		String author = jsonObj.get("author").getAsJsonObject().get("name").getAsString();
		user.setUserId(String.valueOf(author.hashCode()));
		user.setUserName(author);
		users.add(user);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}


	return users;
    }

    private String getStringFromRemoteUrl(String url) throws ClientProtocolException, IOException {
	HttpClient client = HttpClientBuilder.create().build();
	HttpGet request = new HttpGet(postsPath);
	HttpResponse response = client.execute(request);
	HttpEntity entity = response.getEntity();
	return EntityUtils.toString(entity);
    }

}
