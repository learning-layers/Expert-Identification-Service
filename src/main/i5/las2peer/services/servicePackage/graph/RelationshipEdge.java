package i5.las2peer.services.servicePackage.graph;

/**
 * A bean class that holds edge information with two vertices.
 * 
 * @author sathvik
 */

public class RelationshipEdge {
    private String vertex1;
    private String vertex2;
    private String label;

    /**
     * @param v1
     *            One of the vertex associated with this edge.
     * @param v2
     *            Another vertex associated with this edge.
     * @param label
     *            Name to identify an edge.
     * */
    public RelationshipEdge(String v1, String v2, String label) {
	this.vertex1 = v1;
	this.vertex2 = v2;
	this.label = label;
    }

    /**
     * This method returns one of the vertex of an edge.
     * 
     * @return String identifying one of the vertex.
     * 
     * */
    public String getV1() {
	return vertex1;
    }

    /**
     * This method returns one of the vertex of an edge.
     * 
     * @return String identifying one of the vertex.
     * 
     * */
    public String getV2() {
	return vertex2;
    }

    /**
     * This method returns the name identifying an edge.
     * 
     * @return Returns the label of the edge.
     * 
     * */
    public String toString() {
	return label;
    }
}