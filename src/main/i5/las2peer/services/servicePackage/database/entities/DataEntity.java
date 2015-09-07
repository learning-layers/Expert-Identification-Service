package i5.las2peer.services.servicePackage.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "data" table in the corresponding
 * MySQL database.
 * 
 * "data" table contains details about the posts made by the users.
 * 
 * This entity will hold the values from the "data" table columns.
 * Every field of the class specifies the mapping of the table column name,
 * data type and constraints if any.
 * 
 * @author sathvik
 * 
 */

@DatabaseTable(tableName = "data")
public class DataEntity {

    @DatabaseField(columnName = "post_id", unique = true, dataType = DataType.LONG, throwIfNull = true, id = true)
    private long postId;

    @DatabaseField(columnName = "post_type_id", dataType = DataType.INTEGER)
    private int postTypeId;

    @DatabaseField(columnName = "creation_date", dataType = DataType.LONG_STRING)
    private String creationDate;

    @DatabaseField(columnName = "score", dataType = DataType.LONG)
    private long score;

    @DatabaseField(columnName = "body", dataType = DataType.STRING_BYTES, format = "UTF-8")
    private String body;

    @DatabaseField(columnName = "owner_user_id", dataType = DataType.LONG)
    private long ownerUserId;

    @DatabaseField(columnName = "title", dataType = DataType.STRING_BYTES, format = "UTF-8")
    private String title;

    @DatabaseField(columnName = "tags", dataType = DataType.LONG_STRING)
    String tags;

    @DatabaseField(columnName = "parent_id", dataType = DataType.LONG)
    private long parentId;

    @DatabaseField(columnName = "clean_text", dataType = DataType.STRING_BYTES, format = "UTF-8")
    private String cleanText;

    // @DatabaseField(generatedId = true)
    // long id;

    public DataEntity() {

    }

    public void setPostId(long id) {
	this.postId = id;
    }

    public void setPostTypeId(int post_type) {
	this.postTypeId = post_type;
    }


    public void setCreationDate(String date) {
	this.creationDate = date;
    }

    public void setScore(long score) {
	this.score = score;
    }

    public void setOwnerUserId(long id) {
	this.ownerUserId = id;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public void setTags(String tags) {
	this.tags = tags;
    }

    public void setParentId(long id) {
	this.parentId = id;
    }

    public void setCleanText(String text) {
	this.cleanText = text;
    }

    public void setBody(String text) {
	this.body = text;
    }

    public long getPostId() {
	return this.postId;
    }

    public String getCreationDate() {
	return creationDate;
    }

    public long getScore() {
	return score;
    }

    public String getBody() {
	return body;
    }

    public long getOwnerUserId() {
	return ownerUserId;
    }

    public String getTitle() {
	return title;
    }

    public String getTags() {
	return this.tags;
    }

    public int getPostTypeId() {
	return postTypeId;
    }

    public long getParentId() {
	return parentId;
    }

    public String getCleanText() {
	return cleanText;
    }

}
