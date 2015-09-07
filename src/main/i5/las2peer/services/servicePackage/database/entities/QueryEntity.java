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

@DatabaseTable(tableName = "query")
public class QueryEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "name", dataType = DataType.LONG_STRING)
    private String text;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date date;

    public QueryEntity() {

    }

    public void setText(String text) {
	this.text = text;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public long getId() {
	return id;
    }

    public String getName() {
	return text;
    }

    public Date getDate() {
	return date;
    }
}
