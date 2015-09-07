package i5.las2peer.services.servicePackage.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "query" table in the
 * corresponding MySQL database.
 * This entity will hold the details about the query.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "experts")
public class ExpertEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "query_id", dataType = DataType.LONG)
    private long queryId;

    @DatabaseField(columnName = "experts", dataType = DataType.LONG_STRING)
    private String experts;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date date;

    public ExpertEntity() {

    }

    public void setQueryId(long id) {
	this.queryId = id;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public void setExperts(String experts) {
	this.experts = experts;
    }

    public long getId() {
	return id;
    }

    public long getQueryId() {
	return queryId;
    }

    public Date getDate() {
	return date;
    }

    public String getExperts() {
	return experts;
    }

}
