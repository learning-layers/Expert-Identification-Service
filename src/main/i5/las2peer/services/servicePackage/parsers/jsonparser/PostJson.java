package i5.las2peer.services.servicePackage.parsers.jsonparser;

import i5.las2peer.services.servicePackage.parsers.IPost;

import com.google.gson.annotations.SerializedName;

/**
 * XML binding to the posts.xml.
 * This contains the properties of a single post made by the user.
 * JAXB is used to parse the tags of an XML.
 * 
 * @author sathvik
 */

public class PostJson implements IPost {

    @SerializedName("Id")
    String postid;

    @SerializedName("PostTypeId")
    String post_type_id;

    @SerializedName("AcceptedAnswerId")
    String accept_ans_id;

    @SerializedName("CreationDate")
    String creation_date;

    @SerializedName("Score")
    String score;

    @SerializedName("ViewCount")
    String view_count;

    @SerializedName("Body")
    String body;

    @SerializedName("OwnerUserId")
    String owner_user_id;

    @SerializedName("LastEditorUserId")
    String last_editor_user_id;

    @SerializedName("LastEditDate")
    String last_edit_date;

    @SerializedName("LastActivityDate")
    String last_activity_date;

    @SerializedName("Title")
    String title;

    @SerializedName("Tags")
    String tags;

    @SerializedName("AnswerCount")
    String answer_count;

    @SerializedName("CommentCount")
    String comment_count;

    @SerializedName("FavoriteCount")
    String favorite_count;

    @SerializedName("ParentId")
    String parent_id;

    public PostJson() {

    }

    public PostJson(String postid) {
	this.postid = postid;
    }

    public String getPostId() {
	return this.postid;
    }

    public String getAccAnsId() {
	return this.accept_ans_id;
    }

    public String getCreationDate() {
	return this.creation_date;
    }

    public String getScore() {
	return this.score;
    }

    public String getViewCount() {
	return this.view_count;
    }

    public String getBody() {
	return this.body;
    }

    public String getOwnerUserId() {
	return this.owner_user_id;
    }

    public String getLastEditorUserId() {
	return this.last_editor_user_id;
    }

    public String getLastActivityDate() {
	return this.last_activity_date;
    }

    public String getTitle() {
	return this.title;
    }

    public String getTags() {
	return this.tags;
    }

    public String getAnswerCount() {
	return this.answer_count;
    }

    public String getCommentCount() {
	return this.comment_count;
    }

    public String getFavoriteCount() {
	return this.favorite_count;
    }

    public String getLastEditDate() {
	return this.last_edit_date;
    }

    public String getPostTypeId() {
	return this.post_type_id;
    }

    public String getParentId() {
	return this.parent_id;
    }

}
