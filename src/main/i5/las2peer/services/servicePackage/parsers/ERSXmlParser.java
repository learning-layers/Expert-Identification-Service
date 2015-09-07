/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.parsers.xmlparser.Post;
import i5.las2peer.services.servicePackage.parsers.xmlparser.Posts;
import i5.las2peer.services.servicePackage.parsers.xmlparser.User;
import i5.las2peer.services.servicePackage.parsers.xmlparser.Users;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author sathvik
 *
 */
public class ERSXmlParser implements IParser<List<Post>, List<User>> {
    private JAXBContext context;
    private String postsFilePath;
    private String usersFilePath;

    private static final String DATA_FILENAME = "posts.xml";
    private static final String USER_FILENAME = "users.xml";

    public ERSXmlParser(String dirPath) {
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

	List<Post> postsList = null;
	File file = new File(postsFilePath);
	try {

	    context = JAXBContext.newInstance(Posts.class);
	    Unmarshaller um = context.createUnmarshaller();
	    Posts posts = (Posts) um.unmarshal(file);
	    postsList = (ArrayList) posts.getResources();

	} catch (JAXBException e) {
	    e.printStackTrace();
	}
	return postsList;
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
