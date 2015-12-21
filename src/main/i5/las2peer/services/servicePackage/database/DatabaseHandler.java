/**
 * 
 */
package i5.las2peer.services.servicePackage.database;

import i5.las2peer.services.servicePackage.database.entities.DataEntity;
import i5.las2peer.services.servicePackage.database.entities.EvaluationMetricsEntity;
import i5.las2peer.services.servicePackage.database.entities.ExpertEntity;
import i5.las2peer.services.servicePackage.database.entities.GraphEntity;
import i5.las2peer.services.servicePackage.database.entities.QueryEntity;
import i5.las2peer.services.servicePackage.database.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.database.entities.UserAccEntity;
import i5.las2peer.services.servicePackage.database.entities.UserEntity;
import i5.las2peer.services.servicePackage.parsers.IPost;
import i5.las2peer.services.servicePackage.parsers.IUser;
import i5.las2peer.services.servicePackage.textProcessor.StopWordRemover;
import i5.las2peer.services.servicePackage.utils.Application;
import i5.las2peer.services.servicePackage.utils.ERSMessage;
import i5.las2peer.services.servicePackage.utils.semanticTagger.SemanticTagger;
import i5.las2peer.services.servicePackage.utils.statistics.Stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

/**
 * A Database handler to do CRUD operations on the Database. Database is created
 * if not present when fetching connection source.
 * 
 * @author sathvik
 *
 */
public class DatabaseHandler extends MySqlOpenHelper {
    private Log log = LogFactory.getLog(DatabaseHandler.class);

    public DatabaseHandler() {
	super();
    }

