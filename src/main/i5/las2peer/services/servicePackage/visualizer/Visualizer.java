/**
 * 
 */
package i5.las2peer.services.servicePackage.visualizer;

import i5.las2peer.services.servicePackage.database.entities.GraphEntity;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */

// TODO: Check factory pattern for this.
public class Visualizer {
    GraphEntity visEntity;

    public Visualizer() {

    }

    public void saveVisGraph(long queryId, LinkedHashMap<String, Double> nodes, ConnectionSource connSrc) {
	try {
	    GraphMl2GEXFConverter converter = new GraphMl2GEXFConverter();
	    converter.convert("graph_jung.graphml");
	    converter.colorNodes(nodes);
	    converter.applyLayout();
	    converter.export("visgraph");

	    // try {
	    // Dao<GraphEntity, Long> visulaizationDao =
	    // DaoManager.createDao(connSrc, GraphEntity.class);
	    // visEntity = visulaizationDao.queryForId(queryId);
	    //
	    // String graphContent =
	    // LocalFileManager.getFile("visgraph.gexf").toString();
	    // visEntity.setVisGraph(graphContent);
	    //
	    // } catch (SQLException e) {
	    // e.printStackTrace();
	    // }

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public String getGraph() {
	return visEntity.getGraph();
    }

    public long getId() {
	if (visEntity != null) {
	    return visEntity.getId();
	}
	return -1;
    }

}
