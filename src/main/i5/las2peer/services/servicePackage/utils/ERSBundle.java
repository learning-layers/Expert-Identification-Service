/**
 * 
 */
package i5.las2peer.services.servicePackage.utils;

/**
 * @author sathvik
 *
 */
public class ERSBundle {
    public final String datasetId;
    public final String algorithmName;
    public final String query;
    public final boolean isEvaluation;
    public final boolean isVisualization;
    public final String alpha;
    public final String intraWeight;
    public final String dateBefore;

    // Use builder pattern to save all service parameter, pass this to
    // SearcherBase

    public static class Builder {
	private String datasetId = null;
	private String algorithmName = null;
	private String query = null;

	private boolean isEvaluation = false;
	private boolean isVisualization = false;
	private String alpha = null;
	private String intraWeight = null;
	private String dateBefore = null;

	public Builder(String datasetId, String query, String algorithmName) {
	    this.datasetId = datasetId;
	    this.query = query;
	    this.algorithmName = algorithmName;
	}

	public Builder alpha(String alpha) {
	    this.alpha = alpha;
	    return this;
	}

	public Builder intraWeight(String weight) {
	    this.intraWeight = weight;
	    return this;
	}

	public Builder dateBefore(String datebefore) {
	    this.dateBefore = datebefore;
	    return this;
	}

	public Builder isVisualization(boolean isVis) {
	    this.isVisualization = isVis;
	    return this;
	}

	public Builder isEvaluation(boolean isEval) {
	    this.isEvaluation = isEval;
	    return this;
	}

	public ERSBundle build() {
	    return new ERSBundle(this);
	}
    }

    private ERSBundle(Builder builder) {
	// Required parameters
	datasetId = builder.datasetId;
	algorithmName = builder.algorithmName;

	// Optional parameters
	query = builder.query;
	alpha = builder.alpha;
	intraWeight = builder.intraWeight;
	dateBefore = builder.dateBefore;
	isVisualization = builder.isVisualization;
	isEvaluation = builder.isEvaluation;
    }

    public static void main(String args[]) {
	ERSBundle map = new ERSBundle.Builder("test", "test", "test").alpha("").intraWeight("").isEvaluation(false).isVisualization(false).build();
	String name = map.algorithmName;
    }

}
