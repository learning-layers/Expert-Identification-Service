package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.ocd.NodeCoverParser;

import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author sathvik
 * @param <V>
 */

public class CAwarePageRank<V, E> extends PageRankWithPriors<V, E> {
    private HashMap<Long, NodeCoverParser> nodeId2Covers;
    public static double intraWeight = 0.8;

    /**
     * 
     * @param g
     * @param edge_weights
     * @param alpha
     * @param nodeId2Covers
     */
    public CAwarePageRank(Graph<V, E> g, Transformer<E, Double> edge_weights, double alpha, HashMap<Long, NodeCoverParser> nodeId2Covers) {
	super(g, edge_weights, ScoringUtils.getUniformRootPrior(g.getVertices()), alpha);
	this.nodeId2Covers = nodeId2Covers;

    }

    /**
     * 
     * @param g
     * @param alpha
     * @param nodeId2Covers
     */
    public CAwarePageRank(Graph<V, E> g, double alpha, HashMap<Long, NodeCoverParser> nodeId2Covers, double intraWeight) {
	super(g, ScoringUtils.getUniformRootPrior(g.getVertices()), alpha);
	this.nodeId2Covers = nodeId2Covers;
	CAwarePageRank.intraWeight = intraWeight;
    }

    @Override
    public double update(V v) {

	NodeCoverParser ncManager = nodeId2Covers.get(Long.parseLong(v.toString()));

	// If cannot find a cover, do a regular PageRank update.
	if (ncManager == null) {
	    System.out.println("Not able to find cover, falling back to normal Pagerank update..");
	    super.update(v);
	}

	long currCoverId = ncManager.getDominantCover();

	// This is to be calculated depending on the community
	double v_input = 0;
	collectDisappearingPotential(v);

	for (E e : graph.getInEdges(v)) {
	    int incident_count = getAdjustedIncidentCount(e);
	    for (V w : graph.getIncidentVertices(e)) {
		NodeCoverParser neighborNCManager = nodeId2Covers.get(Long.parseLong(w.toString()));
		long neighbourCoverId = neighborNCManager.getDominantCover();

		if (!w.equals(v) || hyperedges_are_self_loops) {
		    double edgeWeight = getEdgeWeight(w, e).doubleValue();
		    if (currCoverId != neighbourCoverId) {
			edgeWeight = edgeWeight * (1 - intraWeight);
		    } else {
			edgeWeight = edgeWeight * intraWeight;
		    }

		    // System.out.println("edgeWeight "+edgeWeight);
		    v_input += (getCurrentValue(w) * edgeWeight / incident_count);
		}
	    }
	}

	// modify total_input according to alpha
	double new_value = alpha > 0 ? v_input * (1 - alpha) + getVertexPrior(v) * alpha : v_input;
	setOutputValue(v, new_value);

	return Math.abs(getCurrentValue(v) - new_value);

    }
}
