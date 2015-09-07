package i5.las2peer.services.servicePackage.parsers.xmlparser;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML binding to the config/data_mapping.xml.
 * JAXB is used to parse the tags of an XML.
 * 
 * @author sathvik
 */

@XmlRootElement(name = "map")
public class DataField {

    @XmlAttribute(name = "Id")
    public String postidFieldName;

    @XmlAttribute(name = "PostTypeId")
    public String postTypeIdFieldName;

    @XmlAttribute(name = "CreationDate")
    public String creationDateFieldName;

    @XmlAttribute(name = "Score")
    public String scoreFieldName;

    @XmlAttribute(name = "Body")
    public String bodyFieldName;

    @XmlAttribute(name = "OwnerUserId")
    public String ownerUserIdFieldName;

    @XmlAttribute(name = "Title")
    public String titleFieldName;

    @XmlAttribute(name = "ParentId")
    public String parentIdFieldName;

    public DataField() {

    }

    public String getPostIdName() {
	return this.postidFieldName;
    }

    public String getCreationDateName() {
	return this.creationDateFieldName;
    }

    public String getScoreName() {
	return this.scoreFieldName;
    }

    public String getBodyName() {
	return this.bodyFieldName;
    }

    public String getOwnerUserIdName() {
	return this.ownerUserIdFieldName;
    }

    public String getTitleName() {
	return this.titleFieldName;
    }

    public String getPostTypeIdName() {
	return this.postTypeIdFieldName;
    }

    public String getParentIdName() {
	return this.parentIdFieldName;
    }

}
