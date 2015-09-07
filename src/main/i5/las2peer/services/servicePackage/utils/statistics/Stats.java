/**
 * 
 */
package i5.las2peer.services.servicePackage.utils.statistics;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author sathvik
 *
 */
public class Stats {
    double[] values;
    double[] normalized_values;

    double min;
    double max;
    double mean;
    double median;
    double lower_quartile;
    double upper_quartile;

    DescriptiveStatistics stats;

    /**
     * 
     * @param values
     */
    public Stats(List<Long> values) {
	this.stats = new DescriptiveStatistics();
	

	// TODO: check if you can avoid this casting...
	this.values = new double[values.size()];

	for (int i = 0; i < this.values.length; i++) {
	    this.values[i] = values.get(i);
	    stats.addValue(values.get(i));
	}

	// normalized_values = new double[values.size()];
	//
	// this.normalized_values = StatUtils.normalize(this.values);
	//
	// for (double value : this.normalized_values) {
	// stats.addValue(value);
	// }

    }

    public double getLowerQuartile() {
	return stats.getPercentile(25);
    }

    public double getMedian() {
	return stats.getPercentile(50);
    }

    public double getUpperQuartile() {
	return stats.getPercentile(75);
    }

    public double getPercentileAbove(double value) {
	return stats.getPercentile(value);
    }

    public double getMean() {
	return stats.getMean();
    }

    public double getStandardDeviation() {
	return stats.getStandardDeviation();
    }

    public double getMin() {
	return stats.getMin();
    }

    public double getMax() {
	return stats.getMax();
    }

    public void calculateStandardMetrics() {
	mean = getMean();
	median = getMedian();
	lower_quartile = getLowerQuartile();
	upper_quartile = getUpperQuartile();
	min = getMin();
	max = getMax();
    }

    public void saveMetrics(String filepath) {

    }

    public int getRelevanceScore(long groundTruthValue) {

	if (groundTruthValue > stats.getPercentile(90)) {
	    return 3;
	} else if (groundTruthValue > stats.getPercentile(75)) {
	    return 2;
	} else if (groundTruthValue > stats.getPercentile(55)) {
	    return 1;
	}
	return 0;
    }
}
