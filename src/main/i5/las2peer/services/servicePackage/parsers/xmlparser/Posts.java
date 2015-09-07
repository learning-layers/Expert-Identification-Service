package i5.las2peer.services.servicePackage.parsers.xmlparser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This contains all the posts available in the posts.xml
 * which is a list of post
 * 
 * @see Post
 * @author sathvik
 */

@XmlRootElement(name = "posts")
public class Posts {
    @XmlElement(name = "row")
    private List<Post> resources;

    @XmlElement(name = "row")
    public void setResources(ArrayList<Post> resources) {
	this.resources = resources;
    }

    public List<Post> getResources() {
	return resources;
    }
}
