/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import i5.las2peer.services.servicePackage.database.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */

// TODO: Check factory pattern for this.
public class EvaluationMeasure {
    private LinkedHashMap<String, Double> userId2Score = new LinkedHashMap<String, Double>();
    private Precision precision;
    private Recall recall;
    private PrecisionRecall precision_recall;
    private ElevenPointInterpolatedAveragePrecision epIAP;
    private ReciprocalRank rr;
    private NormalizedDiscountedCumulativeGain ndcg;
    private String algoName;
    private Map<Long, UserEntity> userId2userObj;
    private EvaluationMetricsEntity evaluationEntity;

    private static int collectionSize = 30;

    public EvaluationMeasure(LinkedHashMap<String, Double> id2Score, Map<Long, UserEntity> userId2userObj, String name) {
	this.userId2Score = id2Score;
	this.algoName = name;
	this.userId2userObj = userId2userObj;
    }

    public void computeAll() throws IOException {
	computePrecision();
	computeRecall();
	computePrecisionRecall();
	compute11ptIP();
	computeMRR();
	computeNormalDiscountedCumilativeGain();
    }

    private void computeNormalDiscountedCumilativeGain() {
	ndcg = new NormalizedDiscountedCumulativeGain(userId2Score, userId2userObj, collectionSize);
	ndcg.compute();
    }

    public void computePrecision() {
	precision = new Precision(userId2Score, userId2userObj, collectionSize);
	precision.compute();
    }

    public void computeRecall() {
	recall = new Recall(userId2Score, userId2userObj, collectionSize);
	recall.compute();
    }

    public void computePrecisionRecall() throws IOException {
	//System.out.println("PRECISION - RECALL ::");
	if (precision == null) {
	    computePrecision();
	}

	if (recall == null) {
	    computeRecall();
	}

	precision_recall = new PrecisionRecall(precision.getRoundedPrecisionValues(), recall.getRoundedValues());
	// precision_recall.savePrecisionRecallCSV1();
	precision_recall.compute();
    }

    public void compute11ptIP() {
	//System.out.println("11 pt Interpolated precision ::");
	epIAP = new ElevenPointInterpolatedAveragePrecision();
	epIAP.compute(recall.getRoundedValues(), precision.getRoundedPrecisionValues());

	epIAP.compute(ArrayUtils.toPrimitive(precision_recall.getRecallValues()), ArrayUtils.toPrimitive(precision_recall.getPrecisionValues()));

	// epap.save();
    }

    public void computeMAP() {

    }

    public void computeMRR() {
	rr = new ReciprocalRank(userId2Score, userId2userObj, collectionSize);
	rr.compute();
    }

    public void save(long queryId, ConnectionSource connSrc) throws JsonIOException, JsonSyntaxException, IOException {

	JsonObject jContainer = new JsonObject();
	JsonObject metricsObj = new JsonObject();

	// JsonArray jPrecision = new JsonArray();
	// for (double value : precision.getRoundedPrecisionValues()) {
	// JsonPrimitive element = new JsonPrimitive(value);
	// jPrecision.add(element);
	// }

	// JsonArray jRecall = new JsonArray();
	// for (double value : recall.getRoundedValues()) {
	// JsonPrimitive element = new JsonPrimitive(value);
	// jRecall.add(element);
	// }

	LinkedHashMap<Double, Double> recall2precicion = epIAP.getValue();
	JsonArray jIARecallPts = new JsonArray();
	JsonArray jIAPrecisionPts = new JsonArray();

	Iterator entrySetIterator = recall2precicion.entrySet().iterator();
	while (entrySetIterator.hasNext()) {
	    Map.Entry pair = (Map.Entry) entrySetIterator.next();

	    JsonPrimitive standardRecallPts = new JsonPrimitive((double) pair.getKey());
	    JsonPrimitive ipPrecision = new JsonPrimitive((double) pair.getValue());

	    // System.out.println((double) pair.getKey() + " :: " + (double)
	    // pair.getValue());

	    entrySetIterator.remove(); // To avoid
				       // concurrentModificationException

	    jIARecallPts.add(standardRecallPts);
	    jIAPrecisionPts.add(ipPrecision);
	}

	JsonObject IAPObject = new JsonObject();
	IAPObject.add("recall", jIARecallPts);
	IAPObject.add("precision", jIAPrecisionPts);

	Iterator prIterator = precision_recall.getRecallPrecisionMap().entrySet().iterator();
	JsonArray jRecalls = new JsonArray();
	JsonArray jPrecisions = new JsonArray();

	//System.out.println("Iterating precision recall...");
	while (prIterator.hasNext()) {
	    Map.Entry pair = (Map.Entry) prIterator.next();

	    //System.out.println((double) pair.getKey() + " :: " + (double) pair.getValue());

	    JsonPrimitive recalls = new JsonPrimitive((double) pair.getKey());
	    JsonPrimitive precision = new JsonPrimitive((double) pair.getValue());

	    prIterator.remove(); // To avoid concurrentModificationException

	    jRecalls.add(recalls);
	    jPrecisions.add(precision);
	}

	JsonObject precRecallObj = new JsonObject();
	precRecallObj.add("recall", jRecalls);
	precRecallObj.add("precision", jPrecisions);

	JsonPrimitive precisionElement = new JsonPrimitive(String.format("%.3f", precision.getValue()));
	JsonPrimitive recallElement = new JsonPrimitive(String.format("%.3f", recall.getValue()));
	JsonPrimitive rrElement = new JsonPrimitive(String.format("%.3f", rr.getValue()));
	JsonPrimitive ndcgElement = new JsonPrimitive(String.format("%.3f", ndcg.getValue()));

	metricsObj.add("avgPrecision", precisionElement);
	metricsObj.add("avgRecall", recallElement);
	metricsObj.add("precisionRecalls", precRecallObj);
	metricsObj.add("11-ptIAP", IAPObject);
	metricsObj.add("reciprocalRank", rrElement);
	metricsObj.add("ndcg", ndcgElement);

	JsonObject containerObj = new JsonObject();
	containerObj.addProperty("id", queryId);
	containerObj.addProperty("algo", algoName);
	containerObj.addProperty("timestamp", System.currentTimeMillis());
	containerObj.add("measures", metricsObj);

	jContainer.add("metrics", containerObj);
	String evaluationMeasures = jContainer.toString();

	try {
	    Dao<EvaluationMetricsEntity, Long> EvaluationDao = DaoManager.createDao(connSrc, EvaluationMetricsEntity.class);

	    evaluationEntity = new EvaluationMetricsEntity();
	    evaluationEntity.setQueryId(queryId);
	    evaluationEntity.setCreateDate(new Date());
	    evaluationEntity.setMetrics(evaluationMeasures);

	    EvaluationDao.createIfNotExists(evaluationEntity);

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    public long getId() {
	if (evaluationEntity != null) {
	    return evaluationEntity.getId();
	}
	return -1;
    }

}
