package i5.las2peer.services.servicePackage.parsers.xmlparser;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This contains all the users available in the users.xml
 * which is a list of all the user.
 * 
 * @see User
 * @author sathvik
 */

@XmlRootElement(name = "users")
public class Users {

    @XmlElement(name = "row")
    private ArrayList<User> users;

    @XmlElement(name = "row")
    public void setUsersList(ArrayList<User> users) {
	this.users = users;
    }

    /**
     * This method returns the list of all the users available in users.xml
     * 
     * @return list of all the user
     * @see User
     */
    public ArrayList<User> getUsersList() {
	return users;
    }
}
