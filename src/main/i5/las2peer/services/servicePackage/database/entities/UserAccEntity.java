package i5.las2peer.services.servicePackage.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "account" table in the
 * corresponding MySQL database.
 * This entity will hold the values from the "account" table columns.
 * Every field of the class specifies the mapping of the table column name,
 * data type and constraints if any.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "account")
public class UserAccEntity {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "username", dataType = DataType.LONG_STRING)
    private String userName;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date date;



    public void setDate(Date date) {
	this.date = date;
    }

    public void setUserName(String username) {
	this.userName = username;
    }

    public long getUserAccId() {
	return id;
    }

    public String getUserName() {
	return userName;
    }

    public Date getDate() {
	return date;
    }

}
