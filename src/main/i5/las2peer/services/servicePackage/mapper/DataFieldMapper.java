/**
 * 
 */
package i5.las2peer.services.servicePackage.mapper;

import java.util.HashMap;

/**
 * @author sathvik
 *
 */
public class DataFieldMapper {
    HashMap<String, String> key2val = new HashMap<String, String>();

    public DataFieldMapper() {
	// Parse the mapping xml file.
    }

    public String getField(String fieldname) {
	if (key2val != null) {
	    return key2val.get(fieldname);
	} else {
	    return null;
	}
    }
}

