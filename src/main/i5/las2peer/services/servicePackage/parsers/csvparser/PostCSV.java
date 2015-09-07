package i5.las2peer.services.servicePackage.parsers.csvparser;

import i5.las2peer.services.servicePackage.parsers.IPost;

import com.googlecode.jcsv.annotations.MapToColumn;

/**
 * 
 * @author sathvik
 */

public class PostCSV implements IPost {

    @MapToColumn(column = 0)
    String postid;

    @MapToColumn(column = 1)
    String parentId;

    @MapToColumn(column = 3)
    String userId;

    @MapToColumn(column = 4)
    String date;

    @MapToColumn(column = 6)
    String title;

    @MapToColumn(column = 7)
    String body;

    public PostCSV() {

    }

    public String getPostId() {
	return this.postid;
    }

    public String getBody() {
	return this.body;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IPost#getOwnerUserId()
     */
    @Override
    public String getOwnerUserId() {
	return this.userId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IPost#getCreationDate()
     */
    @Override
    public String getCreationDate() {
	return date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IPost#getScore()
     */
    @Override
    public String getScore() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IPost#getTitle()
     */
    @Override
    public String getTitle() {
	return title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IPost#getPostTypeId()
     */
    @Override
    public String getPostTypeId() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IPost#getParentId()
     */
    @Override
    public String getParentId() {
	return parentId;
    }

}
