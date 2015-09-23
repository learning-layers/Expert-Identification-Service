/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.parsers.xmlparser.DataField;
import i5.las2peer.services.servicePackage.parsers.xmlparser.UserField;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author sathvik
 *
 */
public class ParameterExtractor {
    private JAXBContext context;

    public ParameterExtractor() {
	try {
	    context = JAXBContext.newInstance(DataField.class);
	} catch (JAXBException e) {
	    e.printStackTrace();
	}
    }

    public DataField extractDataFields() {
	DataField parameter = null;
	try {
	    context = JAXBContext.newInstance(DataField.class);
	    Unmarshaller um = context.createUnmarshaller();
	    File file = new File("config/data_mapping.xml");
	    parameter = (DataField) um.unmarshal(file);

	} catch (JAXBException e) {
	    e.printStackTrace();
	}

	return parameter;

    }

    public UserField extractUserFields() {
	UserField parameter = null;

	try {
	    context = JAXBContext.newInstance(UserField.class);
	    Unmarshaller um = context.createUnmarshaller();
	    File file = new File("config/user_mapping.xml");
	    parameter = (UserField) um.unmarshal(file);

	} catch (JAXBException e) {
	    e.printStackTrace();
	}

	return parameter;

    }

}
