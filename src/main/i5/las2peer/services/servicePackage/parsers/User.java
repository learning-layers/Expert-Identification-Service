package i5.las2peer.services.servicePackage.parsers;

/**
 * Bean class to hold parsed values.
 * 
 * @author sathvik
 */

public class User implements IUser {
    private String accountId;

    private String userId;

    private String userName;

    private String reputation;

    private String creationDate;

    private String location;

    private String aboutme;

    private String websiteUrl;

    public void setUserAccId(String accId) {
	accountId = accId;
    }

    public void setUserName(String username) {
	userName = username;
    }

    public void setReputation(String rept) {
	reputation = rept;
    }

    public void setCreationDate(String date) {
	creationDate = date;
    }

    public void setLocation(String loc) {
	location = loc;
    }

    public void setAbtMe(String abtMe) {
	aboutme = abtMe;
    }

    public void setUserId(String userid) {
	userId = userid;
    }

    public String setWebsiteUrl() {
	return websiteUrl;
    }

    public String getUserAccId() {
	return accountId;
    }

    public String getUserName() {
	return userName;
    }

    public String getReputation() {
	return reputation;
    }

    public String getCreationDate() {
	return creationDate;
    }

    public String getLocation() {
	return location;
    }

    public String getAbtMe() {
	return aboutme;
    }

    public String getUserId() {
	return userId;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

}
