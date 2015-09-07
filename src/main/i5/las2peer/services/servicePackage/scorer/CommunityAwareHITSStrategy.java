package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.AbstractSearcher;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.exceptions.ERSException;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;
import i5.las2peer.services.servicePackage.ocd.OCD;
import i5.las2peer.services.servicePackage.parsers.xmlparser.CommunityCoverMatrixParser;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSBundle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import edu.uci.ics.jung.algorithms.scoring.HITS;

/**
 * @author sathvik
 */

public class CommunityAwareHITSStrategy extends AbstractSearcher implements ScoreStrategy {
    public Map<String, Double> node2hitsscore = new HashMap<String, Double>();
    private int maxIterations = 30;
    private double tolerance = 0.0000001d;
    private LinkedHashMap<String, Double> expert2score;
    private String experts;

    /**
     * 
     * @param params
     *            Collection of parameters.
     * @throws ERSException
     *             Throws custom exception, see {@link AbstractSearcher}
     */
    public CommunityAwareHITSStrategy(ERSBundle params) throws ERSException {
	super(params);
    }

    /**
     * 
     */
    @Override
    public void executeAlgorithm() {

	OCD ocdHITS = new OCD();
	ocdHITS.start(super.graphWriter.getGraphAsString("graph_jung.graphml"));
	String coversHITS = ocdHITS.getCovers();
	if (coversHITS == null) {
	    // Throw exception or switch to classic algorithms.
	}
	CommunityCoverMatrixParser CCMPHits = new CommunityCoverMatrixParser(coversHITS);
	CCMPHits.parse();

	CAwareHITS<String, RelationshipEdge> ranker = new CAwareHITS<String, RelationshipEdge>(super.jcreator.getGraph(),
		CCMPHits.getNodeId2CoversMap(), Double.parseDouble(super.requestParameters.alpha),
		Double.parseDouble(super.requestParameters.intraWeight));

	ranker.setTolerance(tolerance);
	ranker.setMaxIterations(maxIterations);
	ranker.evaluate();

	for (String v : super.jcreator.getGraph().getVertices()) {
	    HITS.Scores scores = (HITS.Scores) ranker.getVertexScore(v);
	    node2hitsscore.put(v, scores.authority);
	}

    }

    /**
     * 
     */
    @Override
    public String getExperts() {
	return experts;
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
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#saveResults()
     */
    @Override
    public void saveResults() {
	expert2score = Application.sortByValue(node2hitsscore);

	int count = 0;
	JSONArray jsonArray = new JSONArray();

	for (String userid : expert2score.keySet()) {
	    // Restrict result to 10 items for now.
	    if (count < 10) {
		UserEntity user = super.usermap.get(Long.parseLong(userid));
		user.setScore(node2hitsscore.get(userid));

		if (user != null) {
		    jsonArray.add(user);

		} else {
		    break;
		}
		count++;
	    }
	}
	experts = jsonArray.toJSONString();
	super.save(expert2score, experts);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * i5.las2peer.services.servicePackage.scorer.ScoreStrategy#getExpertId()
     */
    @Override
    public long getExpertsId() {
	return super.eMeasureId;
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
