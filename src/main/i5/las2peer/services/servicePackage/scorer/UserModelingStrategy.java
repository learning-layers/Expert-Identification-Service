package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.utils.Application;

import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minidev.json.JSONArray;

/**
 * @author sathvik
 */

public class UserModelingStrategy implements ScoreStrategy {
	HashMap<String, Double> userId2Score = new HashMap<String, Double>();
	long currentdate = System.currentTimeMillis() / 1000L; // In millisec.
	double decayfactor = 0.2; // Find out the value for this, Not present in
							// research paper.

	@Override
	public void executeAlgorithm() {

		// Step1: Add All the values in postid 2 Resource Map.
		// Step2: Iterate all post id, get userid.
		// Step3: For user calculate idf(p)
		// Multiply idf(p) with Step1 value which is the score for the user.
		// Sort the map by value.

		// double sum_of_res_score = 0;
		// Set<String> postids = Application.postid2Resource.keySet();
		// long age = 0;
		// for (String postid : postids) {
		// Resource res = Application.postid2Resource.get(postid);
		// age = currentdate - res.getLastEditDate();
		// if(age < 0 ) {
		// System.out.println("Age ..." + age);
		// System.out.println("currentdate" + currentdate);
		// System.out
		// .println("Last edit date ..." + res.getLastEditDate());
		// }
		//
		// // TODO : Add time decay to the resource.
		// sum_of_res_score += (Math.exp(-decayfactor * age)
		// * res.getPopularity() * res.getCosineSimilarity());
		// }
		//
		// double score = sum_of_res_score;
		// for (String postid : postids) {
		// String userid = Application.postId2userId1.get(postid);
		// User user = Application.userId2userObj.get(userid);
		//
		// double idf_of_p = 1;
		// if (user != null) {
		// if (user.getResFreq() != null) {
		// // This can be done in background and stored in db.
		// double user_association_weight = Double.parseDouble(user
		// .getResFreq());
		// if (user_association_weight > 0) {
		// idf_of_p = Math
		// .log((Application.totalNoOfResources / user_association_weight));
		// }
		//
		// score = idf_of_p * sum_of_res_score;
		// }
		// userId2Score.put(userid, score);
		// } else {
		// System.out.println("User is Anonymous.. Ignoring... ");
		// }
		//
		// }

	}

	public String getExperts() {
		LinkedHashMap<String, Double> experts = Application
				.sortByValue(userId2Score);

		int i = 0;
		JSONArray jsonArray = new JSONArray();

		// for (String userid : experts.keySet()) {
		// i++;
		// // Restrict result to 10 items for now.
		// if (i < 10) {
		// User user = Application.userId2userObj.get(userid);
		//
		// // TODO: Change to setScore.
		// user.setRank(String.valueOf(userId2Score.get(userid)));
		// if (user != null) {
		// jsonArray.add(user);
		// }
		// } else {
		// break;
		// }
		// }

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
		// TODO Auto-generated method stub
		return null;
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
