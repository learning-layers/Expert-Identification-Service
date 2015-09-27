/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.parsers.xmlparser.DataField;
import i5.las2peer.services.servicePackage.parsers.xmlparser.UserField;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.TextUtils;
import org.jfree.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author sathvik
 *
 */
public class XMLParser implements IParser<List<Post>, List<User>> {

    private List<Post> posts = null;
    private List<User> users = null;

    private HashMap<String, String> field2Value = new HashMap<String, String>();
    private String postsPath;
    private String usersPath;

    public XMLParser() {
	posts = new ArrayList<Post>();
	users = new ArrayList<User>();

    }

    public void parseData(String path, boolean isLocal) {
	postsPath = path;
	try {

	    final DataField dataField = new ParameterExtractor().extractDataFields();

	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser saxParser = factory.newSAXParser();

	    DefaultHandler handler = new DefaultHandler() {

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		    Post post = new Post();
		    field2Value.clear();

		    // Collect all values.
		    for (int i = 0; i < attributes.getLength(); i++) {
			field2Value.put(attributes.getQName(i), attributes.getValue(i));
		    }

		    // Set the posts.
		    if (!TextUtils.isEmpty(dataField.postidFieldName)) {
			post.setPostId(field2Value.get(dataField.getPostIdName()));
		    }

		    if (!TextUtils.isEmpty(dataField.postTypeIdFieldName)) {
			post.setPostTypeId(field2Value.get(dataField.postTypeIdFieldName));
		    }
		    if (!TextUtils.isEmpty(dataField.creationDateFieldName)) {
			post.setCreationDate(field2Value.get(dataField.creationDateFieldName));
		    }
		    if (!TextUtils.isEmpty(dataField.scoreFieldName)) {
			post.setScore(field2Value.get(dataField.scoreFieldName));
		    }
		    if (!TextUtils.isEmpty(dataField.bodyFieldName)) {

			post.setBody(field2Value.get(dataField.bodyFieldName));
		    }
		    if (!TextUtils.isEmpty(dataField.ownerUserIdFieldName)) {
			post.setOwnerUserId(field2Value.get(dataField.ownerUserIdFieldName));
		    }
		    if (!TextUtils.isEmpty(dataField.titleFieldName)) {
			post.setTitle(field2Value.get(dataField.titleFieldName));
		    }
		    if (!TextUtils.isEmpty(dataField.parentIdFieldName)) {
			post.setParentId(field2Value.get(dataField.parentIdFieldName));
		    }

		    posts.add(post);

		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
		}

		public void characters(char ch[], int start, int length) throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

	    };

	    if (isLocal) {
		saxParser.parse(postsPath, handler);
	    } else {
		Log.info("POST PATH " + postsPath);
		URL postsUrl = new URL(postsPath);
		saxParser.parse(postsUrl.openStream(), handler);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void parseUserData(String path, boolean isLocal) {
	usersPath = path;
	try {
	    final UserField userdataField = new ParameterExtractor().extractUserFields();

	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser saxParser = factory.newSAXParser();

	    DefaultHandler handler = new DefaultHandler() {

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		    User user = new User();
		    field2Value.clear();

		    // Collect all values.
		    for (int i = 0; i < attributes.getLength(); i++) {
			field2Value.put(attributes.getQName(i), attributes.getValue(i));
		    }

		    // Set the user details.
		    if (!TextUtils.isEmpty(userdataField.getUserId())) {
			user.setUserId(field2Value.get(userdataField.getUserId()));
		    }

		    if (!TextUtils.isEmpty(userdataField.getAbtMe())) {
			user.setAbtMe(field2Value.get(userdataField.getAbtMe()));
		    }
		    if (!TextUtils.isEmpty(userdataField.getCreationDate())) {
			user.setCreationDate(field2Value.get(userdataField.getCreationDate()));
		    }
		    if (!TextUtils.isEmpty(userdataField.getReputation())) {
			user.setReputation(field2Value.get(userdataField.getReputation()));
		    }
		    if (!TextUtils.isEmpty(userdataField.getLocation())) {
			user.setLocation(field2Value.get(userdataField.getLocation()));
		    }
		    if (!TextUtils.isEmpty(userdataField.getWebsiteUrl())) {
			user.setWebsiteUrl(field2Value.get(userdataField.getWebsiteUrl()));
		    }
		    if (!TextUtils.isEmpty(userdataField.getUserName())) {
			user.setUserName(field2Value.get(userdataField.getUserName()));
		    }

		    users.add(user);

		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
		}

		public void characters(char ch[], int start, int length) throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

	    };

	    if (isLocal) {
		saxParser.parse(usersPath, handler);
	    } else {
		URL postsUrl = new URL(usersPath);
		saxParser.parse(postsUrl.openStream(), handler);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public List<Post> getPosts() {
	return posts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IParser#getUsers()
     */
    @Override
    public List<User> getUsers() {
	return users;
    }

}
