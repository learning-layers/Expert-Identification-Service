package i5.las2peer.services.servicePackage.scorer;

import java.util.LinkedHashMap;

/**
 * A strategy pattern to execute a particular scoring algorithm
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy
 *      Pattern</a>
 * 
 * @author sathvik
 */

public interface ScoreStrategy {

    /**
     * A method where the logic of algorithm is implemented
     */
    void executeAlgorithm();

    /**
     * 
     * @return String in the form of json containing details about the experts
     */
    public String getExperts();

    /**
     * 
     * @return A map of expert id to their score calculated by the respective
     *         algorithm.
     */
    public LinkedHashMap<String, Double> getExpertMap();

    /**
     * Saves the results obtained from the strategy to database
     */
    public void saveResults();

    /**
     * 
     * @return
     */
    public long getExpertsId();

    /**
     * Gets the unique id from the database for the saved evaluation data.
     * 
     * @return A long value to identify the evaluation record in the database.
     */
    public long getEvaluationId();

    /**
     * Gets the unique id from the database for the saved visualization data.
     * 
     * @return A long value to identify the visualization record in the
     *         database.
     */
    public long getVisualizationId();

    /**
     * Closes Database connection if open.
     */
    public void close();

}
