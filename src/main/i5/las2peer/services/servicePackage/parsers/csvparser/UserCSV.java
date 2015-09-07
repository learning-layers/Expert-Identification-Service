package i5.las2peer.services.servicePackage.parsers.csvparser;

import i5.las2peer.services.servicePackage.parsers.IUser;

import com.googlecode.jcsv.annotations.MapToColumn;

/**
 * 
 * XML binding to the users.xml.
 * 
 * This contains the properties of a user who made the post.
 * jCSV is used to parse the tags of CSV file.
 * 
 * @author sathvik
 */

public class UserCSV implements IUser {

    private String accountId;

    @MapToColumn(column = 3)
    private String userId;

    @MapToColumn(column = 5)
    private String userName;

    private String reputation;

    private String creationDate;

    private String location;

    private String aboutme;

    private String views;

    private String upvotes;

    private String downvotes;

    private String websiteUrl;

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getUserAccId()
     */
    @Override
    public String getUserAccId() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getUserName()
     */
    @Override
    public String getUserName() {
	// TODO Auto-generated method stub
	return userName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getReputation()
     */
    @Override
    public String getReputation() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getCreationDate()
     */
    @Override
    public String getCreationDate() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getLocation()
     */
    @Override
    public String getLocation() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getAbtMe()
     */
    @Override
    public String getAbtMe() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getUserId()
     */
    @Override
    public String getUserId() {
	// TODO Auto-generated method stub
	return userId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IUser#getWebsiteUrl()
     */
    @Override
    public String getWebsiteUrl() {
	// TODO Auto-generated method stub
	return null;
    }


}
