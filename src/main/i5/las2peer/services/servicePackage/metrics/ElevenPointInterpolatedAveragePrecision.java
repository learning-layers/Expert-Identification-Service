/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import java.util.LinkedHashMap;

/**
 * @author sathvik
 *
 */
public class ElevenPointInterpolatedAveragePrecision {
    private double[] standard_recall_values;
    private double[] inter_prec_values;
    private LinkedHashMap<Double, Double> recall2InterPolatedPrecision;

    public ElevenPointInterpolatedAveragePrecision() {
	recall2InterPolatedPrecision = new LinkedHashMap<Double, Double>();
    }

    public int findIndexOfNextPeak(double[] array, int currIndex) {
	int index_of_max_value = currIndex;
	double value_to_check = -1;
	double curr_peak_value = -1;

	for (int i = currIndex; i < array.length; i++) {
	    value_to_check = array[i];

	    if (value_to_check >= curr_peak_value) {
		index_of_max_value = i;
		curr_peak_value = value_to_check;
	    }
	}
	// System.out.println(curr_value + " :: " + index_of_max_value);
	return index_of_max_value;
    }

    public void compute(double[] recall_values, double[] precision_values) {
	int curr_index = 0;
	int next_peak_index = 0;
	standard_recall_values = new double[recall_values.length];
	inter_prec_values = new double[precision_values.length];

	while (curr_index < precision_values.length) {
	    next_peak_index = findIndexOfNextPeak(precision_values, curr_index);
	    if (next_peak_index > precision_values.length - 1) {
		break;
	    }

	    for (int j = curr_index; j <= next_peak_index && j < recall_values.length; j++) {
		precision_values[j] = precision_values[next_peak_index];

		standard_recall_values[j] = recall_values[j];
		inter_prec_values[j] = precision_values[j];

	    }
	    if (curr_index == next_peak_index) {
		curr_index = curr_index + 1;
	    } else {
		curr_index = next_peak_index;
	    }

	}

	for (int i = 0; i < standard_recall_values.length && i < inter_prec_values.length; i++) {
	    recall2InterPolatedPrecision.put(standard_recall_values[i], inter_prec_values[i]);
	}
    }

    public LinkedHashMap<Double, Double> getValue() {
	return recall2InterPolatedPrecision;
    }

}
