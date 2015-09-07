package i5.las2peer.services.servicePackage.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "data_info" table.
 * This holds the detail about the database name, index directory path mapped to
 * the particular dataset.
 * 
 * NOTE: This is stored in its own database called as ersdb. This table is not
 * present in the the database corresponding to dataset.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "datasetInfo")
public class DataInfoEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "dataset_name", dataType = DataType.LONG_STRING)
    private String datasetName;

    @DatabaseField(columnName = "database_name", dataType = DataType.LONG_STRING)
    private String databaseName;

    @DatabaseField(columnName = "original_filepath", dataType = DataType.LONG_STRING)
    private String filepath;

    @DatabaseField(columnName = "index_path", dataType = DataType.LONG_STRING)
    private String lucenIndexFilepath;

    @DatabaseField(columnName = "created_on", dataType = DataType.DATE)
    private Date date;

    public DataInfoEntity() {

    }

    public long getId() {
	return id;
    }

    public void setFilepath(String filepath) {
	this.filepath = filepath;
    }

    public void setIndexFilepath(String filepath) {
	this.lucenIndexFilepath = filepath;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public void setDatabase(String dbName) {
	this.databaseName = dbName;
    }

    public void setDataset(String dataset) {
	this.datasetName = dataset;
    }

    public String getIndexFilepath() {
	return lucenIndexFilepath;
    }

    public String getDatasetName() {
	return datasetName;
    }

    public String getDatabaseName() {
	return databaseName;
    }

    public String getDatasetFilePath() {
	return filepath;
    }

    public Date getDate() {
	return date;
    }
}
