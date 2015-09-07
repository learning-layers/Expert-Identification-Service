package i5.las2peer.services.servicePackage.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "account" table in the
 * corresponding MySQL database.
 * "account" table is used to store user account details using the service.
 * This entity will hold the values from the "account" table columns.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "account")
public class UserClickDetails {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "username", dataType = DataType.LONG_STRING)
    private String username;

    @DatabaseField(columnName = "experts_id", dataType = DataType.LONG)
    private String expertsId;

    @DatabaseField(columnName = "click_position", dataType = DataType.LONG_STRING)
    private String positions;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date date;

    public void setDate(Date date) {
	this.date = date;
    }

    public void setUserName(String username) {
	this.username = username;
    }

    public void setQueryId(String queryId) {
	this.expertsId = queryId;
    }

    public void setPositions(String positions) {
	this.positions = positions;
    }

    public long getUserAccId() {
	return id;
    }

    public String getUserName() {
	return username;
    }

    public Date getDate() {
	return date;
    }

}
