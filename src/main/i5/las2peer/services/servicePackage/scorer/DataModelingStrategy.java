package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.mapper.SematicsMapper;
import i5.las2peer.services.servicePackage.mapper.TextMapper;
import i5.las2peer.services.servicePackage.utils.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;

import com.google.common.collect.HashMultimap;

/**
 * @author sathvik
 */

public class DataModelingStrategy implements ScoreStrategy {
    double alpha = 0.5;
    private Map<Long, Double> userId2score = new HashMap<Long, Double>();
    LinkedHashMap<Long, Double> expert2score;
    // private Map<Long, UserEntity> mUserId2userObj;
    private TextMapper mTextIndexer;
    private SematicsMapper mSemanticsIndexer;
    private Map<Long, UserEntity> userId2userObj;
    private HashMultimap userId2postIds;

    /**
     * 
     * @param textIndexer
     * @param semanticsIndexer
     */
    public DataModelingStrategy(TextMapper textIndexer, SematicsMapper semanticsIndexer, Map<Long, UserEntity> userId2userObj, double alpha) {
	this.alpha = alpha;
	mTextIndexer = textIndexer;
	mSemanticsIndexer = semanticsIndexer;
	this.userId2userObj = userId2userObj;

	userId2postIds = HashMultimap.create();
    }

    @Override
    public void executeAlgorithm() {
	System.out.println("Ranking the res.." + mTextIndexer.getTokenMap().entries().size());
	try {
	    for (Map.Entry entry : mTextIndexer.getTokenMap().entries()) {
		long postid = (Long) entry.getKey();
		// Set the title of the post that is associated with the user.
		if (mTextIndexer.getPostId2UserIdMap().containsKey(postid)) {
		    long userid = mTextIndexer.getPostId2UserIdMap().get(postid);
		    UserEntity user = userId2userObj.get(userid);
		    if (userid > 0 && user != null) {
			// user.setRelatedPost(postid);
			// Set termObjs = TERM_FREQ_MAP.get(postid);
			// if (termObjs.size() > 0) {
			// Resource res = (Resource) termObjs.iterator().next();
			// user.setTitle(res.getText());
		    } else {
			// user.setTitle("Title is empty");
		    }

		    double sum = 0;
		    double termFreq_inverseResFreq = mTextIndexer.getTfIrfMap().get(postid);

		    double entityFreq_inverseResFreq = 0;

		    // Entites are mapped only if terms are present, this should
		    // be changed in future when creating entity freq inverse
		    // res freq map.
		    if (mSemanticsIndexer.getEfirfMap().get(postid) != null) {
			entityFreq_inverseResFreq = mSemanticsIndexer.getEfirfMap().get(postid);
		    }

		    // Scoring rule.
		    sum = (alpha) * termFreq_inverseResFreq + (1 - alpha) * entityFreq_inverseResFreq;
		    userId2score.put(userid, sum);
		    userId2postIds.put(userid, postid);

		    System.out.println("SUM::" + sum);
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Ranking the res completed..");
    }

    public String getExperts() {
	ArrayList<String> relatedPosts = new ArrayList();
	JSONArray jsonArray = new JSONArray();
	try {
	    expert2score = Application.sortByValue(userId2score);

	    int i = 0;

	    for (long userid : expert2score.keySet()) {

		// Restrict result to 10 items for now.
		if (i < 10) {
		    UserEntity user = userId2userObj.get(userid);
		    user.setScore(userId2score.get(userid));
		    relatedPosts.addAll(userId2postIds.get(userid));
		    user.setRelatedPosts(relatedPosts);
		    if (user != null) {
			jsonArray.add(user);
		    }
		} else {
		    break;
		}
		i++;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return jsonArray.toJSONString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scoring.ScoreStrategy#getExpertMap()
     */
    @Override
    public LinkedHashMap<String, Double> getExpertMap() {
	// Avoid this, try to change in the exper2score map itself.
	LinkedHashMap<String, Double> expertMap = new LinkedHashMap<String, Double>();
	for (Map.Entry<Long, Double> entry : expert2score.entrySet()) {
	    expertMap.put(String.valueOf(entry.getKey()), entry.getValue());

	}
	return expertMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#saveResults()
     */
    @Override
    public void saveResults() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#getExpertsId()
     */
    @Override
    public long getExpertsId() {
	// TODO Auto-generated method stub
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#getEvaluationId
     * ()
     */
    @Override
    public long getEvaluationId() {
	// TODO Auto-generated method stub
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#getVisualizationId
     * ()
     */
    @Override
    public long getVisualizationId() {
	// TODO Auto-generated method stub
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.scorer.ScoreStrategy#close()
     */
    @Override
    public void close() {
	// TODO Auto-generated method stub

    }

}
