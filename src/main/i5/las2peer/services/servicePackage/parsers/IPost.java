/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

/**
 * @author sathvik
 *
 */
public interface IPost {
    public String getPostId();

    public String getCreationDate();

    public String getScore();

    public String getBody();

    public String getOwnerUserId();

    public String getTitle();

    public String getPostTypeId();

    public String getParentId();
}
