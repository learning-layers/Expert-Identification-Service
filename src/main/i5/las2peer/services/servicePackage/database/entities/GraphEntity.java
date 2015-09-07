package i5.las2peer.services.servicePackage.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An ORMLite persistence object mapped to "visualization" table in the
 * corresponding MySQL database.
 * This entity will hold the details about the visualization graph.
 * 
 * @author sathvik
 */

@DatabaseTable(tableName = "graph")
public class GraphEntity {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(columnName = "query_id", dataType = DataType.LONG)
    private long queryId;

    @DatabaseField(columnName = "date_created", dataType = DataType.DATE)
    private Date createdAt;

    @DatabaseField(columnDefinition = "LONGTEXT", columnName = "graph_data", dataType = DataType.LONG_STRING)
    private String graphData;

    @DatabaseField(columnDefinition = "LONGTEXT", columnName = "vis_data", dataType = DataType.LONG_STRING)
    private String visData;

    public GraphEntity() {

    }

    public void setQueryId(long queryId) {
	this.queryId = queryId;
    }

    public void setCreateDate(Date date) {
	this.createdAt = date;
    }

    public void setGraph(String graph) {
	graphData = graph;
    }

    public void setVisGraph(String visGraph) {
	visData = visGraph;
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

    public String getGraph() {
	return graphData;
    }

    public String getVisGraph() {
	return visData;
    }
}
