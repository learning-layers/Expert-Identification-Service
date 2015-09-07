package i5.las2peer.services.servicePackage.models;

/**
 * @author sathvik
 */

public class SemanticToken extends Token {

    /**
     * Annotation containing details about the identified single semantic
     * information tag.
     * 
     * */

    private String annotations;

    /**
     * A confidence value associated with the extracted annotation.
     * Annotation with lower confidence values are discarded later in the
     * process.
     * 
     * */

    private double confidenceVal;

    /**
     * Categories/tags extracted from DBpedia.
     * 
     * @see <a href="http://dbpedia.org/"> DBpedia </a>
     * */
    private String tags;

    public SemanticToken(long id, String annotations) {
	super(id, annotations);
	this.annotations = annotations;
    }

    /**
     * A confidence value of the extracted annotation.
     * 
     * @param confidenceVal
     */
    public void setConfidenceVal(double confidenceVal) {
	this.confidenceVal = confidenceVal;
    }

    /**
     * 
     * @return A json string containing annotation details.
     */
    public String getAnnotations() {
	return annotations;
    }

    /**
     * 
     * @return double value representing the value of the annotations.
     */
    public double getConfidenceVal() {
	return confidenceVal;
    }

    /**
     * This method returns the DbCategories extracted from the JSON String
     * retrieved by TAGME web service.
     * 
     * @return Tags extracted from JSON string.
     */
    public String getTags() {
	// TODO: Extract tags here from annotation rather than SemanticTagger.
	return tags;
    }

}
