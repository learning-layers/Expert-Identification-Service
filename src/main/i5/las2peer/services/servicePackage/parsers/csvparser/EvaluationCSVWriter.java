/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers.csvparser;

import i5.las2peer.services.servicePackage.database.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.database.entities.QueryEntity;
import i5.las2peer.services.servicePackage.ocd.OCD;
import i5.las2peer.services.servicePackage.utils.Application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvWriter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */
public class EvaluationCSVWriter {
	List<EvaluationCSV> evaluationResults;

	public EvaluationCSVWriter() {

	}

	public void extractResultsfromDb(ConnectionSource connSrc) {

		evaluationResults = new ArrayList<EvaluationCSV>();
		List<EvaluationMetricsEntity> entities = null;
		try {
			Dao<EvaluationMetricsEntity, Long> EvaluationDao = DaoManager
					.createDao(connSrc, EvaluationMetricsEntity.class);
			entities = EvaluationDao.queryForAll();

			Dao<QueryEntity, Long> queryDao = DaoManager.createDao(connSrc,
					QueryEntity.class);

			for (EvaluationMetricsEntity entity : entities) {
				String jevaluation = entity.getMetrics();
				// System.out.println("METRICS:: " + jevaluation);
				long queryId = entity.getQueryId();

				JsonParser parser = new JsonParser();
				JsonObject jObj = parser.parse(jevaluation).getAsJsonObject()
						.get("metrics").getAsJsonObject();
				JsonObject jmetricsObj = jObj.get("measures").getAsJsonObject();
				JsonObject jIAPObj = jmetricsObj.get("11-ptIAP")
						.getAsJsonObject();
				JsonObject jPrecRecallObj = jmetricsObj.get("precisionRecalls")
						.getAsJsonObject();

				System.out.println(jPrecRecallObj.get("precision")
						.getAsJsonArray().toString());
				// System.out.println(jPrecRecallObj.get("precision").getAsJsonArray().getAsString());

				EvaluationCSV evalCSV = new EvaluationCSV();
				evalCSV.setPrecision(jmetricsObj.get("avgPrecision")
						.getAsString());
				evalCSV.setRecall(jmetricsObj.get("avgRecall").getAsString());
				evalCSV.setPrecisions(jPrecRecallObj.get("precision")
						.getAsJsonArray().toString());
				evalCSV.setRecalls(jPrecRecallObj.get("recall")
						.getAsJsonArray().toString());
				evalCSV.setEpIAPrecisions(jIAPObj.get("precision")
						.getAsJsonArray().toString());
				evalCSV.setEpIAPStandardRecallPts(jIAPObj.get("recall")
						.getAsJsonArray().toString());
				evalCSV.setNdcg(jmetricsObj.get("ndcg").getAsString());
				evalCSV.setRr(jmetricsObj.get("reciprocalRank").getAsString());
				evalCSV.setAlgoName(jObj.get("algo").getAsString());

				QueryEntity qEntity = queryDao.queryForId(queryId);
				evalCSV.setQuery(qEntity.getName());

				evaluationResults.add(evalCSV);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void write(String filepath) {
		Writer out;
		try {
			out = new FileWriter(filepath);

			CSVWriter<EvaluationCSV> csvWriter = new CSVWriterBuilder<EvaluationCSV>(
					out).entryConverter(new EvaluationCSVConverter()).build();

			out.write(Application.dateInfo + ", alpha(intra weight) = "
					+ Application.intraWeight + ", OCD Algo = "
					+ OCD.ALGORITHM_LABEL + " Algo= " + Application.algoName
					+ "\n");

			if (csvWriter != null && evaluationResults != null) {
				csvWriter.writeAll(evaluationResults);
				csvWriter.flush();
			} else {
				System.out.println("evaluationResults writer is null...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	

}