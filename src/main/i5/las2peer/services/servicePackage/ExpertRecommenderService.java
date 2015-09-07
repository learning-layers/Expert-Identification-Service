package i5.las2peer.services.servicePackage;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.ResourceListApi;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
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
import i5.las2peer.services.servicePackage.parsers.ERSXmlParser;
import i5.las2peer.services.servicePackage.parsers.User;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

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
@Version("0.1")
@ApiInfo(title = "Expert Recommender Service", description = "A RESTful expert recommender service", termsOfServiceUrl = "sample-tos.io", contact = "sathvik.parekodi@rwth-aachen.de", license = "Apache License 2", licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0")
public class ExpertRecommenderService extends Service {

    private Log log = LogFactory.getLog(ExpertRecommenderService.class);

    public ExpertRecommenderService() {
	// read and set properties values
	// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED
	// TO BE CHANGED TOO!
	setFieldValues();

    }


    /**
     * 
     * @return
     */
    @GET
    @Path("datasets")

    @ResourceListApi(description = "Get available dataset on the server")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the available datasets on the server.")
    public HttpResponse getAvailableDatasets() {

	DatabaseHandler handler = new DatabaseHandler("ersdb", "root", "");
	JsonArray datasetsObj = null;
	try {
	    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);
	    List<DataInfoEntity> datasets = DatasetInfoDao.queryForAll();

	    datasetsObj = new JsonArray();
	    for (DataInfoEntity entity : datasets) {
		String name = entity.getDatasetName();
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
	    res.setStatus(200);
	    return res;
	}

	HttpResponse res = new HttpResponse(datasetsObj.toString());
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
    @ResourceListApi(description = "Get the experts from the dataset with respect to an expert id.")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the collection of experts for the specific id. Id is retrieved after applying recommendetaion algorithm on the dataset")
    public HttpResponse getExperts(@PathParam("datasetId") String datasetId, @PathParam("expertsId") String expertsId) {
	log.info("expertsId:: " + expertsId);
	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	HttpResponse res = null;
	if (expertsId != null && Long.parseLong(expertsId) != -1) {
	    String experts = dbHandler.getExperts(Long.parseLong(expertsId));
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
    @ResourceListApi(description = "Get the evaluation results.")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the evaluation metrics computed if requested when applying algorithms. Evaluation Id is retrieved after applying algorithm on the dataset.")
    public HttpResponse getEvaluationResults(@PathParam("datasetId") String datasetId, @PathParam("evaluationId") String evaluationId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	log.info("evaluationId:: " + evaluationId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");
	String evaluationMeasures = dbHandler.getEvaluationMetrics(Long.parseLong(evaluationId));

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
    @ResourceListApi(description = "Get the visualization graph")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the visualization graph to be consumed by the client.")
    public HttpResponse getVisulaizationData(@PathParam("visualizationId") String visId) {

	log.info("expertsId:: " + visId);
	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler("healthcare", "root", "");
	String visGraph = dbHandler.getVisGraph(Long.parseLong(visId));

	String fileContentsString = "data:" + "text/xml" + ";base64," + Base64.encodeBase64String(visGraph.getBytes());

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");
	res.setStatus(200);

	return res;
    }

    /**
     * 
     * @param datasetId
     * @param query
     * @param dateBefore
     * @param alpha
     * @return
     */
    @POST
    @Path("datasets/{datasetId}/algorithms/datamodeling")
    @ResourceListApi(description = "Get the visualization graph")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the id of the expert collection.")
    public HttpResponse modelExperts(@PathParam("datasetId") String datasetId, @ContentParam String query,
	    @QueryParam(name = "dateBefore", defaultValue = "2025-12-31") String dateBefore,
	    @QueryParam(name = "alpha", defaultValue = "0.5") double alpha) {

	Application.algoName = "datamodeling";

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = new DatabaseHandler(databaseName, "root", "");

	dbHandler.truncateEvaluationTable();

	if (query == null || query.length() < 0) {
	    try {
		throw new ERSException(ExceptionMessages.QUERY_NOT_VALID);
	    } catch (ERSException e) {
		e.printStackTrace();
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
	long queryId = qAnalyzer.getId(connectionSource);

	TextMapper dbTextIndexer = null;
	SematicsMapper dbSemanticsIndexer = null;

	Map<Long, UserEntity> usermap = null;

	try {
	    usermap = UserMapSingleton.getInstance().getUserMap(connectionSource);
	} catch (SQLException e1) {
	    e1.printStackTrace();
	}

	long expertsId = -1;
	long eMeasureId = -1;

	try {

	    LuceneSearcher searcher = new LuceneSearcher(qAnalyzer.getText(), databaseName + "_index");
	    TopDocs docs = searcher.performSearch(qAnalyzer.getText(), Integer.MAX_VALUE);

	    dbTextIndexer = new TextMapper(searcher.getTotalNumberOfDocs());
	    dbTextIndexer.buildMaps(docs, qAnalyzer.getText(), databaseName + "_index");

	    dbSemanticsIndexer = new SematicsMapper(dbHandler.getConnectionSource());
	    TopDocs semanticDocs = searcher.performSemanticSearch();
	    dbSemanticsIndexer.buildIndex(semanticDocs, qAnalyzer.getText(), databaseName + "_index");

	    ScoringContext scontext = new ScoringContext(new DataModelingStrategy(dbTextIndexer, dbSemanticsIndexer, usermap, alpha));
	    scontext.executeStrategy();
	    expertPosts = scontext.getExperts();

	    expertsId = dbHandler.addExperts(queryId, expertPosts);

	    log.info("Evaluating modeling technique");

	    EvaluationMeasure eMeasure = new EvaluationMeasure(scontext.getExpertMap(), usermap, "datamodeling");

	    // Compute Evaluation Measures.
	    try {
		eMeasure.computeAll();
		eMeasure.save(queryId, connectionSource);
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
    @ResourceListApi(description = "Executes the requested algorithm on the dataset.")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the id of the expert collection, id of evaluation metrics and id of the visualization")
    public HttpResponse applyAlgorithm(@PathParam("datasetId") String datasetId, @PathParam("algorithmName") String algorithmName,
	    @ContentParam String query, @QueryParam(name = "evaluation", defaultValue = "false") boolean isEvaluation,
	    @QueryParam(name = "visualization", defaultValue = "true") boolean isVisualization,
	    @QueryParam(name = "alpha", defaultValue = "0.15d") String alpha, @QueryParam(name = "intra", defaultValue = "0.6") String intraWeight) {

	ERSBundle properties = new ERSBundle.Builder(datasetId, query, algorithmName).alpha(alpha).intraWeight(intraWeight).isEvaluation(false)
		.isVisualization(true).build();

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
	    res.setStatus(200);
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
     * @param expertCollectionId
     * @param expertId
     * @return
     */
    @GET
    @Path("datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/tags")
    @ResourceListApi(description = "Retrieves the related tags for the user")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"), @ApiResponse(code = 401, message = "Unauthorized") })
    @Summary("Returns tags associated with experts for the specific post.")
    public HttpResponse getTags(@PathParam("datasetId") String datasetId, @PathParam("expertsCollectionId") String expertCollectionId,
	    @PathParam("expertId") String expertId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	TagExtractor extractor = new TagExtractor(dbHandler, expertCollectionId, expertId);

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
    @ResourceListApi(description = "Returns the related post of the expert.")

    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Returns the related posts of the expert user.")
    public HttpResponse getPosts(@PathParam("datasetId") String datasetId, @PathParam("expertsCollectionId") String expertCollectionId,
	    @PathParam("expertId") String expertId) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    try {
		throw new ERSException(ExceptionMessages.DATABASE_NOT_FOUND);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");
	RelatedPostsExtractor extractor = new RelatedPostsExtractor(dbHandler, expertCollectionId, expertId);

	HttpResponse res = new HttpResponse(extractor.getPosts());
	res.setStatus(200);
	return res;
    }

    /**
     * 
     * @param datasetId
     * @return
     */
    @POST
    @Path("datasets/{datasetId}/semantics")
    @ResourceListApi(description = "Adds semantics to the posts in the dataset.")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") })
    @Summary("Adds semantic tags to the text.")
    public HttpResponse addSemantics(@PathParam(value = "datasetId") String datasetId) {

	String dbName = getDatabaseName(datasetId);
	DatabaseHandler dbHandler = new DatabaseHandler(dbName, "root", "");

	HttpResponse res;
	try {
	    dbHandler.addSemanticTags();
	} catch (Exception e) {
	    res = new HttpResponse(ERSMessage.ADD_SEMANTICS_FAILURE, 200);
	    return res;
	}

	res = new HttpResponse(ERSMessage.ADD_SEMANTICS_SUCCESS, 200);
	return res;
    }

    /**
     * 
     * @param databaseName
     *            Name of the database to store corresponding to dataset.
     * @param displayName
     *            Display Name of the dataset. Useful for the web client.
     * @return
     */
    @POST
    @ResourceListApi(description = "Creates all the required database tables.")
    @Path("datasets/{databaseName}/prepare")
    public HttpResponse prepareDataset(@PathParam("databaseName") String databaseName, @ContentParam String displayName) {

	if (databaseName == null || StringUtils.isAlphanumeric(databaseName) == false) {
	    try {
		throw new ERSException(ERSMessage.DATASET_NAME_INVALID);
	    } catch (ERSException e) {
		e.printStackTrace();
		HttpResponse res = new HttpResponse(e.getMessage());
		res.setStatus(200);
	    }
	}

	// TODO: If dataset is not present in the path throw an exception.
	DatabaseHandler handler = new DatabaseHandler("ersdb", "root", "");
	DataInfoEntity entity = new DataInfoEntity();
	try {
	    Dao<DataInfoEntity, Long> DatasetInfoDao = DaoManager.createDao(handler.getConnectionSource(), DataInfoEntity.class);

	    QueryBuilder<DataInfoEntity, Long> qb = DatasetInfoDao.queryBuilder();
	    qb.where().eq("database_name", databaseName);
	    List<DataInfoEntity> entities = qb.query();

	    // If entry is present, return the id. Else create and insert the
	    // entity.
	    if (entities != null && entities.size() > 0) {
		entity = entities.get(0);
	    } else {
		entity.setDatabase(databaseName);
		if (displayName == null)
		    displayName = databaseName;

		entity.setDataset(displayName);

		Calendar cal = Calendar.getInstance();
		entity.setDate(cal.getTime());

		entity.setFilepath(databaseName);
		entity.setIndexFilepath(databaseName + "_index");
		DatasetInfoDao.create(entity);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	// Create database and necessary tables in the database.
	DatabaseHandler dbHandler = new DatabaseHandler(databaseName, "root", "");
	ConnectionSource connectionSrc;
	try {
	    connectionSrc = dbHandler.getConnectionSource();
	    // Create necessary tables.
	    TableUtils.createTableIfNotExists(connectionSrc, DataEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, UserEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, SemanticTagEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, QueryEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, EvaluationMetricsEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, GraphEntity.class);
	    TableUtils.createTableIfNotExists(connectionSrc, ExpertEntity.class);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	HttpResponse res = new HttpResponse(String.valueOf(entity.getId()));
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
	log.info(fileContentsString);

	HttpResponse res = new HttpResponse(fileContentsString, 200);
	res.setHeader("content-type", "text/xml");

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
    @ResourceListApi(description = "Parse the data files and add it to database.")
    @Path("datasets/{datasetId}/parse")
    public HttpResponse parse(@PathParam("datasetId") String id, @ContentParam String urlObject,
	    @QueryParam(name = "format", defaultValue = "xml") String type) {

	log.info("URL::" + urlObject);
	HttpResponse res = null;

	String databaseName = getDatabaseName(id);
	if (databaseName == null) {
	    try {
		throw new ERSException(ERSMessage.DATASET_NOT_CONFIGURED);
	    } catch (ERSException e) {
		e.printStackTrace();
	    }
	}

	String path = null;
	if (!TextUtils.isEmpty(urlObject)) {
	    path = urlObject;
	} else {
	    path = "datasets/" + databaseName;
	}

	DatabaseHandler dbHandler = new DatabaseHandler(databaseName, "root", "");

	try {
	    if (type.equalsIgnoreCase("xml")) {
		log.info("Executing XML Parser...");
		ERSXmlParser xmlparser = new ERSXmlParser(path);
		dbHandler.addPosts(xmlparser.getPosts());
		dbHandler.addUsers(xmlparser.getUsers());
	    } else if (type.equalsIgnoreCase("csv")) {
		log.info("Executing CSV Parser...");
		ERSCSVParser csvparser = new ERSCSVParser(path);
		dbHandler.addPosts(csvparser.getPosts());
		List<UserCSV> users = csvparser.getUsers();

		if (users != null && users.size() > 0) {
		    dbHandler.addUsers(users);
		}
	    } else if (type.equalsIgnoreCase("json")) {
		log.info("Executing Json Parser...");
		ERSJsonParser jsonparser = new ERSJsonParser(path);
		dbHandler.addPosts(jsonparser.getPosts());
		List<User> users = jsonparser.getUsers();
		if (users != null && users.size() > 0) {
		    dbHandler.addUsers(users);
		}
	    } else {
		throw new ERSException(ERSMessage.UNSUPPORTED_TYPE);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	    res = new HttpResponse(ERSMessage.SQL_FAILURE);
	    res.setStatus(200);
	    return res;
	} catch (ERSException e) {
	    e.printStackTrace();
	    res = new HttpResponse(ERSMessage.UNSUPPORTED_TYPE);
	    res.setStatus(200);
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
    @ResourceListApi(description = "Index the data in the dataset.")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Indexing success") })
    @Summary("Indexes the text stored in the database.")
    public HttpResponse index(@PathParam("datasetId") String id) {

	String databaseName = getDatabaseName(id);
	String indexDir = getIndexDirectory(id);

	DatabaseHandler dbHandler = new DatabaseHandler(databaseName, "root", "");
	HttpResponse res = null;
	try {
	    LuceneMysqlIndexer indexer = new LuceneMysqlIndexer(dbHandler.getConnectionSource(), indexDir);
	    indexer.buildIndex();
	    res = new HttpResponse(ERSMessage.INDEX_SUCCESS);
	} catch (SQLException e) {
	    e.printStackTrace();
	    res = new HttpResponse(ERSMessage.SQL_FAILURE);
	} catch (Exception e) {
	    e.printStackTrace();
	    res = new HttpResponse(ERSMessage.INDEX_FAILURE);
	}

	res.setStatus(200);
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

    @POST
    @Path("datasets/{datasetId}/position")
    public void saveClickPositions(@PathParam(value = "datasetId") String datasetId,
	    @QueryParam(name = "expertsId", defaultValue = "-1") String expertsId, @QueryParam(name = "position", defaultValue = "-1") int position) {

	String databaseName = getDatabaseName(datasetId);
	if (databaseName == null) {
	    // Throw custom exception.
	}

	DatabaseHandler dbHandler = null;
	dbHandler = new DatabaseHandler(databaseName, "root", "");

	// log.info("USRNAME:: " + username);
	try {
	    TableUtils.createTableIfNotExists(dbHandler.getConnectionSource(), UserClickDetails.class);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	dbHandler.saveClickPositions(expertsId, position);
    }

    @POST
    @Path("datasets/{datasetId}/skillDistribution")
    @ResourceListApi(description = "Extracts the most popular skill tags from the dataset.")
    public HttpResponse createSkillDistribution(@PathParam(value = "datasetId") String datasetId) {

	String dbName = getDatabaseName(datasetId);
	DatabaseHandler dbHandler = new DatabaseHandler(dbName, "root", "");

	HttpResponse res;
	dbHandler.createTagDistribution();

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

    private String getIndexDirectory(String datasetId) {
	DatabaseHandler handler = new DatabaseHandler("ersdb", "root", "");
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

    @POST
    @Path("datasets")
    public HttpResponse uploadDataset() {
	// TODO: Allow users to upload datasets.
	return null;
    }

    // ////////////////////////////////////////////////////////////////
    // /////// SWAGGER
    // ////////////////////////////////////////////////////////////////

    @GET
    @Path("api-docs")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getSwaggerResourceListing() {
	return RESTMapper.getSwaggerResourceListing(this.getClass());
    }

    @GET
    @Path("api-docs/{tlr}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getSwaggerApiDeclaration(@PathParam("tlr") String tlr) {
	// return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr,
	// "http://127.0.0.1:8080/ocd/");
	return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr, "https://api.learning-layers.eu/ers/");
    }

}
