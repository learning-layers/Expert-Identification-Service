/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.utils.statistics.Stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sathvik
 *
 */
public class NormalizedDiscountedCumulativeGain implements IEvaluator<Float> {

    private float ndcg;

    private Map<Long, UserEntity> userId2userObj;
    private LinkedHashMap<String, Double> userId2score;

    private int windowSize;

    /**
     * 
     * @param userId2score
     * @param userId2userObj
     * @param count
     */
    public NormalizedDiscountedCumulativeGain(LinkedHashMap<String, Double> userId2score, Map<Long, UserEntity> userId2userObj, int count) {
	this.userId2score = userId2score;
	this.userId2userObj = userId2userObj;
	this.windowSize = count;
    }

    /**
     * Computes the relevance score, Discounted cumulative gain(dcg) and finally
     * normalized dcg.
     */
    public void compute() {

	if (userId2userObj != null && userId2score != null) {
	    ArrayList<Integer> relevanceScores = new ArrayList<Integer>();
	    // Calculate the distribution of reputation values in the dataset.
	    Iterator<Long> it = userId2userObj.keySet().iterator();
	    ArrayList<Long> reputationList = new ArrayList<Long>();
	    while (it.hasNext()) {
		long key = (long) it.next();
		UserEntity entity = userId2userObj.get(key);
		long groundTruth = entity.getReputation();
		reputationList.add(groundTruth);
	    }
	    Stats stats = new Stats(reputationList);

	    Iterator<String> userScoreIt = userId2score.keySet().iterator();
	    int count = 0;

	    // Use the distribution in the dataset to calculate the relevance
	    // value of the reputation scores.
	    while (userScoreIt.hasNext() && count < windowSize) {
		String key = (String) userScoreIt.next();
		UserEntity entity = userId2userObj.get(Long.parseLong(key));
		long score = entity.getReputation();
		relevanceScores.add(stats.getRelevanceScore((long) score));

		count++;
	    }

	    //System.out.println("RELEVANCE SCORES:: " + relevanceScores);
	    float dcg = getDiscountedCumulativeGain(relevanceScores);
	    //System.out.println("DCG:: " + dcg);

	    float idealDcg = getDiscountedCumulativeGain(getIdealRelevanceScore(relevanceScores));
	    //System.out.println("NDCG:: " + idealDcg);

	    ndcg = dcg / idealDcg;
	}

    }

    /**
     * @return Float ndcg value.
     */
    public Float getValue() {
	return ndcg;
    }

    /**
     * 
     * @return ArrayList of Integer, returns the ideal relevance score after
     *         sorting the available scores.
     * 
     */
    private ArrayList<Integer> getIdealRelevanceScore(ArrayList<Integer> relevanceScores) {
	ArrayList<Integer> idealRelevanceScores = (ArrayList<Integer>) relevanceScores.clone();

	Collections.sort(idealRelevanceScores);
	Collections.reverse(idealRelevanceScores);

	return idealRelevanceScores;
    }

    /**
     * 
     * @param relevanceScores
     *            List of integers containing relevance score of the users
     * @return float, A discounted cumulative gain value.
     */
    public float getDiscountedCumulativeGain(ArrayList<Integer> relevanceScores) {
	ArrayList<Float> dcgains = new ArrayList<Float>();
	float dcgGainValue = 0;
	try {
	    if (relevanceScores != null && relevanceScores.size() > 1) {
		dcgains.add((float) relevanceScores.get(0));

		for (int i = 1; i < relevanceScores.size(); i++) {
		    float dcgVal = (float) (((float) relevanceScores.get(i) / Math.log(i + 1)) * Math.log(2));
		    dcgains.add(dcgains.get(i - 1) + dcgVal);
		}
		dcgGainValue = dcgains.get(dcgains.size() - 1);

	    } else if (relevanceScores != null && relevanceScores.size() == 1) {
		dcgGainValue = relevanceScores.get(0);
	    }
	} catch (Exception e) {
	    System.out.println("Exception.." + e.getMessage());
	}
	return dcgGainValue;
    }

}
