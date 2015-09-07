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
public class Precision {
    private LinkedHashMap<String, Double> userId2score;
    private int count; // To calculate P@count
    // private double precisions[];
    private List<Double> precisions;
    private double avgPrecision = 0;

    private Map<Long, UserEntity> userId2userObj;

    public Precision(LinkedHashMap<String, Double> userId2score, Map<Long, UserEntity> userId2userObj, int count) {
	if (count == -1) {
	    this.count = Integer.MAX_VALUE;
	} else {
	    this.count = count;
	}
	this.userId2score = userId2score;
	this.userId2userObj = userId2userObj;
    }

    public void compute() {
	//System.out.println("Computing Precision::" + userId2score.size());
	// precisions = new double[this.userId2score.size()];
	precisions = new ArrayList<Double>();
	double cumulativePrecision = 0;

	Iterator<String> iterator = this.userId2score.keySet().iterator();
	int noRelevantExperts = 0;
	int i = 0;

	while (iterator.hasNext() && i < this.count) {
	    String userId = iterator.next();
	    UserEntity userEntity = userId2userObj.get(Long.valueOf(userId));

	    // Calculate precision value only at relevant experts.
	    double precision = 0;
	    if (userEntity.isProbableExpert()) {
		// System.out.println("An expert..." +
		// userEntity.getReputation());
		noRelevantExperts++;
		// precisions[i] = (double) noRelevantExperts / (i + 1);
	    } else {
		// System.out.println("Not an expert..." +
		// userEntity.getReputation());
	    }
	    precision = (double) noRelevantExperts / (i + 1);
	    System.out.println("Precision..." + precision);
	    precisions.add(precision);
	    // System.out.println(" PRECISION:: " + precision);
	    i++;
	}

	// for (int j = 0; j < this.count && j < this.userId2score.size(); j++)
	// {
	// cumulativePrecision += precisions[j];
	// }
	// System.out.println("Cum Precision Completed...");

	for (int j = 0; j < precisions.size() && j < this.count; j++) {
	    cumulativePrecision += precisions.get(j);
	}

	//System.out.println("CUM PRECISION:: " + cumulativePrecision);

	if (precisions.size() > 0) {
	    avgPrecision = (double) cumulativePrecision / precisions.size();
	} else {
	    avgPrecision = 0;
	}

    }

    // This is the average precision for the set of result.
    public double getValue() {
	return avgPrecision * 100;
    }

    public double[] getRoundedPrecisionValues() {
	ArrayList<Double> roundedPrecisionValues = new ArrayList<Double>();

	// for (int i = 0; i < precisions.length && i < count; i++) {
	// roundedPrecisionValues.add(Application.round(precisions[i], 2));
	// }

	for (int i = 0; i < precisions.size() && i < count; i++) {
	    roundedPrecisionValues.add(Application.round(precisions.get(i), 2));
	}

	Double[] values = roundedPrecisionValues.toArray(new Double[roundedPrecisionValues.size()]);
	return ArrayUtils.toPrimitive(values);
    }

    // Precision@count.
    public int getCount() {
	return this.count;
    }
}
