/**
 * 
 */
package i5.las2peer.services.servicePackage.metrics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author sathvik
 *
 */
public class MeanAveragePrecision {
	private double meanAvgPrec = 0;
	public MeanAveragePrecision() {

	}

	public double getValue() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"average_precision_list.txt"));
		String line;
		int no_of_lines = 0;
		while ((line = br.readLine()) != null) {
			// process the line.
			no_of_lines++;
			meanAvgPrec = meanAvgPrec + Double.parseDouble(line);
		}
		br.close();
		return no_of_lines == 0 ? 0 : (meanAvgPrec / no_of_lines);

	}
}

