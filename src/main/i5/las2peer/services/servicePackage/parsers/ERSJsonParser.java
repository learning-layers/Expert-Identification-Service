/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.parsers.xmlparser.DataField;
import i5.las2peer.services.servicePackage.parsers.xmlparser.User;
import i5.las2peer.services.servicePackage.parsers.xmlparser.Users;

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
import org.apache.http.util.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author sathvik
 *
 */
public class ERSJsonParser implements IParser<List<Post>, List<User>> {
    private Log log = LogFactory.getLog(ERSJsonParser.class);

    private JAXBContext context;
    private String postsFilePath;
    private String usersFilePath;

    private static final String DATA_FILENAME = "posts.json";
    private static final String USER_FILENAME = "users.json";

    public ERSJsonParser(String dirPath) {
	postsFilePath = dirPath + "/" + DATA_FILENAME;
	usersFilePath = dirPath + "/" + USER_FILENAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IParser#getPosts()
     */
    @Override
    public List<Post> getPosts() {

	DataField dataField = null;
	File file = new File("config/data_mapping.xml");
	try {

	    context = JAXBContext.newInstance(DataField.class);
	    Unmarshaller um = context.createUnmarshaller();
	    dataField = (DataField) um.unmarshal(file);

	} catch (JAXBException e) {
	    e.printStackTrace();
	}

	Path postPath = Paths.get(postsFilePath);

	String jsonStr = null;
	try {
	    jsonStr = new String(Files.readAllBytes(postPath));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Specific for Sevianno. Will be used to extract userInfo.
	Path objectPath = Paths.get(usersFilePath);
	String SeViAnnoObject = null;
	try {
	    SeViAnnoObject = new String(Files.readAllBytes(objectPath));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	List<Post> posts = new ArrayList<Post>();

	try {

	    JsonParser jsonParser = new JsonParser();
	    JsonArray jsonArr = (JsonArray) jsonParser.parse(jsonStr);

	    JsonArray SeViAnnoArr = (JsonArray) jsonParser.parse(SeViAnnoObject);

	    for (JsonElement elem : jsonArr) {
		JsonObject jsonObj = elem.getAsJsonObject();
		Post post = new Post();

		if (!TextUtils.isEmpty(dataField.getPostIdName())) {
		    post.setPostId(jsonObj.get(dataField.getPostIdName()).getAsString());
		}

		if (!TextUtils.isEmpty(dataField.postTypeIdFieldName)) {
		    post.setPostTypeId(jsonObj.get(dataField.postTypeIdFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(dataField.creationDateFieldName)) {
		    post.setCreationDate(jsonObj.get(dataField.creationDateFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(dataField.scoreFieldName)) {
		    post.setScore(jsonObj.get(dataField.scoreFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(dataField.bodyFieldName)) {

		    post.setBody(jsonObj.get(dataField.bodyFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(dataField.ownerUserIdFieldName)) {
		    // For SeviaAnno, use SeViAnnoObject and parse for username
		    // with objectId of annotation to map id of the object.
		    String id = "-1";
		    for (JsonElement SeVielem : SeViAnnoArr) {
			JsonObject SeViObj = SeVielem.getAsJsonObject();
			id = SeViObj.get(dataField.ownerUserIdFieldName).getAsString();

			if (Long.parseLong(id) == Long.parseLong(jsonObj.get(dataField.getPostIdName()).getAsString())) {
			    String author = SeViObj.get("author").getAsJsonObject().get("name").getAsString();
			    post.setOwnerUserId(author);

			}

		    }

		    // post.setOwnerUserId(jsonObj.get(dataField.ownerUserIdFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(dataField.titleFieldName)) {
		    post.setTitle(jsonObj.get(dataField.titleFieldName).getAsString());
		}
		if (!TextUtils.isEmpty(dataField.parentIdFieldName)) {
		    post.setParentId(jsonObj.get(dataField.parentIdFieldName).getAsString());
		}

		posts.add(post);

	    }
	} catch (Exception e) {
	    log.info("Exception while json parsing");
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
	List<User> user_list = null;
	File users_file = new File(usersFilePath);

	try {

	    context = JAXBContext.newInstance(Users.class);
	    Unmarshaller um1 = context.createUnmarshaller();
	    Users users = (Users) um1.unmarshal(users_file);
	    user_list = (ArrayList) users.getUsersList();

	} catch (JAXBException e) {
	    e.printStackTrace();
	}
	return user_list;
    }

}
