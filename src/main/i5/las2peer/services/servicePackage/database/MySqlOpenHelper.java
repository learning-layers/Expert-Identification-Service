/**
 * 
 */
package i5.las2peer.services.servicePackage.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcDatabaseConnection;
import com.j256.ormlite.support.ConnectionSource;

import i5.las2peer.services.servicePackage.utils.PropertySupplier;

/**
 * An abstract MySQL helper class to create database and to get connection
 * source, required by ORMLite methods for CRUD operations.
 * 
 * @author sathvik
 *
 */
public abstract class MySqlOpenHelper {
    private String mDbUrl;
    private String mUserName;
    private String mPassword;
    private String mDbName;
    private ConnectionSource mConnectionSource;
    private JdbcDatabaseConnection mJdbConnection;
    private Connection sqlConnection;

    public MySqlOpenHelper() {
    mDbUrl = PropertySupplier.getProperty("jdbc.url");
	mDbName = PropertySupplier.getProperty("jdbc.name");
	mUserName = PropertySupplier.getProperty("jdbc.username");;
	mPassword = PropertySupplier.getProperty("jdbc.password");;

	createDatabase();
    }

    private void createDatabase() {
	try {
	    close();
	    sqlConnection = DriverManager.getConnection(mDbUrl, mUserName, mPassword);
	    mJdbConnection = new JdbcDatabaseConnection(sqlConnection);
	    mJdbConnection.executeStatement("CREATE DATABASE IF NOT EXISTS " + mDbName, -1);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}

    }

    public ConnectionSource getConnectionSource() {
	try {
	    if (mConnectionSource != null && mConnectionSource.isOpen()) {
		return mConnectionSource;
	    } else {
		mConnectionSource = new JdbcConnectionSource(mDbUrl + mDbName);
		((JdbcConnectionSource) mConnectionSource).setUsername(mUserName);
		((JdbcConnectionSource) mConnectionSource).setPassword(mPassword);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return mConnectionSource;
    }

    public void close() throws SQLException {
	if (sqlConnection != null) {
	    sqlConnection.close();
	}
	if (mJdbConnection != null) {
	    mJdbConnection.close();
	}
	if (mConnectionSource != null) {
	    mConnectionSource.close();
	}
    }
}
