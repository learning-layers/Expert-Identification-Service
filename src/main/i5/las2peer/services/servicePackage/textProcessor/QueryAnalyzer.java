/**
 * 
 */
package i5.las2peer.services.servicePackage.textProcessor;

import i5.las2peer.services.servicePackage.database.entities.QueryEntity;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * This class analyzes the query text and inserts into database to uniquely
 * identify this query.
 * 
 * @author sathvik
 *
 */
public class QueryAnalyzer {

    private String queryText;
    private QueryEntity queryEntity = null;

    public QueryAnalyzer(String text) {
	queryText = text;
    }

    public String getText() {
	// TODO: If stemming is requested, stem it.
	StopWordRemover remover = null;
	String cleanstr = null;

	try {
	    // TODO: Semantic analysis of the text.
	    remover = new StopWordRemover(queryText);
	    cleanstr = remover.getPlainText();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return cleanstr;
    }

    /**
     * 
     * @param connSrc
     *            ConnectionSource to access the mysql database.
     * @return An id of the inserted row.
     */
    public long getId(ConnectionSource connSrc) {
	// Save the text to the Db and generate an Id.
	try {
	    Dao<QueryEntity, Long> QueryDao = DaoManager.createDao(connSrc, QueryEntity.class);
	    queryEntity = new QueryEntity();
	    queryEntity.setText(queryText);
	    queryEntity.setDate(new Date());

	    QueryDao.createIfNotExists(queryEntity);
	    return queryEntity.getId();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return -1;
    }

}
