package i5.las2peer.services.servicePackage;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.servicePackage.database.DatabaseHandler;
import i5.las2peer.services.servicePackage.database.entities.DataEntity;
import i5.las2peer.services.servicePackage.database.entities.DataInfoEntity;
import i5.las2peer.services.servicePackage.database.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.database.entities.ExpertEntity;
import i5.las2peer.services.servicePackage.database.entities.GraphEntity;
import i5.las2peer.services.servicePackage.database.entities.QueryEntity;
import i5.las2peer.services.servicePackage.database.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.database.entities.UserClickDetails;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.exceptions.ERSException;
import i5.las2peer.services.servicePackage.lucene.indexer.LuceneMysqlIndexer;
import i5.las2peer.services.servicePackage.lucene.searcher.LuceneSearcher;
import i5.las2peer.services.servicePackage.mapper.SematicsMapper;
import i5.las2peer.services.servicePackage.mapper.TextMapper;
import i5.las2peer.services.servicePackage.metrics.EvaluationMeasure;
import i5.las2peer.services.servicePackage.parsers.ERSCSVParser;
import i5.las2peer.services.servicePackage.parsers.ERSJsonParser;
import i5.las2peer.services.servicePackage.parsers.User;
import i5.las2peer.services.servicePackage.parsers.XMLParser;
import i5.las2peer.services.servicePackage.parsers.csvparser.UserCSV;
import i5.las2peer.services.servicePackage.scorer.CommunityAwareHITSStrategy;
import i5.las2peer.services.servicePackage.scorer.CommunityAwarePageRankStrategy;
import i5.las2peer.services.servicePackage.scorer.DataModelingStrategy;
import i5.las2peer.services.servicePackage.scorer.HITSStrategy;
import i5.las2peer.services.servicePackage.scorer.PageRankStrategy;
import i5.las2peer.services.servicePackage.scorer.ScoreStrategy;
import i5.las2peer.services.servicePackage.scorer.ScoringContext;
import i5.las2peer.services.servicePackage.textProcessor.QueryAnalyzer;
import i5.las2peer.services.servicePackage.utils.AlgorithmType;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSBundle;
import i5.las2peer.services.servicePackage.utils.ERSMessage;
import i5.las2peer.services.servicePackage.utils.ExceptionMessages;
import i5.las2peer.services.servicePackage.utils.LocalFileManager;
import i5.las2peer.services.servicePackage.utils.UserMapSingleton;
import i5.las2peer.services.servicePackage.utils.semanticTagger.RelatedPostsExtractor;
import i5.las2peer.services.servicePackage.utils.semanticTagger.TagExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
/**
 * @author sathvik
 */


@Path("ers")
@Version("1.0") // this annotation is used by the XML mapper
@Api
@SwaggerDefinition(
		info = @Info(
				title = "Expert Recommender Service",
				version = "1.0",
				description = "An open source expert recommender system to recommend experts from the given data.",
				termsOfService = "",
				contact = @Contact(
						name = "Sathvik Parekodi",
						url = "",
						email = "sathvik.parekodi@rwth-aachen.de"
				),
				license = @License(
						name = "Apache License 2",
						url = "http://www.apache.org/licenses/LICENSE-2.0"
				)
		))
public class ExpertRecommenderService extends Service {

    private Log log = LogFactory.getLog(ExpertRecommenderService.class);

    public ExpertRecommenderService() {
		// read and set properties values
		// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED
		// TO BE CHANGED TOO!
		setFieldValues();
    }

    
    @GET
	@Path("/validation")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(value = {	@ApiResponse(code = 200, message = "Validation Confirmation")})
	@ApiOperation(value = "User Validation",
		notes = "Simple function to validate a user login.")
    public HttpResponse validateLogin() {
		String returnString = "";
		returnString += "You are " + ((UserAgent) getActiveAgent()).getLoginName() + " and your login is valid!";

		return new HttpResponse(returnString, 200);
	}
    
