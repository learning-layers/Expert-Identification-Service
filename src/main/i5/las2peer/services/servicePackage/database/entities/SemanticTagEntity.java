package i5.las2peer.services.servicePackage.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "semantics" table in the
 * corresponding MySQL database.
 * This entity will hold the values from the "data" table columns.
 * Every field of the class specifies the mapping of the table column name,
 * data type and constraints if any.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "semantics")
public class SemanticTagEntity {
    @DatabaseField(columnName = "post_id", dataType = DataType.LONG, id = true, unique = true)
    private long postId;

    @DatabaseField(columnName = "annotations", dataType = DataType.STRING_BYTES, format = "UTF-8")
    private String annotations;

    @DatabaseField(columnDefinition = "LONGTEXT", columnName = "tags", dataType = DataType.LONG_STRING)
    private String tags;

    public void setPostId(long id) {
	this.postId = id;
    }

    public void setAnnotations(String annotations) {
	this.annotations = annotations;
    }

    public void setTags(String tags) {
	this.tags = tags;
    }

    public long getPostId() {
	return postId;
    }

    public String getTags() {
	return tags;
    }

    public String getAnnotations() {
	return annotations;
    }

}
