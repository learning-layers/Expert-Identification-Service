package i5.las2peer.services.servicePackage.scorer;

import java.util.LinkedHashMap;

/**
 * A context for the startegy pattern. This sets the type of strategy to be used
 * to execute algorithm.
 * 
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy
 *      Pattern</a>
 * 
 * 
 * @author sathvik
 */

public class ScoringContext {
    private ScoreStrategy strategy;

    /**
     * This method sets up the strategy to be used.
     * 
     * @param strategy
     * @see ScoreStrategy
     */
    public ScoringContext(ScoreStrategy strategy) {
	this.strategy = strategy;
    }

    /**
     * Method to be called while executing the respective algorithm.
     * 
     */
    public void executeStrategy() {
	this.strategy.executeAlgorithm();
    }

    /**
     * 
     * @return A Json string containing details about the experts.
     */
    public String getExperts() {
	return strategy.getExperts();
    }

    /**
     * 
     * @return A mapping from expert id to their score value as calculated by
     *         the algorithm.
     */
    public LinkedHashMap<String, Double> getExpertMap() {
	return strategy.getExpertMap();
    }

    public void saveResults() {
	strategy.saveResults();
    }

    public void close() {
	strategy.close();
    }

}
