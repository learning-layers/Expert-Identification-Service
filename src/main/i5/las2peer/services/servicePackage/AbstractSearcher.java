/**
 * 
 */
package i5.las2peer.services.servicePackage;

import i5.las2peer.services.servicePackage.database.DatabaseHandler;
import i5.las2peer.services.servicePackage.database.entities.DataInfoEntity;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.exceptions.ERSException;
import i5.las2peer.services.servicePackage.graph.GraphWriter;
import i5.las2peer.services.servicePackage.graph.JUNGGraphCreator;
import i5.las2peer.services.servicePackage.lucene.searcher.LuceneSearcher;
import i5.las2peer.services.servicePackage.metrics.EvaluationMeasure;
import i5.las2peer.services.servicePackage.textProcessor.QueryAnalyzer;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSBundle;
import i5.las2peer.services.servicePackage.utils.ExceptionMessages;
import i5.las2peer.services.servicePackage.utils.UserMapSingleton;
import i5.las2peer.services.servicePackage.visualizer.Visualizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.search.TopDocs;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 *         This class is a base class which handles connecting to the database,
 *         analyzes the query, searches the index and creates a graph.
 */
public abstract class AbstractSearcher {
    protected String databaseName = null;
    protected DatabaseHandler dbHandler = null;
    protected ConnectionSource connectionSource = null;

    private QueryAnalyzer qAnalyzer = null;
    private long queryId;
    public long expertsId;
    public long eMeasureId;
    public long visId;

    LuceneSearcher searcher = null;

    protected JUNGGraphCreator jcreator = null;
    protected Map<Long, UserEntity> usermap = null;
    protected ERSBundle requestParameters = null;
    protected GraphWriter graphWriter = null;

    /**
     * 
     * @param requestParameters
     *            Request parameters from the client is bundled together for
     *            further processing.
     * @throws ERSException
     */
    public AbstractSearcher(ERSBundle requestParameters) throws ERSException {
	this.requestParameters = requestParameters;
	start();
    }

    /**
     * This method performs the standard operations and ends with creating graph
     * on which graph algorithms can be applied.
     * 
     * @throws ERSException
     */
    private void start() throws ERSException {
	connect();
	analyseQuery();
	searchIndex();
	createGraph();
    }

    /**
     * Connects to the database corresponding to the dataset that is requested
     * by client.
     * 
     * NOTE: Every dataset is given a unique id when it is uploaded to the
     * server.
     */
    public void connect() {
	databaseName = getDatabaseName(requestParameters.datasetId);
	if (databaseName == null) {
	    // Throw custom exception.
	}

	dbHandler = new DatabaseHandler(databaseName, "root", "");

	connectionSource = dbHandler.getConnectionSource();

	Application.algoName = requestParameters.algorithmName;
	// Application.intraWeight = intraWeight;
	System.out.println("Intra weight:: " + Application.intraWeight);
	dbHandler.truncateEvaluationTable();
    }

    /**
     * @throws ERSException
     *             A custom exception is thrown if the query parameter of
     *             request parameter is null or empty.
     * 
     */
    public void analyseQuery() throws ERSException {
	if (requestParameters.query == null || requestParameters.query.length() < 0) {
	    throw new ERSException(ExceptionMessages.QUERY_NOT_VALID);
	}

	if (requestParameters.query != null && requestParameters.query.length() < 0) {
	    throw new ERSException(ExceptionMessages.REQ_NOT_VALID);
	}

	try {
	    qAnalyzer = new QueryAnalyzer(requestParameters.query);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	queryId = qAnalyzer.getId(connectionSource);
	try {
	    usermap = UserMapSingleton.getInstance().getUserMap(connectionSource);
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}
	if (usermap == null) {
	    throw new ERSException(ExceptionMessages.USERS_NOT_VALID);
	}
    }

    /**
     * @throws ERSException
     *             An exception is thrown while searching in index fails.
     * 
     */
    public void searchIndex() throws ERSException {
	try {
	    // System.out.println("Performing search...");
	    searcher = new LuceneSearcher(qAnalyzer.getText(), databaseName + "_index");
	    TopDocs docs = searcher.performSearch(qAnalyzer.getText(), Integer.MAX_VALUE);
	    Date dateFilter = null;
	    searcher.buildQnAMap(docs, dateFilter);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ERSException(ExceptionMessages.INDEX_SEARCH_EXCEPTION);
	}
    }

    /**
     * This method creates a graph using generated question and answer map and
     * posts to user map. Generated graph is saved into a file that will be used
     * for uploading to OCD service. Further, generated graph is also saved into
     * database.
     * 
     * @throws ERSException
     *             An exception is thrown if generation of graph fails.
     */
    public void createGraph() throws ERSException {

	try {
	    // System.out.println("Performing search...");
	    jcreator = new JUNGGraphCreator();
	    jcreator.createGraph(searcher.getQnAMap(), searcher.getPostId2UserIdMap());
	    // System.out.println("Graph created");

	    graphWriter = new GraphWriter(jcreator);
	    graphWriter.saveToGraphMl("graph_jung.graphml");
	    graphWriter.saveToDb(queryId, connectionSource);

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ERSException(ExceptionMessages.CREATE_GRAPH_EXCEPTION);
	}

    }

    /**
     * This method saves the evaluation measures and visualization of the
     * expert network into database
     * 
     * @param expert2score
     *            A map associating expert user id to their score.
     * @param experts
     *            A json string consisting of expert details.
     */
    public void save(LinkedHashMap<String, Double> expert2score, String experts) {
	expertsId = dbHandler.addExperts(queryId, experts);

	// If evaluation is requested.

	if (this.requestParameters.isEvaluation) {
	    EvaluationMeasure eMeasure = new EvaluationMeasure(expert2score, usermap, requestParameters.algorithmName);

	    // Compute Evaluation Measures.
	    try {
		eMeasure.computeAll();
		eMeasure.save(queryId, connectionSource);
		eMeasureId = eMeasure.getId();
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (JsonIOException e) {
		e.printStackTrace();
	    } catch (JsonSyntaxException e) {
		e.printStackTrace();
	    }

	}

	// If Visualization is requested, Visualize the result
	if (this.requestParameters.isVisualization) {
	    LinkedHashMap<String, Double> influentialNodes = new LinkedHashMap<String, Double>();
	    Iterator<String> it = expert2score.keySet().iterator();

	    // TODO: Remove this static constant. Externalize it.
	    int count = 10;
	    int i = 0;
	    while (it.hasNext() && i < count) {
		String key = it.next();
		influentialNodes.put(key, expert2score.get(key));
		i++;
	    }

	    Visualizer visualizer = new Visualizer();
	    visualizer.saveVisGraph(graphWriter.getId(), influentialNodes, connectionSource);
	    visId = visualizer.getId();
	}
    }

    /**
     * 
     * @param datasetId
     *            An id identifying the dataset. Ids are stored in a database
     *            called ersdb.
     * 
     * @return Returns the database name associated with a particular dataset.
     */
    private String getDatabaseName(String datasetId) {
	DatabaseHandler handler = new DatabaseHandler("ersdb", "root", "");
	String databaseName = null;
	try {
	    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
	    DataInfoEntity datasetEntity = DatasetInfoDao.queryForId(Long.parseLong(datasetId));
	    databaseName = datasetEntity.getDatabaseName();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	handler.close();

	return databaseName;
    }

    /**
     * This method closes any open database connection.
     */
    public void close() {
	dbHandler.close();
    }

}
