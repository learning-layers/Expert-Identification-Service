package i5.las2peer.services.servicePackage.parsers.csvparser;

/**
 * 
 * @author sathvik
 */

public class EvaluationCSV {

    private String precision;

    private String recall;

    private String precisions;

    private String recalls;

    private String epIAPrecisions;

    private String epIAPRecalls;

    private String rr;

    private String ndcg;

    private String algoName;

    private String query;

    public EvaluationCSV() {

    }

    public void setPrecision(String precision) {
	this.precision = precision;
    }

    public void setRecall(String recall) {
	this.recall = recall;
    }

    public void setPrecisionValues(String precisions) {
	this.precisions = precisions;
    }

    public void setRecallValues(String recalls) {
	this.recalls = recalls;
    }

    public void setEpIAPrecisions(String epIAPrecision) {
	this.epIAPrecisions = epIAPrecision;
    }

    public void setEpIAPStandardRecallPts(String standardRecallPoints) {
	this.epIAPRecalls = standardRecallPoints;
    }

    public void setRR(String rr) {
	this.rr = rr;
    }

    public void setNDCG(String ndcg) {
	this.ndcg = ndcg;
    }

    public void setAlgoName(String algoName) {
	this.algoName = algoName;
    }

    public void setQuery(String query) {
	this.query = query;
    }

    /**
     * @return the precisions
     */
    public String getPrecisions() {
	return precisions;
    }

    /**
     * @param precisions
     *            the precisions to set
     */
    public void setPrecisions(String precisions) {
	this.precisions = precisions;
    }

    /**
     * @return the recalls
     */
    public String getRecalls() {
	return recalls;
    }

    /**
     * @param recalls
     *            the recalls to set
     */
    public void setRecalls(String recalls) {
	this.recalls = recalls;
    }

    /**
     * @return the rr
     */
    public String getRr() {
	return rr;
    }

    /**
     * @param rr
     *            the rr to set
     */
    public void setRr(String rr) {
	this.rr = rr;
    }

    /**
     * @return the ndcg
     */
    public String getNdcg() {
	return ndcg;
    }

    /**
     * @param ndcg
     *            the ndcg to set
     */
    public void setNdcg(String ndcg) {
	this.ndcg = ndcg;
    }

    /**
     * @return the recall
     */
    public String getRecall() {
	return recall;
    }

    /**
     * @return the epIAP
     */
    public String getEpIAPrecisions() {
	return epIAPrecisions;
    }

    /**
     * @return the epIAP
     */
    public String getStandardRecallPts() {
	return epIAPRecalls;
    }

    /**
     * @return the algoName
     */
    public String getAlgoName() {
	return algoName;
    }

    /**
     * @return the query
     */
    public String getQuery() {
	return query;
    }

    /**
     * @return the precision
     */
    public String getPrecision() {
	return precision;
    }

}
