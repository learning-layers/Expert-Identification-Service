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

	JSONArray jsonArray = new JSONArray();

	for (String userid : expert2score.keySet()) {
	    if (super.MAX_RESULTS > 0) {
		UserEntity user = super.usermap.get(Long.parseLong(userid));
		user.setScore(node2hitsscore.get(userid));

		ArrayList<String> labels = super.jcreator.getConnectedLabels(userid);
		user.setRelatedPosts(labels);

		if (user != null) {
		    jsonArray.add(user);
		}
	    } else {
		break;
	    }
	    super.MAX_RESULTS--;
	}

	experts = jsonArray.toJSONString();

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
