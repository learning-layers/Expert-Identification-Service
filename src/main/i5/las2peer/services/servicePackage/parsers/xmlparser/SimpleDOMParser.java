package i5.las2peer.services.servicePackage.parsers.xmlparser;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 */

/**
 * @author sathvik
 *
 */
public class SimpleDOMParser {
    String xmlstring;

    public SimpleDOMParser(String xmlstring) {
	this.xmlstring = xmlstring;
    }

    /**
     * 
     * @param nodename
     *            Tagname to parse
     * @return A nodelist found by the parser for the given node name
     */
    public NodeList getNodes(String nodename) {
	if (xmlstring == null)
	    return null;

	DocumentBuilder db;
	try {
	    db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(xmlstring));
	    Document doc = db.parse(is);

	    return doc.getElementsByTagName(nodename);
	} catch (ParserConfigurationException e2) {
	    e2.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
