package i5.las2peer.services.servicePackage.parsers.xmlparser;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * XML binding to write dataset information
 * This contains information about the dataset, database associated with it.
 * 
 * @author Sathvik
 */

@XmlRootElement
public class DatasetInfo {

    private long id;
    private String name;
    private Date createdDateTime;

    public String getName() {
	return name;
    }

    @XmlAttribute
    public void setName(String name) {
	this.name = name;
    }

    @XmlAttribute
    public void setDate(Date date) {
	this.createdDateTime = date;
    }

    public long getId() {
	return id;
    }

    @XmlAttribute
    public void setId(int id) {
	this.id = id;
    }

}