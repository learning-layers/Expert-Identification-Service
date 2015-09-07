/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

import i5.las2peer.services.servicePackage.parsers.csvparser.PostCSV;
import i5.las2peer.services.servicePackage.parsers.csvparser.UserCSV;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.googlecode.jcsv.annotations.internal.ValueProcessorProvider;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.AnnotationEntryParser;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

/**
 * @author sathvik
 *
 */
public class ERSCSVParser implements IParser<List<PostCSV>, List<UserCSV>> {

    private String filePath;
    private Reader reader;
    private ValueProcessorProvider provider;
    private static final String csvFileName = "data.csv";

    public ERSCSVParser(String dirPath) {
	this.filePath = dirPath + "/" + csvFileName;
	this.provider = new ValueProcessorProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IParser#getPosts()
     */
    @Override
    public List<PostCSV> getPosts() {

	List<PostCSV> posts_list = null;
	try {
	    reader = new FileReader(filePath);

	    CSVEntryParser<PostCSV> entryParser = new AnnotationEntryParser<PostCSV>(PostCSV.class, provider);
	    CSVReader<PostCSV> csvPostReader = new CSVReaderBuilder<PostCSV>(reader).entryParser(entryParser).build();

	    posts_list = csvPostReader.readAll();
	} catch (IOException e) {
	    System.out.println("Exception while reading" + e);
	    e.printStackTrace();
	}

	return posts_list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see i5.las2peer.services.servicePackage.parsers.IParser#getUsers()
     */
    @Override
    public List<UserCSV> getUsers() {
	List<UserCSV> userList = null;
	try {
	    reader = new FileReader(filePath);
	    
	    CSVEntryParser<UserCSV> entryUserParser = new AnnotationEntryParser<UserCSV>(UserCSV.class, provider);
	    CSVReader<UserCSV> csvUserReader = new CSVReaderBuilder<UserCSV>(reader)
		    .entryParser(entryUserParser).build();

	    userList = csvUserReader.readAll();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return userList;
    }

}
