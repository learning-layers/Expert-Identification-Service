/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers.csvparser;

import com.googlecode.jcsv.writer.CSVEntryConverter;

/**
 * @author sathvik
 *
 */
public class EvaluationCSVConverter implements CSVEntryConverter<EvaluationCSV> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.googlecode.jcsv.writer.CSVEntryConverter#convertEntry(java.lang.Object
     * )
     */
    @Override
    public String[] convertEntry(EvaluationCSV evalCSV) {
	String[] columns = new String[10];

	columns[0] = evalCSV.getPrecision();
	columns[1] = evalCSV.getRecall();
	columns[2] = evalCSV.getPrecisions();
	columns[3] = evalCSV.getRecalls();
	columns[4] = evalCSV.getEpIAPrecisions();
	columns[5] = evalCSV.getStandardRecallPts();
	columns[6] = evalCSV.getNdcg();
	columns[7] = evalCSV.getRr();
	columns[8] = evalCSV.getAlgoName();
	columns[9] = evalCSV.getQuery();

	return columns;
    }
}