    /**
     * 
     * @return
     */
    @GET
    @Path("datasets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 500, message = "Connection or Query Failure") })
	@ApiOperation(value = "Get Datasets on Server.",
		notes = "Returns the available datasets on the server.")
    public HttpResponse getAvailableDatasets() {

		DatabaseHandler handler = new DatabaseHandler();
	
		JsonArray datasetsObj = null;
		try {
		    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
		    List<DataInfoEntity> datasets = DatasetInfoDao.queryForAll();
	
		    datasetsObj = new JsonArray();
		    for (DataInfoEntity entity : datasets) {
				String name = entity.getDisplayName();
				long id = entity.getId();
				String description = "NA";
		
				JsonObject jObj = new JsonObject();
				jObj.addProperty(ERSMessage.NAME, name);
				jObj.addProperty(ERSMessage.ID, id);
				jObj.addProperty(ERSMessage.DESCRIPTION, description);
		
				datasetsObj.add(jObj);
		    }
	
		} catch (SQLException e) {
		    e.printStackTrace();
		    HttpResponse res = new HttpResponse(ERSMessage.DATABASE_CONNECT_FAILURE);
		    res.setStatus(500);
		    return res;
		}
	
		HttpResponse res = new HttpResponse(datasetsObj.toString());
		res.setStatus(200);
		return res;
    }
        
    @POST
    @Path("prepareDataset/{datasetName}")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input"), @ApiResponse(code = 500, message = "Connection or Query Failure") })
	@ApiOperation(value = "Prepare Database for Uploading Data from Dataset.",
		notes = "Creates the main table if not exists already, then adds dataset info to main table and creates all the required tables required for the dataset and recommendation service.")
    public HttpResponse prepareDataset(@PathParam("datasetName") String datasetName, @ContentParam String displayName) {

		if (datasetName == null || datasetName.length() > 64) {
		    try {
		    	throw new ERSException(ERSMessage.DATASET_NAME_INVALID);
		    } catch (ERSException e) {
				e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		DatabaseHandler handler = new DatabaseHandler();
		DataInfoEntity entity = new DataInfoEntity();
		ConnectionSource connectionSrc;
		
		try {
		    connectionSrc = handler.getConnectionSource();
		    // Create main table.
		    TableUtils.createTableIfNotExists(connectionSrc, DataInfoEntity.class);
		} catch (SQLException e) {
		    e.printStackTrace();
			HttpResponse res = new HttpResponse(e.getMessage());
			res.setStatus(500);
			return res;
		}

		try {
		    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
	
		    QueryBuilder<DataInfoEntity, Long> qb = DatasetInfoDao.queryBuilder();
		    qb.where().eq("dataset_name", datasetName);
		    List<DataInfoEntity> entities = qb.query();
	
		    // If entry is present, return the id. Else create and insert the
		    // entity.
		    if (entities != null && entities.size() > 0) {
		    	entity = entities.get(0);
		    } else {
				entity.setDatasetName(datasetName);
				if (displayName == null)
				    displayName = datasetName;
		
				entity.setDisplayName(displayName);
		
				Calendar cal = Calendar.getInstance();
				entity.setDate(cal.getTime());
		
				entity.setFilepath(datasetName);
				entity.setIndexFilepath(datasetName + "_index");
				DatasetInfoDao.create(entity);
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
			HttpResponse res = new HttpResponse(e.toString());
			res.setStatus(500);
			return res;
		}
	
		// Create database and necessary tables in the database.
		try {
		    connectionSrc = handler.getConnectionSource();
		    
		    // Create necessary tables
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc, DataEntity.class, datasetName));
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc , UserEntity.class, datasetName));
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc , SemanticTagEntity.class, datasetName));
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc , QueryEntity.class, datasetName));
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc , EvaluationMetricsEntity.class, datasetName));
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc , GraphEntity.class, datasetName));
		    TableUtils.createTableIfNotExists(connectionSrc, handler.getEntityConfigOfDataSet(connectionSrc , ExpertEntity.class, datasetName));

		} catch (SQLException e) {
		    e.printStackTrace();
			HttpResponse res = new HttpResponse(e.getMessage());
			res.setStatus(500);
			return res;
		}
	
		HttpResponse res = new HttpResponse(String.valueOf(entity.getId()));
		res.setStatus(200);
		return res;
    }

    /**
     * This method parses data from remote url or from the local file on the
     * server.
     * 
     * @param id
     *            Id of the dataset corresponding to the database to update.
     *            This is obtained while preparing the database.
     * @param type
     *            Input format of the dataset (xml, csv, json)
     * @param urlObject
     *            A remote urls wrapped in json object to parse the data from.
     * 
     * @return A string representing if the update was success or failure.
     */
    @POST
    @Path("datasets/{datasetId}/parse")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input"), @ApiResponse(code = 500, message = "Connection or Query Failure") })
	@ApiOperation(value = "Parse the Data.",
		notes = "Parses the data files and add it to database.")
    public HttpResponse parse(@PathParam("datasetId") String id, @ContentParam String urlObject,
    	@DefaultValue("xml") @QueryParam("format") String type) {

		// log.info("URL::" + urlObject);
		HttpResponse res = null;
	
		String datasetName = getDatasetName(id);
		if (datasetName == null) {
		    try {
		    	throw new ERSException(ERSMessage.DATASET_NOT_CONFIGURED);
		    } catch (ERSException e) {
			    e.printStackTrace();
			    res = new HttpResponse(e.getMessage());
			    res.setStatus(400);
			    return res;
		    }
		}
	
		boolean isLocal = false;
		String postsPath = null;
		String usersPath = null;
		if (!TextUtils.isEmpty(urlObject)) {
		    String[] urls = urlObject.split(",");
		    postsPath = urls[0];
		    usersPath = urls[1];
		    if(!postsPath.startsWith("http") && !postsPath.startsWith("ftp")){
		    	isLocal = true;
		    }
		} else {
		    isLocal = true;
		    postsPath = "datasets/" + datasetName + "/posts.xml";
		    usersPath = "datasets/" + datasetName + "/users.xml";
		}
	
		log.info(postsPath);
		log.info(usersPath);
	
		DatabaseHandler dbHandler = new DatabaseHandler();
	
		try {
		    if (type.equalsIgnoreCase("xml")) {
				log.info("Executing XML Parser...");
				XMLParser xmlparser = new XMLParser();
				xmlparser.parseData(postsPath, isLocal);
				dbHandler.addPosts(datasetName, xmlparser.getPosts());
		
				xmlparser.parseUserData(usersPath, isLocal);
				dbHandler.addUsers(datasetName, xmlparser.getUsers());
		    } else if (type.equalsIgnoreCase("csv")) {
	
				log.info("Executing CSV Parser...");
				// User details are extracted from posts data file itself.
				// (data.csv)
		
				postsPath = "datasets/" + datasetName + "/data.csv";
				ERSCSVParser csvparser = new ERSCSVParser(postsPath);
				dbHandler.addPosts(datasetName, csvparser.getPosts());
				List<UserCSV> users = csvparser.getUsers();
		
				if (users != null && users.size() > 0) {
				    dbHandler.addUsers(datasetName, users);
				}
		    } else if (type.equalsIgnoreCase("json")) {
				log.info("Executing Json Parser...");
		
				ERSJsonParser jsonparser = new ERSJsonParser(postsPath);
				dbHandler.addPosts(datasetName, jsonparser.getPosts());
				List<User> users = jsonparser.getUsers();
				if (users != null && users.size() > 0) {
				    dbHandler.addUsers(datasetName, users);
				}
		    } else {
		    	throw new ERSException(ERSMessage.UNSUPPORTED_TYPE);
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		    res = new HttpResponse(ERSMessage.SQL_FAILURE);
		    res.setStatus(500);
		    return res;
		} catch (ERSException e) {
		    e.printStackTrace();
		    res = new HttpResponse(ERSMessage.UNSUPPORTED_TYPE);
		    res.setStatus(400);
		    return res;
		}
	
		res = new HttpResponse(ERSMessage.SUCCESS);
		res.setStatus(200);
		return res;
    }

    /**
     * 
     * @param id
     *            Id of the dataset returned in the first step while preparing
     *            the database.
     * @return A success or failure string.
     */
    @POST
    @Path("datasets/{datasetId}/indexer")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Indexing success"), @ApiResponse(code = 500, message = "Indexing, Connection or Query Failure") })
	@ApiOperation(value = "Index the Data.",
		notes = "Indexes the text stored in the database for the given dataset.")
    public HttpResponse index(@PathParam("datasetId") String id) {

		String datasetName = getDatasetName(id);
		String indexDir = getIndexDirectory(id);
	
		DatabaseHandler dbHandler = new DatabaseHandler();
		HttpResponse res = null;
		try {
		    LuceneMysqlIndexer indexer = new LuceneMysqlIndexer(datasetName, dbHandler.getConnectionSource(), indexDir);
		    indexer.buildIndex();
		    res = new HttpResponse(ERSMessage.INDEX_SUCCESS);
			res.setStatus(200);
		} catch (SQLException e) {
		    e.printStackTrace();
		    res = new HttpResponse(ERSMessage.SQL_FAILURE);
			res.setStatus(500);
		} catch (Exception e) {
		    e.printStackTrace();
		    res = new HttpResponse(ERSMessage.INDEX_FAILURE);
			res.setStatus(500);
		}
	
		return res;
    }

    /**
     * 
     * @param datasetId
     * @return
     */
    @POST
    @Path("datasets/{datasetId}/semantics")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 500, message = "Semantic Addition, Connection or Query Failure") })
	@ApiOperation(value = "Add Semantic to Dataset.",
		notes = "Adds semantic tags to the posts in the dataset.")
    public HttpResponse addSemantics(@PathParam(value = "datasetId") String datasetId) {

		String datasetName = getDatasetName(datasetId);
		DatabaseHandler dbHandler = new DatabaseHandler();
	
		HttpResponse res;
		try {
		    dbHandler.addSemanticTags(datasetName);
		} catch (Exception e) {
		    res = new HttpResponse(ERSMessage.ADD_SEMANTICS_FAILURE, 500);
		    return res;
		}
	
		res = new HttpResponse(ERSMessage.ADD_SEMANTICS_SUCCESS, 200);
		return res;
    }
    
    /**
     * 
     * @param datasetId
     *            Long value identifying the corresponding dataset.
     * @param algorithmName
     *            String value specifying the algorithm name. pagerank,
     *            hits,communityAwareHITS.
     * @param query
     *            String value specifying the query/information need.
     * @param isEvaluation
     *            A boolean value to identify if evaluation is required or not.
     * @param isVisualization
     *            A boolean value to identify if visualization is required or
     *            not.
     * @return 200 response code and A json string containing list of ids i.e
     *         expertId, evaluationId, visualizationId.
     */
    @POST
    @Path("datasets/{datasetId}/algorithms/{algorithmName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 500, message = "Connection or Query Failure") })
	@ApiOperation(value = "Execute the Requested Algorithm on the Dataset.",
		notes = "Returns the id of the expert collection, id of evaluation metrics and id of the visualization.")
    public HttpResponse applyAlgorithm(@PathParam("datasetId") String datasetId, @PathParam("algorithmName") String algorithmName,
	    @ContentParam String query, 
	    @DefaultValue("false") @QueryParam("evaluation") boolean isEvaluation,
	    @DefaultValue("true") @QueryParam("visualization") boolean isVisualization,
	    @DefaultValue("0.15d") @QueryParam("alpha") String alpha, 
	    @DefaultValue("0.6") @QueryParam("intra") String intraWeight) {

	    ERSBundle properties = new ERSBundle.Builder(datasetId, query, algorithmName).alpha(alpha).intraWeight(intraWeight)
			.isEvaluation(isEvaluation)
			.isVisualization(isVisualization).build();
	
		ScoreStrategy strategy = null;
		ScoringContext scontext = null;
		try {
	
		    // JDK 7 offers switch on strings instead of creating enums.
		    switch (algorithmName == null ? "" : algorithmName) {
			    case AlgorithmType.PAGE_RANK:
					log.info("Applying PageRank strategy...");
					strategy = new PageRankStrategy(properties);
					break;
			    case AlgorithmType.HITS:
					log.info("Applying HITS strategy...");
					strategy = new HITSStrategy(properties);
					break;
			    case AlgorithmType.CA_PR:
					log.info("Applying community Aware PageRank strategy...");
					strategy = new CommunityAwarePageRankStrategy(properties);
					break;
			    case AlgorithmType.CA_HITS:
					log.info("Applying community Aware HITS strategy...");
					strategy = new CommunityAwareHITSStrategy(properties);
					break;
			    default:
					log.info("Applying default strategy...");
					strategy = new PageRankStrategy(properties);
					break;
		    }
		} catch (ERSException e) {
		    HttpResponse res = new HttpResponse(e.getMessage());
		    res.setStatus(500);
		    return res;
		}
		scontext = new ScoringContext(strategy);
		scontext.executeStrategy();
		scontext.saveResults();
	
		JsonObject jObj = new JsonObject();
		jObj.addProperty("expertsId", strategy.getExpertsId());
		jObj.addProperty("evaluationId", strategy.getEvaluationId());
		jObj.addProperty("visualizationId", -1);
	
		scontext.close();
	
		HttpResponse res = new HttpResponse(jObj.toString());
		res.setStatus(200);
		return res;
    }

    /**
     * 
     * @param datasetId
     * @param expertsId
     * @return
     */
    @GET
    @Path("datasets/{datasetId}/experts/{expertsId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input") })
	@ApiOperation(value = "Get the Expert of the Dataset.",
		notes = "Returns the collection of experts for the specific id. Id is retrieved after applying recommendation algorithm on the dataset.")
    public HttpResponse getExperts(@PathParam("datasetId") String datasetId, @PathParam("expertsId") String expertsId) {
		log.info("expertsId:: " + expertsId);
		String datasetName = getDatasetName(datasetId);
		if (datasetName == null) {
		    try {
		    	throw new ERSException(ExceptionMessages.DATASET_NOT_FOUND);
		    } catch (ERSException e) {
		    	e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		DatabaseHandler dbHandler = new DatabaseHandler();
	
		HttpResponse res = null;
		if (expertsId != null && Long.parseLong(expertsId) != -1) {
		    String experts = dbHandler.getExperts(datasetName, Long.parseLong(expertsId));
		    res = new HttpResponse(experts);
		} else {
		    res = new HttpResponse(ERSMessage.EXPERTS_NOT_FOUND);
		}
	
		res.setStatus(200);
		return res;
    }
    
    /**
     * 
     * @param datasetId
     * @param evaluationId
     * @return
     */
    @GET
    @Path("datasets/{datasetId}/evaluations/{evaluationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input") })
	@ApiOperation(value = "Get the Evaluation Results.",
		notes = "Returns the evaluation metrics computed if requested when applying algorithms. Evaluation Id is retrieved after applying algorithm on the dataset.")
    public HttpResponse getEvaluationResults(@PathParam("datasetId") String datasetId, @PathParam("evaluationId") String evaluationId) {

		String datasetName = getDatasetName(datasetId);
		if (datasetName == null) {
		    try {
		    	throw new ERSException(ExceptionMessages.DATASET_NOT_FOUND);
		    } catch (ERSException e) {
		    	e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		log.info("evaluationId:: " + evaluationId);
		DatabaseHandler dbHandler = new DatabaseHandler();
		String evaluationMeasures = dbHandler.getEvaluationMetrics(datasetName, Long.parseLong(evaluationId));
	
		HttpResponse res = new HttpResponse(evaluationMeasures);
		res.setStatus(200);
		return res;
    }

    /**
     * 
     * @param visId
     * @return
     */
    @GET
    @Path("datasets/{datasetId}/visualizations/{visualizationId}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
	@ApiOperation(value = "Get the Visualization Graph.",
		notes = "Returns the visualization graph to be consumed by the client.")
    public HttpResponse getVisulaizationData(@PathParam("datasetId") String datasetId, @PathParam("visualizationId") String visId) {
		log.info("expertsId:: " + visId);
	
		String datasetName = getDatasetName(datasetId);
		DatabaseHandler dbHandler = new DatabaseHandler();
		String visGraph = dbHandler.getVisGraph(datasetName, Long.parseLong(visId));
	
		String fileContentsString = "data:" + "text/xml" + ";base64," + Base64.encodeBase64String(visGraph.getBytes());
	
		HttpResponse res = new HttpResponse(fileContentsString, 200);
		res.setHeader("content-type", "text/xml");
		res.setStatus(200);
	
		return res;
    }

    /**
     * 
     * @param datasetId
     *            Id of the dataset.
     * @param query
     *            String value for the query
     * @param alpha
     *            floating number to adjust semantics and term analysis weight.
     * @return
     */
    @POST
    @Path("datasets/{datasetId}/algorithms/datamodeling")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input") })
	@ApiOperation(value = "Apply Data Modeling Technique.",
	notes = "Returns the id of the expert collection.")
    public HttpResponse modelExperts(@PathParam("datasetId") String datasetId, @ContentParam String query,
    	@DefaultValue("0.5") @QueryParam("alpha") double alpha) {
    
		Application.algoName = "datamodeling";
	
		String datasetName = getDatasetName(datasetId);
		if (datasetName == null) {
		    try {
		    	throw new ERSException(ExceptionMessages.DATASET_NOT_FOUND);
		    } catch (ERSException e) {
		    	e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		DatabaseHandler dbHandler = new DatabaseHandler();
	
		dbHandler.truncateEvaluationTable(datasetName);
	
		if (query == null || query.length() < 0) {
		    try {
		    	throw new ERSException(ExceptionMessages.QUERY_NOT_VALID);
		    } catch (ERSException e) {
		    	e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		String expertPosts = "";
		QueryAnalyzer qAnalyzer = null;
		try {
		    qAnalyzer = new QueryAnalyzer(query);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	
		ConnectionSource connectionSource = dbHandler.getConnectionSource();
		long queryId = qAnalyzer.getId(datasetName, connectionSource);
	
		TextMapper dbTextIndexer = null;
		SematicsMapper dbSemanticsIndexer = null;
	
		Map<Long, UserEntity> usermap = null;
	
		try {
		    usermap = UserMapSingleton.getInstance().getUserMap(datasetName, connectionSource);
		} catch (SQLException e1) {
		    e1.printStackTrace();
		}
	
		long expertsId = -1;
		long eMeasureId = -1;
	
		try {
	
		    LuceneSearcher searcher = new LuceneSearcher(qAnalyzer.getText(), getIndexDirectory(datasetId));
		    TopDocs docs = searcher.performSearch(qAnalyzer.getText(), Integer.MAX_VALUE);
	
		    dbTextIndexer = new TextMapper(searcher.getTotalNumberOfDocs());
		    dbTextIndexer.buildMaps(docs, qAnalyzer.getText(), getIndexDirectory(datasetId));
	
		    dbSemanticsIndexer = new SematicsMapper(dbHandler.getConnectionSource());
		    TopDocs semanticDocs = searcher.performSemanticSearch();
		    dbSemanticsIndexer.buildIndex(datasetName, semanticDocs, qAnalyzer.getText(), getIndexDirectory(datasetId));
	
		    ScoringContext scontext = new ScoringContext(new DataModelingStrategy(dbTextIndexer, dbSemanticsIndexer, usermap, alpha));
		    scontext.executeStrategy();
		    expertPosts = scontext.getExperts();
	
		    expertsId = dbHandler.addExperts(datasetName, queryId, expertPosts);
	
		    log.info("Evaluating modeling technique");
	
		    EvaluationMeasure eMeasure = new EvaluationMeasure(scontext.getExpertMap(), usermap, "datamodeling");
	
		    // Compute Evaluation Measures.
		    try {
			eMeasure.computeAll();
			eMeasure.save(datasetName, queryId, connectionSource);
			eMeasureId = eMeasure.getId();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
	
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (ParseException e) {
		    e.printStackTrace();
		}
	
		dbHandler.close();
	
		JsonObject jObj = new JsonObject();
		jObj.addProperty("expertsId", expertsId);
		jObj.addProperty("evaluationId", eMeasureId);
	
		HttpResponse res = new HttpResponse(jObj.toString());
		res.setStatus(200);
		return res;
    }


    /**
     * 
     * @param datasetId
     * @param expertCollectionId
     * @param expertId
     * @return
     */
    @GET
    @Path("datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/tags")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input") })
	@ApiOperation(value = "Retrieve the Related Tags for the User.",
		notes = "Returns tags associated with experts for the specific post.")
    public HttpResponse getTags(@PathParam("datasetId") String datasetId, @PathParam("expertsCollectionId") String expertCollectionId,
	    @PathParam("expertId") String expertId) {

		String datasetName = getDatasetName(datasetId);
		if (datasetName == null) {
		    try {
		    	throw new ERSException(ExceptionMessages.DATASET_NOT_FOUND);
		    } catch (ERSException e) {
		    	e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		DatabaseHandler dbHandler = new DatabaseHandler();
		
		TagExtractor extractor = new TagExtractor(datasetName, dbHandler, expertCollectionId, expertId);
	
		HttpResponse res = new HttpResponse(extractor.getTags());
		res.setStatus(200);
		return res;
    }

    /**
     * 
     * @param datasetId
     * @param expertCollectionId
     * @param expertId
     * @return
     */
    @GET
    @Path("datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/posts")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 400, message = "Invalid Input") })
	@ApiOperation(value = "Get the Related Post of the Expert.",
		notes = "Returns the related posts of the expert user.")
    public HttpResponse getPosts(@PathParam("datasetId") String datasetId, @PathParam("expertsCollectionId") String expertCollectionId,
	    @PathParam("expertId") String expertId) {

		String datasetName = getDatasetName(datasetId);
		if (datasetName == null) {
		    try {
		    	throw new ERSException(ExceptionMessages.DATASET_NOT_FOUND);
		    } catch (ERSException e) {
		    	e.printStackTrace();
				HttpResponse res = new HttpResponse(e.getMessage());
				res.setStatus(400);
				return res;
		    }
		}
	
		DatabaseHandler dbHandler = new DatabaseHandler();
		
		RelatedPostsExtractor extractor = new RelatedPostsExtractor(datasetName, dbHandler, expertCollectionId, expertId);
	
		HttpResponse res = new HttpResponse(extractor.getPosts());
		res.setStatus(200);
		return res;
    }


    // TODO:Refactor the path and the method.
    @GET
    @Path("download/{filename}")
    public HttpResponse getGraph(@PathParam("filename") String filename) {

	byte[] data = LocalFileManager.getFile(filename);
	String fileContentsString = null;
	try {
	    fileContentsString = new String(data, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	// log.info(fileContentsString);

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");

	return res;
    }

    /**
     * Method for debugging purposes. Here the concept of restMapping validation
     * is shown. It is important to check, if all annotations are correct and
     * consistent. Otherwise the service will not be accessible by the
     * WebConnector. Best to do it in the unit tests. To avoid being
     * overlooked/ignored the method is implemented here and not in the test
     * section.
     * 
     * @return true, if mapping correct
     */
    public boolean debugMapping() {
	String XML_LOCATION = "./restMapping.xml";
	String xml = getRESTMapping();

	try {
	    RESTMapper.writeFile(XML_LOCATION, xml);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	XMLCheck validator = new XMLCheck();
	ValidationResult result = validator.validate(xml);

	if (result.isValid())
	    return true;
	return false;
    }

    /**
     * This method is needed for every RESTful application in LAS2peer. There is
     * no need to change!
     * 
     * @return the mapping
     */
    public String getRESTMapping() {
	String result = "";
	try {
	    result = RESTMapper.getMethodsAsXML(this.getClass());
	} catch (Exception e) {

	    e.printStackTrace();
	}
	return result;
    }

    @GET
    @Path("datasets/{datasetId}/users/{userId}")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
	@ApiOperation(value = "Get User of the Dataset.",
		notes = "Returns the user details from the given dataset.")
    public HttpResponse getUser(@PathParam("userId") String userId, @PathParam("datasetId") String datasetId) {

	System.out.println("expertsId:: " + userId);

	String datasetName = getDatasetName(datasetId);
	DatabaseHandler dbHandler = new DatabaseHandler();
	String userDetails = dbHandler.getUser(datasetName, Long.parseLong(userId));

	HttpResponse res = new HttpResponse(userDetails);
	res.setStatus(200);
	return res;
    }

    @POST
    @Path("datasets/{datasetId}/position")
    public void saveClickPositions(@PathParam(value = "datasetId") String datasetId,
    	@DefaultValue("-1") @QueryParam("expertsId") String expertsId, 
    	@DefaultValue("-1") @QueryParam("position") int position) {

		String datasetName = getDatasetName(datasetId);
		if (datasetName == null) {
		    // Throw custom exception.
		}
	
		DatabaseHandler dbHandler = new DatabaseHandler();
	
		// log.info("USRNAME:: " + username);
		try {
		    TableUtils.createTableIfNotExists(dbHandler.getConnectionSource(), dbHandler.getEntityConfigOfDataSet(dbHandler.getConnectionSource(), UserClickDetails.class, datasetName) );
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	
		dbHandler.saveClickPositions(expertsId, position);
    }

    @POST
    @Path("datasets/{datasetId}/skillDistribution")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
	@ApiOperation(value = "Get Popular Skill Tags.",
		notes = "Extracts the Most Popular Skill Tags from the Dataset.")
    public HttpResponse createSkillDistribution(@PathParam(value = "datasetId") String datasetId) {

		String datasetName = getDatasetName(datasetId);
		DatabaseHandler dbHandler = new DatabaseHandler();
	
		HttpResponse res;
		dbHandler.createTagDistribution(datasetName);
	
		res = new HttpResponse(ERSMessage.SKILL_DISTRIBUTION_CREATED, 200);
	
		return res;
    }
    
    /**
     * 
     * @param datasetId
     *            An id identifying the dataset. Ids are stored in a database
     *            called ersdb.
     * 
     * @return Returns the database name associated with a particular dataset.
     */
    private String getDatasetName(String datasetId) {
		DatabaseHandler handler = new DatabaseHandler();
		String datasetName = null;
		try {
		    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
		    DataInfoEntity datasetEntity = DatasetInfoDao.queryForId(Long.parseLong(datasetId));
		    if(datasetEntity != null)
		    	datasetName = datasetEntity.getDatasetName();
	
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	
		handler.close();
	
		return datasetName;
    }

    private String getIndexDirectory(String datasetId) {
    DatabaseHandler handler = new DatabaseHandler();
    String filepath = null;
	try {
	    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
	    DataInfoEntity datasetEntity = DatasetInfoDao.queryForId(Long.parseLong(datasetId));
	    filepath = datasetEntity.getIndexFilepath();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	handler.close();

	return filepath;
    }


}
