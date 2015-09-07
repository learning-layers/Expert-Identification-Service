package i5.las2peer.services.servicePackage.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "evaluation" table in the
 * corresponding MySQL database.
 * This entity will hold the details about the evaluation metrics.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "evaluation")
public class EvaluationMetricsEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "query_id", dataType = DataType.LONG)
    private long queryId;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date createdAt;

    // A json string consisting of all metric details.
    @DatabaseField(columnName = "metrics", dataType = DataType.LONG_STRING)
    private String metrics;

    public EvaluationMetricsEntity() {

    }

    public void setQueryId(long id) {
	this.queryId = id;
    }

    public void setCreateDate(Date date) {
	createdAt = date;
    }

    public void setMetrics(String metrics) {
	this.metrics = metrics;
    }

    public long getId() {
	return id;
    }

    public long getQueryId() {
	return queryId;
    }

    public Date getCreateDate() {
	return createdAt;
    }

    public String getMetrics() {
	return metrics;
    }
}
