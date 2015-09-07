package i5.las2peer.services.servicePackage.scorer;

import i5.las2peer.services.servicePackage.ocd.NodeCoverParser;

import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.HITSWithPriors;
import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author sathvik
 * @param <V>
 */

public class CAwareHITS<V, E> extends HITSWithPriors<V, E> {
	private HashMap<Long, NodeCoverParser> nodeId2Covers;
	public static double intraWeight = 0.6;

	/**
	 * 
	 * @param g
	 * @param edge_weights
	 * @param alpha
	 * @param nodeId2Covers
	 */
	public CAwareHITS(Graph<V, E> g, Transformer<E, Double> edge_weights,
			double alpha, HashMap<Long, NodeCoverParser> nodeId2Covers) {
		super(g, edge_weights, ScoringUtils.getHITSUniformRootPrior(g
				.getVertices()), alpha);
		this.nodeId2Covers = nodeId2Covers;
	}

	/**
	 * 
	 * @param g
	 * @param alpha
	 * @param nodeId2Covers
	 */
	public CAwareHITS(Graph<V, E> g,
			HashMap<Long, NodeCoverParser> nodeId2Covers, double alpha,
			double intraWeight) {
		super(g, ScoringUtils.getHITSUniformRootPrior(g.getVertices()), alpha);
		this.nodeId2Covers = nodeId2Covers;
		CAwareHITS.intraWeight = intraWeight;
	}

	/**
	 * Updates the value for this vertex taking into consideration about
	 * neighboring covers.
	 * 
	 * @param v
	 *            A vertex id, generally a long value.
	 */
	@Override
	protected double update(V v) {
		collectDisappearingPotential(v);
		// System.out.println("Vertex id is" + v);
		NodeCoverParser ncManager = nodeId2Covers.get(Long.parseLong(v
				.toString()));

		// If cannot find a cover, do a regular HITS update.
		if (ncManager == null) {
			System.out
					.println("Not able to find cover, falling back to normal HITS update..");
			super.update(v);
		}

		long currCoverId = ncManager.getDominantCover();

		// This is to be calculated depending on the Community
		double v_auth = 0;
		for (E e : graph.getInEdges(v)) {
			int incident_count = getAdjustedIncidentCount(e);
			for (V w : graph.getIncidentVertices(e)) {
				NodeCoverParser neighborNCManager = nodeId2Covers.get(Long
						.parseLong(w.toString()));
				long neighbourCoverId = neighborNCManager.getDominantCover();

				if (!w.equals(v) || hyperedges_are_self_loops) {
					double hubweight = getCurrentValue(w).hub;

					// If current node and neighboring node do not belong to
					// same cover(community) reduce the incoming hub weight from
					// the external node.
					if (currCoverId != neighbourCoverId) {
						hubweight = hubweight * (1 - intraWeight);
					} else {
						hubweight = hubweight * intraWeight;
					}
					// Update depending on community.
					v_auth += (hubweight * getEdgeWeight(w, e).doubleValue() / incident_count);
				}
			}
		}

		double v_hub = 0;
		for (E e : graph.getOutEdges(v)) {
			int incident_count = getAdjustedIncidentCount(e);
			for (V w : graph.getIncidentVertices(e)) {
				NodeCoverParser neighborNCManager = nodeId2Covers.get(Long
						.parseLong(w.toString()));
				long neighbourCoverId = neighborNCManager.getDominantCover();

				if (!w.equals(v) || hyperedges_are_self_loops) {
					double authweight = getCurrentValue(w).authority;

					// If current node and neighboring node does not belong to
					// same cover(community) reduce the incoming authority
					// weight from the external node.
					if (currCoverId != neighbourCoverId) {
						authweight = authweight * (1 - intraWeight);
					} else {
						authweight = authweight * intraWeight;
					}

					// Update depending on community.
					v_hub += (authweight * getEdgeWeight(w, e).doubleValue() / incident_count);

				}
			}
		}

		// modify total_input according to alpha
		if (alpha > 0) {
			v_auth = v_auth * (1 - alpha) + getVertexPrior(v).authority * alpha;
			v_hub = v_hub * (1 - alpha) + getVertexPrior(v).hub * alpha;
		}
		setOutputValue(v, new HITS.Scores(v_hub, v_auth));

		return Math.max(Math.abs(getCurrentValue(v).hub - v_hub),
				Math.abs(getCurrentValue(v).authority - v_auth));
	}
}
