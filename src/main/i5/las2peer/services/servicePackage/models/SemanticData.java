package i5.las2peer.services.servicePackage.models;

import i5.las2peer.services.servicePackage.utils.semanticTagger.SemanticTagger;

/**
 * Represents the semantic data that is retrieved by a web service.
 * 
 * @see SemanticTagger#getSemanticData()
 * @see <a href="http://tagme.di.unipi.it/tagme_help.html">TAGME</a>
 * 
 * @author sathvik
 */

public class SemanticData {
    /**
     * Annotation containing details about the identified single semantic
     * information tag.
     * */
    String annotations;

    /**
     * Categories/tags extracted from DBpedia.
     * 
     * @see <a href="http://dbpedia.org/"> DBpedia </a>
     * */
    String tags;

    /**
     * @param annotation
     *            String representing identified annotation details from the web
     *            service.
     * @param tags
     *            String representing identified tags inside the annotation
     *            text.
     * 
     * */
    public SemanticData(String annotation, String tags) {
	this.annotations = annotation;
	this.tags = tags;
    }

    /**
     * This method returns the Annotations extracted from the JSON String
     * retrieved by TAGME web service.
     * 
     * It contains title, confidence interval(rho) and dbpedia_categories.
     * 
     * @return
     */
    public String getAnnotation() {
	return annotations;
    }

    /**
     * This method returns the DbCategories extracted from the JSON String
     * retrieved by TAGME web service.
     * 
     * @return Tags extracted from JSON string.
     */
    public String getTags() {
	return tags;
    }
}
