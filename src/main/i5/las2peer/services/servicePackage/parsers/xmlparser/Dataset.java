package i5.las2peer.services.servicePackage.parsers.xmlparser;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Parses all the available datasets
 * 
 * @see User
 * @author sathvik
 */

@XmlRootElement(name = "datasets")
public class Dataset {

    @XmlElement(name = "dataset")
    private ArrayList<DatasetInfo> datasetInfos;

    @XmlElement(name = "dataset")
    public void setDatasetInfoList(ArrayList<DatasetInfo> infos) {
	this.datasetInfos = infos;
    }

    public ArrayList<DatasetInfo> getDatsetInfoList() {
	return datasetInfos;
    }
}