    /**
     * Method to add the post containing user activity details into database
     * table. This method is called after all the posts have been collected into
     * a list by a parser such as XML or CSV parser.
     * 
     * @param posts
     *            A list of posts and the details of the posts to be inserted.
     * @throws SQLException
     *             If database is not open to insert posts or does not comply to
     *             table constraints.
     * 
     * */
    public void addPosts(String datasetName, List<? extends IPost> posts) throws SQLException {

	ConnectionSource source = super.getConnectionSource();
	Dao<DataEntity, Long> DataDao = DaoManager.createDao(source, getEntityConfigOfDataSet(source, DataEntity.class, datasetName) );

	DataEntity data = null;
	StopWordRemover remover = null;

	for (IPost res : posts) {

	    data = new DataEntity();
	    if (res.getPostId() != null) {
		Long postid = Long.parseLong(res.getPostId());

		data.setPostId(postid);
	    }

	    if (res.getCreationDate() != null) {
		data.setCreationDate(res.getCreationDate());
	    }

	    if (res.getOwnerUserId() != null) {
		long id = 0;
		try {
		    id = Long.parseLong(res.getOwnerUserId());
		} catch (NumberFormatException e) {
		    // This can be improved. Generate unique number from string.
		    id = res.getOwnerUserId().hashCode();
		}

		data.setOwnerUserId(id);
	    }

	    if (res.getParentId() != null) {
		try {
		    data.setParentId(Long.parseLong(res.getParentId()));
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
	    }

	    if (res.getPostId() != null) {
		data.setPostId(Long.parseLong(res.getPostId()));
	    }

	    if (res.getPostTypeId() != null) {
		data.setPostTypeId(Integer.parseInt(res.getPostTypeId()));
	    } else {
		// If parent Id is not present, fallback, check if title is a
		// reply by matching "Re:"
		String text = res.getTitle();
		if (text != null && text.startsWith("Re:")) {
		    data.setPostTypeId(2);
		} else {
		    data.setPostTypeId(1);
		}

	    }

	    if (res.getScore() != null) {
		data.setScore(Long.parseLong(res.getScore()));
	    }

	    if (res.getTitle() != null) {
		data.setTitle(res.getTitle());
	    }

	    if (res.getBody() != null) {
		remover = new StopWordRemover(res.getBody());

		data.setBody(res.getBody());
		data.setCleanText(remover.getPlainText());
	    }

	    DataDao.createIfNotExists(data);
	    // log.info("Created Data entity.");

	}
    }

    /**
     * Method to add user details into "user" table in the database. This method
     * is called after collecting user information is collected into as list by
     * a parser such as XML or CSV parser.
     * 
     * @param users
     *            A list of users and their information to be inserted.
     * @throws SQLException
     *             If database is not open to insert users or does not comply to
     *             table constraints.
     * */

    public void addUsers(String datasetName, List<? extends IUser> users) throws SQLException {
	ConnectionSource source = super.getConnectionSource();
	UserEntity entity = null;
	Dao<UserEntity, Long> UserDao = DaoManager.createDao(source, getEntityConfigOfDataSet(source, UserEntity.class, datasetName) );

	// Iterate tags and create DAO objects.
	for (IUser user : users) {
	    entity = new UserEntity();
	    try {
		if (user.getReputation() != null) {
		    entity.setReputation(Long.parseLong(user.getReputation()));
		}

		if (user.getUserAccId() != null) {
		    entity.setUserAccId(Long.parseLong(user.getUserAccId()));
		}

		if (user.getUserId() != null) {
		    entity.setUserId(Long.parseLong(user.getUserId()));
		}
		if (user.getCreationDate() != null) {
		    entity.setCreationDate(user.getCreationDate());
		}
		if (user.getAbtMe() != null) {
		    entity.setAbtMe(user.getAbtMe());
		}
		if (user.getLocation() != null) {
		    entity.setLocation(user.getLocation());
		}

		if (user.getUserName() != null && user.getUserName().length() > 0) {
		    entity.setUserName(user.getUserName());
		} else {
		    entity.setUserName("anonymous");
		}

		if (user.getWebsiteUrl() != null) {
		    entity.setWebsiteUrl(user.getWebsiteUrl());
		}
	    } catch (NumberFormatException e) {
		log.info("Exception " + e);
	    }

	    // log.info("Creatting user entity...");
	    UserDao.createIfNotExists(entity);
	}
    }

    /**
     * Method to add semantic information about the post into "semantics" table
     * in the database. This method is called to add semantic information for
     * each post in the database. All the posts are iterated and a web service
     * is used to retrieve semantic data.
     * 
     * @see SemanticTagger#getSemanticData()
     * 
     * */

    public void addSemanticTags(String datasetName) {
	try {
	    ConnectionSource source = super.getConnectionSource();
	    Dao<SemanticTagEntity, Long> SemanticDao = DaoManager.createDao( source, getEntityConfigOfDataSet(source, SemanticTagEntity.class, datasetName) );

	    // Iterate all the posts and extract tags from them.
	    Dao<DataEntity, Long> postsDao = DaoManager.createDao(source, getEntityConfigOfDataSet(source, DataEntity.class, datasetName) );
	    List<DataEntity> data_entites = postsDao.queryForAll();

	    SemanticTagEntity tagEntity = null;
	    SemanticTagger tagger = null;
	    for (DataEntity entity : data_entites) {
			SemanticTagEntity tEntity = SemanticDao.queryForId(entity.getPostId());
			// If particular Id is not present in the semantic table then
			// proceed with extraction of tag
			if (tEntity == null) {
	
			    tagger = new SemanticTagger(entity.getBody());
			    if (tagger != null && tagger.getSemanticData() != null) {
				String tags = tagger.getSemanticData().getTags();
				String annotations = tagger.getSemanticData().getAnnotation();
	
				tagEntity = new SemanticTagEntity();
				tagEntity.setPostId(entity.getPostId());
				tagEntity.setAnnotations(annotations);
				tagEntity.setTags(tags);
	
				SemanticDao.createIfNotExists(tagEntity);
			    }
			}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();

	}
    }

    /**
     * Queries for all the user rows and creates a DAO object by ORMLite The DAO
     * object is used across the application.
     * 
     * @return List of UserEntity Objects containing user properties.
     * @see UserEntity
     * 
     * */

    public List<UserEntity> getUserDAOs(String datasetName) throws SQLException {
	Dao<UserEntity, Long> userDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), UserEntity.class, datasetName) );
	return userDao.queryForAll();
    }

    /**
     * A helper method used to mark the probable expert in the database. It will
     * be used during evaluation and this marks the ground truth value.
     * 
     * This method iterates all the users and retrieves reputation value for the
     * user. If corresponding reputation is above 98 percentile, it is marked as
     * probable expert.
     * 
     * */

    public void markExpertsForEvaluation(String datasetName, ConnectionSource connectionSrc) throws SQLException {
	List<Long> reputations = new ArrayList<Long>();

	Dao<UserEntity, Long> userDao = DaoManager.createDao(connectionSrc, getEntityConfigOfDataSet(connectionSrc, UserEntity.class, datasetName) );
	List<UserEntity> user_entites = userDao.queryForAll();
	for (UserEntity entity : user_entites) {
	    long reputation = entity.getReputation();
	    reputations.add(reputation);
	}

	Stats stats = new Stats(reputations);

	UpdateBuilder updateBuilder = userDao.updateBuilder();
	for (UserEntity entity : user_entites) {
	    long reputation = entity.getReputation();
	    updateBuilder.where().eq("userId", entity.getUserId());
	    System.out.println(reputation + ":::" + stats.getPercentileAbove(99));
	    // if (reputation >= stats.getPercentileAbove(99.7)) {
	    if (reputation >= 3000) {
		updateBuilder.updateColumnValue("probable_expert", true);
	    } else {
		updateBuilder.updateColumnValue("probable_expert", false);
	    }
	    updateBuilder.update();
	}

    }

    /**
     * 
     * @param evaluationId
     * @return
     */
    public String getEvaluationMetrics(String datasetName, long evaluationId) {
	Dao<EvaluationMetricsEntity, Long> evaluationDao = null;
	try {
	    evaluationDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), EvaluationMetricsEntity.class, datasetName) );
	    EvaluationMetricsEntity entity = evaluationDao.queryForId(evaluationId);
	    if(entity == null) return ERSMessage.EVALUATION_NOT_FOUND;
	    return entity.getMetrics();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * 
     * @param datasetName 
     * @param queryId
     * @return
     */
    public String getVisGraph(String datasetName, long queryId) {
	Dao<GraphEntity, Long> visulaizationDao = null;
	try {
	    visulaizationDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), GraphEntity.class, datasetName) );
	    GraphEntity entity = visulaizationDao.queryForId(queryId);

	    return entity.getGraph();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * 
     * @param datasetName 
     * @param queryId
     *            Id to identify the expert list.
     * @param experts
     *            A list of experts and their details stored as json string.
     * @return
     */
    public long addExperts(String datasetName, long queryId, String experts) {
	try {
	    Dao<ExpertEntity, Long> ExpertDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), ExpertEntity.class, datasetName) );
	    ExpertEntity entity = new ExpertEntity();
	    entity.setQueryId(queryId);
	    entity.setExperts(experts);
	    entity.setDate(new Date());
	    ExpertDao.createIfNotExists(entity);

	    return entity.getId();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return -1;
    }

    /**
     * 
     * @param expertsId
     * @return
     */
    public String getExperts(String datasetName, long expertsId) {
	Dao<ExpertEntity, Long> expertsDao = null;
	try {
	    expertsDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), ExpertEntity.class, datasetName) );
	    ExpertEntity entity = expertsDao.queryForId(expertsId);
	    if(entity == null) return ERSMessage.EXPERTS_NOT_FOUND;
	    return entity.getExperts();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public void truncateEvaluationTable(String datasetName) {
	Dao<EvaluationMetricsEntity, Long> evaluationDao = null;
	try {
	    evaluationDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), EvaluationMetricsEntity.class, datasetName) );
	    evaluationDao.delete(evaluationDao.queryForAll());
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    public void saveReputationValues(String datasetName) {
	ConnectionSource source = super.getConnectionSource();
	try {
	    Dao<UserEntity, Long> UserDao = DaoManager.createDao(source, getEntityConfigOfDataSet(source, UserEntity.class, datasetName) );
	    List<UserEntity> userentities = UserDao.queryForAll();
	    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("reputations.txt", false)))) {
		for (UserEntity entity : userentities) {
		    double rep = entity.getReputation();
		    out.println((int) rep);
		}
	    } catch (IOException e) {

	    }

	} catch (SQLException e) {

	    e.printStackTrace();
	}

    }

    public void saveNoOfRepliesByUser(String datasetName) {
	ConnectionSource source = super.getConnectionSource();

	try {
	    Dao<UserEntity, Long> UserDao = DaoManager.createDao(source, getEntityConfigOfDataSet(source, UserEntity.class, datasetName) );
	    Dao<DataEntity, Long> DataDao = DaoManager.createDao(source,getEntityConfigOfDataSet(source, DataEntity.class, datasetName) );

	    QueryBuilder<DataEntity, Long> queryBuilder = DataDao.queryBuilder();

	    List<UserEntity> userentities = UserDao.queryForAll();
	    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("reputations.txt", false)))) {
		for (UserEntity entity : userentities) {
		    double userId = entity.getUserId();
		    queryBuilder.where().eq("owner_user_id", userId);
		    PreparedQuery<DataEntity> preparedQuery = queryBuilder.prepare();
		    int size = DataDao.query(preparedQuery).size();
		    out.println(userId + "=" + size);
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	} catch (SQLException e) {

	    e.printStackTrace();
	}

    }

    public void markPostType(String datasetName) {
	ConnectionSource source = super.getConnectionSource();
	try {
	    Dao<DataEntity, Long> DataDao = DaoManager.createDao(source, getEntityConfigOfDataSet(source, DataEntity.class, datasetName) );

	    List<DataEntity> dataEntities = DataDao.queryForAll();
	    UpdateBuilder<DataEntity, Long> updateBuilder = DataDao.updateBuilder();

	    for (DataEntity entity : dataEntities) {
		String text = entity.getTitle();
		updateBuilder.where().eq("post_id", entity.getPostId());
		if (text != null && text.startsWith("Re:")) {
		    updateBuilder.updateColumnValue("post_type_id", 2);
		} else {
		    updateBuilder.updateColumnValue("post_type_id", 1);
		}
		updateBuilder.update();
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    public String getSemanticTags(String datasetName, String postId) {

	String tags = null;
	ConnectionSource source = super.getConnectionSource();
	try {
		
	    Dao<SemanticTagEntity, Long> SemanticDao = DaoManager.createDao( source, getEntityConfigOfDataSet(source, SemanticTagEntity.class, datasetName) );
	    QueryBuilder<SemanticTagEntity, Long> qb = SemanticDao.queryBuilder();
	    qb.where().eq("post_id", postId);

	    List<SemanticTagEntity> rows = qb.query();

	    if (rows != null && rows.size() > 0) {
		SemanticTagEntity entity = rows.get(0);
		tags = entity.getTags();
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return tags;
    }

    public void markExpertsForEvaluationFromReplies(String datasetName, ConnectionSource connectionSrc) throws SQLException {

	HashMap<Long, Long> userId2NoReplies = new HashMap();
	try (BufferedReader br = new BufferedReader(new FileReader("reputations.txt"))) {
	    for (String line; (line = br.readLine()) != null;) {
		String[] splits = line.split("=");
		String userId = splits[0];
		String noOfReplies = splits[1];

		userId2NoReplies.put(Long.parseLong(userId), Long.parseLong(noOfReplies));
	    }

	} catch (NumberFormatException | IOException e) {
	    e.printStackTrace();
	}

	Dao<UserEntity, Long> userDao = DaoManager.createDao(connectionSrc, getEntityConfigOfDataSet(connectionSrc, UserEntity.class, datasetName) );
	List<UserEntity> user_entites = userDao.queryForAll();

	UpdateBuilder updateBuilder = userDao.updateBuilder();
	for (UserEntity entity : user_entites) {

	    long userId = entity.getUserId();
	    long noOfReplies = userId2NoReplies.get(userId);

	    updateBuilder.where().eq("userId", userId);
	    if (noOfReplies > 200) {
		updateBuilder.updateColumnValue("probable_expert", true);
	    } else {
		updateBuilder.updateColumnValue("probable_expert", false);
	    }
	    updateBuilder.update();
	}

	// UpdateBuilder updateBuilder = userDao.updateBuilder();
	// for (UserEntity entity : user_entites) {
	// long userId = entity.getUserId();
	// long noOfReplies = userId2NoReplies.get(userId);
	// updateBuilder.where().eq("userId", userId);
	// updateBuilder.updateColumnValue("reputation", noOfReplies);
	// updateBuilder.update();
	// }

    }

    public String getPost(String datasetName, long postId) {
		DataEntity entity = null;
		try {
		    Dao<DataEntity, Long> dataDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), DataEntity.class, datasetName));
		    entity = dataDao.queryForId(postId);
		} catch (SQLException e) {
		    e.printStackTrace();
		    return "";
		}
	
		return entity.getBody();

    }

    public void addUser(String username) {
	UserAccEntity entity = null;
	try {
	    Dao<UserAccEntity, Long> AccDao = DaoManager.createDao(super.getConnectionSource(), UserAccEntity.class);
	    entity = new UserAccEntity();
	    entity.setUserName(username);
	    entity.setDate(new Date());

	    // AccDao.createIfNotExists(entity);
	    AccDao.create(entity);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

    }

    public void createTagDistribution(String datasetName) {
	HashMap<String, Integer> c2count = new HashMap<String, Integer>();
	ConnectionSource source = super.getConnectionSource();
	try {
	    Dao<SemanticTagEntity, Long> semanticrDao = DaoManager.createDao(source, getEntityConfigOfDataSet(super.getConnectionSource(), SemanticTagEntity.class, datasetName) );
	    List<SemanticTagEntity> semanticentities = semanticrDao.queryForAll();

	    for (SemanticTagEntity entity : semanticentities) {
		String tags = entity.getTags();
		String tagAr[] = tags.split(",");
		for (String tag : tagAr) {
		    if (c2count.containsKey(tag)) {
			c2count.put(tag, c2count.get(tag) + 1);
		    } else {
			c2count.put(tag, 1);
		    }

		}

	    }

	    LinkedHashMap<String, Integer> c2countSort = Application.sortByValue(c2count);
	    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("tagDistribution.txt", false)))) {

		Iterator it = c2countSort.entrySet().iterator();
		while (it.hasNext()) {
		    Entry thisEntry = (Entry) it.next();
		    Object key = thisEntry.getKey();
		    Object value = thisEntry.getValue();

		    out.println(key + "=" + value);
		}
	    } catch (IOException e) {

	    }

	} catch (SQLException e) {

	    e.printStackTrace();
	}

    }

    /**
     * 
     * @param userId
     * @return
     */
    public String getUser(String datasetName, long userId) {
	Dao<UserEntity, Long> userDao = null;
	try {
	    userDao = DaoManager.createDao(super.getConnectionSource(), getEntityConfigOfDataSet(super.getConnectionSource(), UserEntity.class, datasetName) );
	    UserEntity entity = userDao.queryForId(userId);

	    Gson gson = new Gson();

	    return gson.toJson(entity);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public void saveClickPositions(String expertsId, int position) {
//	 UserAccEntity entity = null;
//	 try {
//	 Dao<UserAccEntity, Long> AccDao =
//	 DaoManager.createDao(super.getConnectionSource(),
//	 UserAccEntity.class );
//	 entity = new UserAccEntity();
//	 entity.setUserName(username);
//	 entity.setDate(new Date());
//	
//	 // AccDao.createIfNotExists(entity);
//	 AccDao.create(entity);
//	 } catch (SQLException e) {
//	 System.out.println("Error in getting post..." + e);
//	 e.printStackTrace();
//	 }

    }

    public <T> DatabaseTableConfig<T> getEntityConfigOfDataSet(ConnectionSource connectionSrc, Class<T> entityClass, String datasetName){
	    // Create the table config, with a custom table name
    	DatabaseTableConfig<T> entityConfig = null;
		try {
			if(entityClass == DataEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
			else if(entityClass == UserEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
			else if(entityClass == SemanticTagEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
			else if(entityClass == QueryEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
			else if(entityClass == EvaluationMetricsEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
			else if(entityClass == GraphEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
			else if(entityClass == ExpertEntity.class){
				entityConfig = DatabaseTableConfig.fromClass(connectionSrc, entityClass);
				entityConfig.setTableName(datasetName + "_" + DatabaseTableConfig.extractTableName(entityClass));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entityConfig;

    }
    
    
    public void close() {
	try {
	    super.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

}
