package i5.las2peer.services.servicePackage.parsers.xmlparser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * XML binding to the users.xml.
 * 
 * This contains the properties of a user who made the post.
 * JAXB is used to parse the tags of an XML.
 * 
 * @author sathvik
 */

@XmlRootElement(name = "map")
public class UserField {
    @XmlAttribute(name = "AccountId")
    private String accountIdFieldName;

    @XmlAttribute(name = "Id")
    private String userIdFieldName;

    @XmlAttribute(name = "DisplayName")
    private String userNameFieldName;

    @XmlAttribute(name = "Reputation")
    private String reputationFieldName;

    @XmlAttribute(name = "CreationDate")
    private String creationDateFieldName;

    @XmlAttribute(name = "Location")
    private String locationFieldName;

    @XmlAttribute(name = "AboutMe")
    private String aboutmeFieldName;

    @XmlAttribute(name = "WebsiteUrl")
    private String websiteUrlFieldName;

    public String getUserAccId() {
	return accountIdFieldName;
    }

    public String getUserName() {
	return userIdFieldName;
    }

    public String getReputation() {
	return reputationFieldName;
    }

    public String getCreationDate() {
	return creationDateFieldName;
    }

    public String getLocation() {
	return locationFieldName;
    }

    public String getAbtMe() {
	return aboutmeFieldName;
    }

    public String getUserId() {
	return userIdFieldName;
    }

    public String getWebsiteUrl() {
	return websiteUrlFieldName;
    }

}
