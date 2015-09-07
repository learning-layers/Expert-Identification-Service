/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sathvik
 *
 */
public class PrecisionRecall {

    // Used to insert standard recall points and default precision value.
    ArrayList<Double> recall_values = new ArrayList<Double>();
    ArrayList<Double> precision_values = new ArrayList<Double>();

    LinkedHashMap<Double, Double> recall2precision = new LinkedHashMap<Double, Double>();

    public PrecisionRecall(double[] precisions, double[] recalls) {
	for (int i = 0; i < precisions.length && i < recalls.length; i++) {
	    recall2precision.put(recalls[i], precisions[i]);
	}
    }

    public LinkedHashMap<Double, Double> getRecallPrecisionMap() {
	return recall2precision;
    }

    public void compute() {
	// Insert standard recall points to exisiting points.
	double[] standard_recall_pts = { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };

	for (Map.Entry<Double, Double> entry : recall2precision.entrySet()) {
	    double recall = entry.getKey();
	    double precision = entry.getValue();

	    recall_values.add(recall);
	    precision_values.add(precision);

	}

	// If standard recall points are not present, insert at right position.
	for (int i = 0; i < standard_recall_pts.length; i++) {
	    if (recall_values.contains(standard_recall_pts[i]) == false) {
		int index = getIndex(standard_recall_pts[i], recall_values);
		// System.out.println("INDEX:: " + index);
		if (index != -1) {
		    recall_values.add(index, standard_recall_pts[i]);
		    precision_values.add(index, 0.0);
		}
	    }
	}

    }

    public Double[] getRecallValues() {
	Double[] values = new Double[recall_values.size()];
	recall_values.toArray(values);
	return values;
    }

    public Double[] getPrecisionValues() {
	Double[] values = new Double[precision_values.size()];
	precision_values.toArray(values);

	for (double val : precision_values) {
	    System.out.println("VAL:: " + val);
	}
	return values;
    }

    // public void savePrecisionRecallCSV() throws IOException {
    //
    // try (PrintWriter out = new PrintWriter(new BufferedWriter(new
    // FileWriter(precision_recall_filepath, false)))) {
    // for (Map.Entry<Double, Double> mapEntry : recall2precision.entrySet()) {
    //
    // double recall = mapEntry.getKey();
    // double precision = mapEntry.getValue();
    //
    // String text_to_write = precision + "," + recall;
    // out.println(text_to_write);
    // }
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // }
    //
    // public void savePrecisionRecallCSV1() throws IOException {
    // BufferedReader precisionBr = new BufferedReader(new
    // FileReader(precision_filepath));
    // BufferedReader recallBr = new BufferedReader(new
    // FileReader(recall_filepath));
    //
    // // Open FileWriter, Read the two files line by line and append values.
    // try (PrintWriter out = new PrintWriter(new BufferedWriter(new
    // FileWriter(precision_recall_filepath, false)))) {
    //
    // while (true) {
    // String precision = precisionBr.readLine();
    // String recall = recallBr.readLine();
    //
    // if (precision == null || recall == null)
    // break;
    //
    // // recallValues2PrecisionValue.put(
    // // Global.round(Double.parseDouble(recall), 1),
    // // Global.round(Double.parseDouble(precision), 1));
    // String text_to_write = precision + "," + recall;
    //
    // out.println(text_to_write);
    //
    // }
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // precisionBr.close();
    // recallBr.close();
    // }

    private int getIndex(double value, ArrayList<Double> values) {
	int j = 0;
	while (j < values.size()) {
	    if (values.get(j) > value) {
		return j;
	    }
	    j++;
	}

	return -1;
    }

}
