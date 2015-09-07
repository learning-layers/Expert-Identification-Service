package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.AbstractSearcher;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.exceptions.ERSException;
import i5.las2peer.services.servicePackage.graph.RelationshipEdge;
import i5.las2peer.services.servicePackage.ocd.OCD;
import i5.las2peer.services.servicePackage.parsers.xmlparser.CommunityCoverMatrixParser;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minidev.json.JSONArray;

/**
 * @author sathvik
 */

public class CommunityAwarePageRankStrategy extends AbstractSearcher implements ScoreStrategy {
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
    public CommunityAwarePageRankStrategy(ERSBundle params) throws ERSException {
	super(params);
    }

    /**
     * 
     */
    @Override
    public void executeAlgorithm() {

	// Get covers and mapping.
	OCD ocdPageRank = new OCD();
	ocdPageRank.start(super.graphWriter.getGraphAsString("graph_jung.graphml"));
	String covers = ocdPageRank.getCovers();
	if (covers == null) {
	    // Throw exception or switch to classic algorithms.
	}
	CommunityCoverMatrixParser CCMP = new CommunityCoverMatrixParser(covers);
	CCMP.parse();

	CAwarePageRank<String, RelationshipEdge> ranker = new CAwarePageRank<String, RelationshipEdge>(super.jcreator.getGraph(),
		Double.parseDouble(super.requestParameters.alpha), CCMP.getNodeId2CoversMap(),
		Double.parseDouble(super.requestParameters.intraWeight));

	ranker.setTolerance(tolerance);
	ranker.setMaxIterations(maxIterations);
	ranker.evaluate();

	for (String v : super.jcreator.getGraph().getVertices()) {
	    double score = ranker.getVertexScore(v);
	    // System.out.println("Executed score ::"+score);
	    node2hitsscore.put(v, score);
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
	// ArrayList expertScores = new ArrayList<Double>();
	for (String userid : expert2score.keySet()) {
	    count++;
	    // Restrict result to 10 items for now.
	    if (count < 10) {
		UserEntity user = super.usermap.get(Long.parseLong(userid));
		user.setScore(node2hitsscore.get(userid));

		ArrayList<String> labels = super.jcreator.getConnectedLabels(userid);
		user.setRelatedPosts(labels);

		// String result = String.format("%.14f",
		// node2hitsscore.get(userid));
		// System.out.println("CPageRAnk Score -- "+result);
		// System.out.println("CPageRAnk Score Rounded:: "+Application.roundDouble(node2hitsscore.get(userid)));
		// expertScores.add(node2hitsscore.get(userid));
		if (user != null) {
		    jsonArray.add(user);
		}

	    } else {
		break;
	    }

	}

	// Application.writeListToFile(expertScores);

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
