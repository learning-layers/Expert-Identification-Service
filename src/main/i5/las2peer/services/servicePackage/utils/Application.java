package i5.las2peer.services.servicePackage.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author sathvik
 */

public class Application {

    public static String dateInfo = "";
    public static String algoName = "";
    public static String intraWeight = "";

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
	List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
	Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
	    @Override
	    public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
		return (o2.getValue()).compareTo(o1.getValue());
	    }
	});

	LinkedHashMap<K, V> result = new LinkedHashMap<>();
	for (Map.Entry<K, V> entry : list) {
	    result.put(entry.getKey(), entry.getValue());
	}
	return result;
    }

    public static double round(double value, int places) {
	if (places < 0)
	    throw new IllegalArgumentException();

	BigDecimal bd = new BigDecimal(value);
	bd = bd.setScale(places, RoundingMode.HALF_UP);
	return bd.doubleValue();
    }

    public static double roundDouble(double value) {
	double rounded = (double) Math.round(value * 100000) / 100000;
	return rounded;
    }

    public static void writeListToFile(List<Double> values) {

	try {

	    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("evaluationResults/" + algoName + ".csv", true)))) {
		DecimalFormat df = new DecimalFormat("#");
		for (int i = 0; i < values.size(); i++) {
		    df.setMaximumFractionDigits(12);
		    String result = df.format(values.get(i).doubleValue());
		    System.out.println("double val is " + result);
		    out.print(result);
		    if (i < (values.size() - 1)) {
			out.print(";");
		    }
		}

		out.print("\n");
	    }
	} catch (IOException e) {
	    // exception handling left as an exercise for the reader
	}

    }

    public static ArrayList<String> getFilterWords() {

	ArrayList<String> words = new ArrayList<String>();
	words.add("Article Feedback 5 Additional Articles");
	words.add("World Wide Web");
	words.add("Greek loanwords");
	words.add("Spamming");
	words.add("Search engine optimization");
	words.add("Microformats");
	words.add("World Wide Web Consortium standards");

	return words;

    }


}
