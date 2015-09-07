package i5.las2peer.services.servicePackage.database.entities;

import java.util.ArrayList;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "users" table in the
 * corresponding MySQL database.
 * All the user details are stored in this table and persisted in this class.
 * 
 * This entity will hold the values from the "users" table columns.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "users")
public class UserEntity {
    @DatabaseField(columnName = "accountId", dataType = DataType.LONG)
    private long accountId;

    @DatabaseField(columnName = "userId", dataType = DataType.LONG, unique = true, id = true)
    private long userId;

    @DatabaseField(columnName = "userName")
    private String userName;

    @DatabaseField(columnName = "reputation", dataType = DataType.LONG)
    private long reputation;

    @DatabaseField(columnName = "creationDate")
    private String creationDate;

    @DatabaseField(columnName = "location")
    private String location;

    @DatabaseField(columnName = "aboutme", dataType = DataType.LONG_STRING)
    private String aboutme;

    @DatabaseField(columnName = "websiteUrl", dataType = DataType.LONG_STRING)
    private String websiteUrl;

    @DatabaseField(columnName = "no_of_posts", dataType = DataType.LONG)
    private long noOfPosts;

    // Inserted after computation by the evaluation module.
    @DatabaseField(columnName = "probable_expert", dataType = DataType.BOOLEAN)
    private boolean probableExpert = false; // Used for evaluation.

    // This is computed by ERS algorithm.
    private double score;

    private String relatedPosts;

    private String tags;

    public void setUserAccId(long id) {
	this.accountId = id;
    }

    public void setUserName(String username) {
	this.userName = username;
    }

    public void setReputation(long rep) {
	this.reputation = rep;
    }

    public void setCreationDate(String date) {
	this.creationDate = date;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public void setAbtMe(String abtme) {
	this.aboutme = abtme;
    }

    public void setUserId(long id) {
	this.userId = id;
    }

    public void setWebsiteUrl(String url) {
	this.websiteUrl = url;
    }

    public void setScore(double score) {
	this.score = score;
    }

    public void setNoOfPosts(long noOfPosts) {
	this.noOfPosts = noOfPosts;
    }

    public void setRelatedPosts(ArrayList<String> posts) {
	this.relatedPosts = posts.toString();
    }

    public void setTags(String tags) {
	this.tags = tags;
    }

    public long getUserAccId() {
	return accountId;
    }

    public String getUserName() {
	return userName;
    }

    public long getReputation() {
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

    public long getUserId() {
	return userId;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public double getScore() {
	return this.score;
    }

    public long getNoOfPosts() {
	return noOfPosts;
    }

    public boolean isProbableExpert() {
	return probableExpert;
    }

    public String getRelatedPosts() {
	return relatedPosts;
    }

    public String getTags() {
	return tags;
    }

}
