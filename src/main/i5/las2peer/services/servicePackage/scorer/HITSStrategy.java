package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.AbstractSearcher;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.exceptions.ERSException;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import edu.uci.ics.jung.algorithms.scoring.HITS;

/**
 * @author sathvik
 */

public class HITSStrategy extends AbstractSearcher implements ScoreStrategy {
    public Map<String, Double> node2hitsscore = new HashMap<String, Double>();

    private int maxIterations = 30;
    private double tolerance = 0.0000001d;
    private LinkedHashMap<String, Double> expert2score;
    private String experts;

    /**
     * 
     * @param parameters
     * @throws ERSException
     */
    public HITSStrategy(ERSBundle parameters) throws ERSException {
	super(parameters);
    }

    @Override
    public void executeAlgorithm() {

	HITS ranker = new HITS(super.jcreator.getGraph());

	ranker.setTolerance(this.tolerance);
	ranker.setMaxIterations(this.maxIterations);
	ranker.evaluate();

	for (String v : super.jcreator.getGraph().getVertices()) {
	    HITS.Scores scores = (HITS.Scores) ranker.getVertexScore(v);
	    node2hitsscore.put(v, scores.authority);
	}

    }

    @Override
    public String getExperts() {
	return experts;
    }

    public void saveResults() {
	expert2score = Application.sortByValue(node2hitsscore);

	int i = 0;
	JSONArray jsonArray = new JSONArray();

	for (String userid : expert2score.keySet()) {
	    i++;
	    // Restrict result to 10 items for now.
	    if (i < 10) {
		UserEntity user = super.usermap.get(Long.parseLong(userid));
		user.setScore(node2hitsscore.get(userid));

		ArrayList<String> labels = super.jcreator.getConnectedLabels(userid);
		user.setRelatedPosts(labels);

		// try {
		// HashMap<String, Integer> tag2count = new HashMap<String,
		// Integer>();
		// if (labels != null && labels.size() > 0) {
		// for (String label : labels) {
		// if (label != null) {
		// String tags = super.dbHandler.getSemanticTags(label);
		// if (tags != null && tags.length() > 0) {
		// String[] tagAr = tags.split(",");
		// if (tagAr != null && tagAr.length > 0) {
		// for (String tag : tagAr) {
		// System.out.println("TAG:: ");
		// if (tag != null && tag.length() > 0) {
		// // System.out.println("TAG:: " +
		// // tag);
		// if (tag2count.containsKey(tag)) {
		// tag2count.put(tag, tag2count.get(tag) + 1);
		// } else {
		// tag2count.put(tag, 1);
		// }
		// }
		// }
		// }
		// }
		// }
		//
		// }
		//
		// LinkedHashMap<String, Integer> tag2countSorted =
		// Application.sortByValue(tag2count);
		// // StringBuilder actualTags = new StringBuilder();
		// Iterator<String> it1 = tag2countSorted.keySet().iterator();
		// int count = 0;
		// ArrayList<String> reqTags = new ArrayList<String>();
		// while (it1.hasNext() && count < 3) {
		// reqTags.add(it1.next());
		// count++;
		// }
		//
		// user.setTags(Joiner.on(",").join(reqTags));
		// }
		// } catch (Exception e) {
		// System.out.print(e.toString());
		// }

		if (user != null) {
		    jsonArray.add(user);
		}
	    } else {
		break;
	    }
	}

	experts = jsonArray.toJSONString();

	System.out.println(experts);

	super.save(expert2score, experts);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scoring.ScoreStrategy#getExpertMap()
     */
    @Override
    public LinkedHashMap<String, Double> getExpertMap() {
	return expert2score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#getExpertId()
     */
    @Override
    public long getExpertsId() {
	return super.expertsId;
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
	return super.eMeasureId;
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
	return super.visId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.scorer.ScoreStrategy#close()
     */
    @Override
    public void close() {
	super.close();
    }

}
