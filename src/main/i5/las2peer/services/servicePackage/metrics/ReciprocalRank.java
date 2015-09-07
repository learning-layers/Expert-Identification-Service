package i5.las2peer.services.servicePackage.metrics;

import i5.las2peer.services.servicePackage.database.entities.UserEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sathvik
 */

public class ReciprocalRank implements IEvaluator<Double> {

    private double rr = 0;
    private Map<Long, UserEntity> userId2userObj;
    private LinkedHashMap<String, Double> userId2score;
    private int windowSize;

    public ReciprocalRank(LinkedHashMap<String, Double> userId2score, Map<Long, UserEntity> userId2userObj, int count) {
	this.userId2userObj = userId2userObj;
	this.userId2score = userId2score;
	this.windowSize = count;
    }

    public Double getValue() {
	return rr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.metrics.IEvaluator#compute()
     */
    @Override
    public void compute() {
	List<Long> reputations = new ArrayList<Long>();

	Set<String> expertkeys = userId2score.keySet();
	int count = 0;
	for (String userid : expertkeys) {
	    if (count < windowSize) {
		UserEntity user = userId2userObj.get(Long.valueOf(userid));
		if (user != null) {
		    reputations.add(user.getReputation());
		    // System.out.println(" Userid " + user.getUserId() +
		    // " Rep..." + user.getReputation());
		}

	    } else {
		break;
	    }
	    count++;
	}
	rr = getPositionOfHighestReputaiton(reputations);

    }

    private double getPositionOfHighestReputaiton(List<Long> reputations) {
	if (reputations != null && reputations.size() > 0) {
	    long max = Collections.max(reputations);
	    return 1.0 / (reputations.indexOf(max) + 1);
	}
	return 0;
    }
}
