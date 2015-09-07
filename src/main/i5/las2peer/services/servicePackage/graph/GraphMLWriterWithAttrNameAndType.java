/**
 * 
 */
package i5.las2peer.services.servicePackage.graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * Adds additional attributes to node key tag in graphml.
 * This is not supported in the default GraphMLWriter.
 * 
 * Note: This adds attr and type only for the key tags of type "node".
 * 
 * @see GraphMLWriter
 * 
 * @author sathvik
 * @param <V>
 * @param <E>
 *
 */
public class GraphMLWriterWithAttrNameAndType<V, E> extends GraphMLWriter<V, E> {
    // private HashMap<String, String> attrName2attrVal;
    private HashMap<String, HashMap<String, String>> headers = new HashMap<String, HashMap<String, String>>();

    public GraphMLWriterWithAttrNameAndType() {

    }

    // public void setKeyAttributes(HashMap<String, String> key2val) {
    // this.attrName2attrVal = key2val;
    // }

    public void addHeader(String key, HashMap<String, String> key2val) {
	headers.put(key, key2val);
    }

    /**
     * Overriding from the library, because default method does not add
     * attr.name and attr.value. These parameters are required for OCD service
     * and for changing properties of the nodes such as color and size.
     * 
     * @see GraphMLWriter
     */
    public void writeKeySpecification(String key, String type, GraphMLMetadata<?> ds, BufferedWriter bw) throws IOException {
	bw.write("<key id=\"" + key + "\" for=\"" + type + "\"");

	HashMap<String, String> header = headers.get(key);
	if (type != null && type.equals("node") && header != null) {
	    for (Map.Entry<String, String> entry : header.entrySet()) {
		bw.write(" " + entry.getKey() + "=\"" + entry.getValue() + "\" ");
	    }
	}
	boolean closed = false;
	// write out description if any
	String desc = ds.description;
	if (desc != null) {
	    if (!closed) {
		bw.write(">\n");
		closed = true;
	    }
	    bw.write("<desc>" + desc + "</desc>\n");
	}
	// write out default if any
	Object def = ds.default_value;
	if (def != null) {
	    if (!closed) {
		bw.write(">\n");
		closed = true;
	    }
	    bw.write("<default>" + def.toString() + "</default>\n");
	}
	if (!closed)
	    bw.write("/>\n");
	else
	    bw.write("</key>\n");
    }
}
