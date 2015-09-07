package i5.las2peer.services.servicePackage.parsers;

/**
 * A bean class to hold parsed values
 * 
 * @author sathvik
 */

public class Post implements IPost {

    private String postid;
    private String post_type_id;
    private String creation_date;
    private String score;
    private String body;
    private String owner_user_id;
    private String title;
    private String parent_id;

    public Post() {

    }

    public void setPostId(String postid) {
	this.postid = postid;
    }

    public void setCreationDate(String date) {
	this.creation_date = date;
    }

    public void setScore(String score) {
	this.score = score;
    }

    public void setBody(String body) {
	this.body = body;
    }

    public void setOwnerUserId(String userid) {
	this.owner_user_id = userid;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public void setPostTypeId(String postTypeId) {
	this.post_type_id = postTypeId;
    }

    public void setParentId(String parentId) {
	this.parent_id = parentId;
    }

    public String getPostId() {
	return this.postid;
    }

    public String getCreationDate() {
	return this.creation_date;
    }

    public String getScore() {
	return this.score;
    }

    public String getBody() {
	return this.body;
    }

    public String getOwnerUserId() {
	return this.owner_user_id;
    }

    public String getTitle() {
	return this.title;
    }

    public String getPostTypeId() {
	return this.post_type_id;
    }

    public String getParentId() {
	return this.parent_id;
    }

}
