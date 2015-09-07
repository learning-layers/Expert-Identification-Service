/**
 * 
 */
package i5.las2peer.services.servicePackage.graph;

import i5.las2peer.services.servicePackage.database.entities.GraphEntity;
import i5.las2peer.services.servicePackage.utils.LocalFileManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * A graph writer class to save the generated graph to a file.
 * 
 * @author sathvik
 *
 */
public class GraphWriter {
    private GraphMLWriterWithAttrNameAndType<String, RelationshipEdge> graphWriter;
    private JUNGGraphCreator creator;
    private GraphEntity graphEntity;

    /**
     * @param creator
     *            An object that holds graph created by node and edge map.
     * @see JUNGGraphCreator#createGraph(java.util.Map, java.util.HashMap)
     * 
     */

    public GraphWriter(JUNGGraphCreator creator) {
	this.graphWriter = new GraphMLWriterWithAttrNameAndType<String, RelationshipEdge>();
	this.creator = creator;
    }

    /**
     * A method that saves the content of creator to the file in GraphML format.
     * 
     * @see <a href="http://en.wikipedia.org/wiki/GraphML">GraphML</a>
     * @see GraphMLWriter
     * 
     * @param filename
     *            Name of the file where graph will be saved.
     * @throws IOException
     */

    public void saveToGraphMl(String filename) throws IOException {

	PrintWriter out = null;
	out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

	graphWriter.addVertexData("d3", "name", null, new Transformer<String, String>() {

	    @Override
	    public String transform(String nodeid) {
		return nodeid;
	    }
	});

	graphWriter.addVertexData("d0", "color", null, new Transformer<String, String>() {

	    @Override
	    public String transform(String nodeid) {
		return "red";
	    }
	});

	HashMap<String, String> key2val = new HashMap<String, String>();
	key2val.put("attr.name", "name");
	key2val.put("attr.type", "string");
	graphWriter.addHeader("d3", key2val);

	HashMap<String, String> key2val1 = new HashMap<String, String>();
	key2val1.put("attr.name", "color");
	key2val1.put("attr.type", "string");
	graphWriter.addHeader("d0", key2val1);

	// graphWriter.setKeyAttributes(key2val);

	// TODO:Check if saving is necessary, If we can get string
	// representation, saving in db should be enough
	graphWriter.save(creator.getGraph(), out);

    }

    public String getGraphAsString(String filename) {
	return new String(LocalFileManager.getFile(filename));
    }

    public void saveToDb(long queryId, ConnectionSource connSrc) {
	try {
	    Dao<GraphEntity, Long> graphDao = DaoManager.createDao(connSrc, GraphEntity.class);

	    graphEntity = new GraphEntity();
	    graphEntity.setQueryId(queryId);
	    graphEntity.setCreateDate(new Date());
	    graphEntity.setGraph(getGraphAsString("graph_jung.graphml"));

	    // graphDao.createIfNotExists(graphEntity);

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    public String getGraph() {
	return graphEntity.getGraph();
    }

    public long getId() {
	if (graphEntity != null) {
	    return graphEntity.getId();
	}
	return -1;
    }
}
