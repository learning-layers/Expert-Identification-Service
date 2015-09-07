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
import edu.uci.ics.jung.algorithms.scoring.PageRank;

/**
 * @author sathvik
 */

public class PageRankStrategy extends AbstractSearcher implements ScoreStrategy {

    private Map<String, Double> node2pagescore = new HashMap<String, Double>();
    private int maxIterations = 30;
    private double tolerance = 0.0000001d;
    private double alpha = 0.15d;
    private LinkedHashMap<String, Double> expert2score;
    private String experts;

    /**
     * 
     * @param parameters
     * @throws ERSException
     */
    public PageRankStrategy(ERSBundle parameters) throws ERSException {
	super(parameters);
	// this.alpha = super.requestParameters.alpha;
    }

    @Override
    public void executeAlgorithm() {
	if (super.requestParameters.alpha != null && super.requestParameters.alpha.length() > 0) {
	    this.alpha = Double.parseDouble(super.requestParameters.alpha);
	}

	PageRank ranker = new PageRank(super.jcreator.getGraph(), this.alpha);
	ranker.setTolerance(this.tolerance);
	ranker.setMaxIterations(this.maxIterations);
	ranker.evaluate();

	for (String v : super.jcreator.getGraph().getVertices()) {
	    node2pagescore.put(v, (Double) ranker.getVertexScore(v));
	}

    }

    @Override
    public String getExperts() {
	return experts;
    }

    public void saveResults() {
	expert2score = Application.sortByValue(node2pagescore);

	int count = 0;
	JSONArray jsonArray = new JSONArray();

	for (String userid : expert2score.keySet()) {
	    count++;
	    // Restrict result to 10 items for now.
	    if (count < 10) {
		UserEntity user = super.usermap.get(Long.parseLong(userid));
		user.setScore(node2pagescore.get(userid));

		ArrayList<String> labels = super.jcreator.getConnectedLabels(userid);
		user.setRelatedPosts(labels);

		if (user != null) {
		    jsonArray.add(user);
		}
	    } else {
		break;
	    }
	}

	experts = jsonArray.toJSONString();
	super.save(expert2score, experts);
    }

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
