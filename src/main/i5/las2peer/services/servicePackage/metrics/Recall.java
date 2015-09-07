/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.utils.Application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author sathvik
 *
 */
public class Recall implements IEvaluator<Double> {
    private LinkedHashMap<String, Double> userId2score;
    private int count; // To calculate R@count
    private double recallScore = 0;
    private List<Double> recalls = new ArrayList<Double>();
    private static final int collectionSize = 50;
    private Map<Long, UserEntity> userId2userObj;

    public Recall(LinkedHashMap<String, Double> userId2score, Map<Long, UserEntity> userId2userObj, int count) {
	if (count == -1) {
	    this.count = Integer.MAX_VALUE;
	} else {
	    this.count = count;
	}

	this.userId2score = userId2score;
	this.userId2userObj = userId2userObj;

    }

    public void compute() {
	//System.out.println("Computing Recall...");
	Iterator<String> iterator = this.userId2score.keySet().iterator();
	int noRelevantExperts = 0;
	int i = 0;

	int totalExpert = getTotalExpertsInCollection();
	if (totalExpert == 0) {
	    return;
	}

	while (iterator.hasNext() && i < this.count) {
	    String userId = iterator.next();
	    UserEntity userEntity = userId2userObj.get(Long.valueOf(userId));
	    if (userEntity != null && userEntity.isProbableExpert()) {
		noRelevantExperts++;
	    }

	    // Calculate Recall value at each position and store them.
	    recalls.add((double) noRelevantExperts / totalExpert);

	    i++;
	}
	// System.out.println("No of relevant expert:: " + noRelevantExperts);

	double cumulativeRecallScore = 0;
	for (int j = 0; j < recalls.size(); j++) {
	    cumulativeRecallScore += recalls.get(j);
	}

	recallScore = (double) cumulativeRecallScore / collectionSize;
    }

    /**
     * 
     * @return Double value indicating the average recall value.
     */
    public Double getValue() {
	return recallScore * 100;
    }

    /**
     * 
     * @return double array, recall at every position.
     */
    public Double[] getValues() {
	return recalls.toArray(new Double[recalls.size()]);
    }

    public double[] getRoundedValues() {

	ArrayList<Double> rounded_recall_values = new ArrayList<Double>();

	for (int i = 0; i < recalls.size() && i < count; i++) {
	    rounded_recall_values.add(Application.round(recalls.get(i), 2));
	}

	Double[] values = rounded_recall_values.toArray(new Double[rounded_recall_values.size()]);
	return ArrayUtils.toPrimitive(values);

    }

    private int getTotalExpertsInCollection() {
	int noRelevantExperts = 0;
	int i = 0;

	Iterator<String> iterator = this.userId2score.keySet().iterator();
	while (iterator.hasNext() && i < collectionSize) {
	    String setElement = iterator.next();
	    UserEntity user_entity = userId2userObj.get(Long.valueOf(setElement));
	    if (user_entity != null && user_entity.isProbableExpert()) {
		noRelevantExperts++;
	    }
	    i++;
	}

	return noRelevantExperts;
    }

    public int getCount() {
	return this.count;
    }
}
