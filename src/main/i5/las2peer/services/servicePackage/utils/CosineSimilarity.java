package i5.las2peer.services.servicePackage.utils;

import java.util.List;

/**
 * @author sathvik
 */

public class CosineSimilarity {
    /**
     * 
     * @param vector1
     * @param vector2
     * @return
     */
    public static Double calculateCosineSimilarity(List<Double> vector1, List<Double> vector2) {
	Double similarity = 0.0;
	Double sum = 0.0; // the numerator of the cosine similarity
	Double fnorm = 0.0; // the first part of the denominator of the cosine
			    // similarity
	Double snorm = 0.0; // the second part of the denominator of the cosine
			    // similarity

	for (int i = 0; i < vector1.size(); i++) {
	    sum = sum + vector1.get(i) * vector2.get(i);
	}

	fnorm = calculateNorm(vector1);
	snorm = calculateNorm(vector2);
	similarity = sum / (fnorm * snorm);
	return similarity;
    }

    public static Double calculateNorm(List<Double> vector) {
	Double norm = 0.0;

	for (Double val : vector) {
	    norm = norm + Math.pow(val, 2);
	}
	return Math.sqrt(norm);
    }
}
