package i5.las2peer.services.servicePackage.textProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         Removes Stop words from the text.
 *         By default, lucene stop words are removed.
 *         One can specify other included stopwords too.
 *
 */
public class StopWordRemover {
    private String text;
    private String mFilePath;

    private Pattern stopWordsPattern;
    private String lang;

    StringBuilder builder = new StringBuilder();
    String pattern = "\\b(?:%s)\\b";

    public StopWordRemover(String text, String lang) {
	this.text = text;
	this.lang = lang;
    }

    public StopWordRemover(String text) {
	this(text, "en");
    }

    public String getPlainText() {
	if (mFilePath != null) {
	    createPatternFromFile();
	} else {
	    createPattern();
	}

	if (text == null) {
	    return "";
	}
	// Remove all html tags.
	String stripped_text = Jsoup.parse(text).text();
	// Remove punctuations.
	stripped_text = stripped_text.replaceAll("\\W", " ");

	if (stopWordsPattern != null) {
	    Matcher matcher = stopWordsPattern.matcher(stripped_text);
	    stripped_text = matcher.replaceAll("");
	}

	return stripped_text;
    }

    private void createPattern() {
	String[] stopwords = new StopWordList().getStopWords(lang);
	if (stopwords != null) {
	    for (String word : stopwords) {
		builder.append(word);
		builder.append("|");
	    }
	    pattern = String.format(pattern, builder);

	    stopWordsPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}
    }

    // TODO: Test this pattern.
    public void createPatternFromFile() {

	if (mFilePath != null) {
	    BufferedReader br;
	    try {
		ClassLoader classLoader = getClass().getClassLoader();
		br = new BufferedReader(new InputStreamReader((classLoader.getResourceAsStream(mFilePath))));
		// File f = new File(filepath);
		// Utils.println("Abs path is:: "+f.getAbsolutePath());
		// br = new BufferedReader(new FileReader(f.getAbsolutePath()));
		String line;
		while ((line = br.readLine()) != null) {
		    builder.append(line);
		    builder.append("|");
		}

		pattern = String.format(pattern, builder);
		br.close();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	stopWordsPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

}
