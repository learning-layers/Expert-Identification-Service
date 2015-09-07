/**
 * 
 */
package i5.las2peer.services.servicePackage.lucene.indexer;

import i5.las2peer.services.servicePackage.database.entities.DataEntity;
import i5.las2peer.services.servicePackage.database.entities.SemanticTagEntity;
import i5.las2peer.services.servicePackage.textProcessor.StopWordRemover;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author sathvik
 *
 */
public class LuceneMysqlIndexer {
    private ConnectionSource connSrc;
    private IndexWriter indexWriter;
    private String indexDirectoryPath;
    private static String dataIndexBasePath = "luceneIndex/%s/data/";
    private static String semanticsIndexBasePath = "luceneIndex/%s/semantics/";

    private Log log = LogFactory.getLog(LuceneMysqlIndexer.class);

    /**
     * 
     * @param connectionSrc
     *            A database connection, used to retrieve fields to index
     * @param indexDirectoryPath
     *            A local path to store the index
     * @throws IOException
     *             Exception is thrown if files cannot be created or opened.
     */
    public LuceneMysqlIndexer(ConnectionSource connectionSrc, String indexDirectoryPath) throws IOException {
	connSrc = connectionSrc;
	this.indexDirectoryPath = indexDirectoryPath;
    }

    /**
     * 
     * @throws SQLException
     * @throws IOException
     */
    public void buildIndex() throws SQLException, IOException {
	Directory dataIndexDir = FSDirectory.open(new File(String.format(dataIndexBasePath, indexDirectoryPath)).toPath());
	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	indexWriter = new IndexWriter(dataIndexDir, config);

	// If index already exists. Delete all.
	if (indexWriter != null) {
	    indexWriter.deleteAll();
	}

	Document dataDoc;

	Dao<DataEntity, Long> postsDao = DaoManager.createDao(connSrc, DataEntity.class);

	List<DataEntity> data_entites = postsDao.queryForAll();
	// Application.totalNoOfResources = data_entites.size();
	StringBuffer fullSearchableText;

	for (DataEntity entity : data_entites) {
	    dataDoc = new Document();

	    long postId = entity.getPostId();
	    String title = entity.getTitle();
	    long parentId = entity.getParentId();
	    long userId = entity.getOwnerUserId();

	    String creationDate = entity.getCreationDate();

	    String body = null;
	    if (entity.getCleanText() == null) {

		StopWordRemover stopWordRemover = new StopWordRemover(entity.getBody());
		body = stopWordRemover.getPlainText();
		System.out.println("Clean text " + body);
	    } else {
		body = entity.getCleanText();
	    }

	    long postTypeId = entity.getPostTypeId();
	    if (postTypeId == -1 && title != null && title.startsWith("Re:")) {
		postTypeId = 2;
	    } else {
		postTypeId = 1;
	    }

	    fullSearchableText = new StringBuffer();
	    if (title != null && title.length() > 0) {
		fullSearchableText.append(title);
		fullSearchableText.append(" ");
	    }

	    if (body != null && body.length() > 0) {
		fullSearchableText.append(body);
		fullSearchableText.append(" ");
	    }

	    // System.out.println("Searchable text ::" +
	    // fullSearchableText.toString());

	    if (fullSearchableText != null && creationDate != null) {



		dataDoc.add(new StringField("postid", String.valueOf(postId), Field.Store.YES));
		dataDoc.add(new StringField("parentid", String.valueOf(parentId), Field.Store.YES));
		dataDoc.add(new StringField("creationDate", creationDate, Field.Store.YES));
		dataDoc.add(new StringField("userid", String.valueOf(userId), Field.Store.YES));
		dataDoc.add(new TextField("postTypeId", String.valueOf(postTypeId), Field.Store.YES));
		dataDoc.add(new TextField("searchableText", fullSearchableText.toString(), Field.Store.YES));

		indexWriter.addDocument(dataDoc);
	    }
	}

	if (indexWriter != null) {
	    // TODO: Add relative path of the the index directory to the
	    // datainfo table.
	    indexWriter.close();
	}

	updateSemanticsIndex();
	System.out.println("Building index completed...");

    }

    /**
     * 
     * @throws IOException
     * @throws SQLException
     */
    private void updateSemanticsIndex() throws IOException, SQLException {

	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	Directory semanticsIndexDir = FSDirectory.open(new File(String.format(semanticsIndexBasePath, indexDirectoryPath)).toPath());

	indexWriter = new IndexWriter(semanticsIndexDir, config);

	// If index already exists. Delete all.
	if (indexWriter != null) {
	    indexWriter.deleteAll();
	}

	Document semanticDataDoc = new Document();

	Dao<DataEntity, Long> postsDao = DaoManager.createDao(connSrc, DataEntity.class);
	Dao<SemanticTagEntity, Long> semanticsDao = DaoManager.createDao(connSrc, SemanticTagEntity.class);

	List<DataEntity> dataEntites = postsDao.queryForAll();

	for (DataEntity entity : dataEntites) {
	    semanticDataDoc = new Document();

	    long postId = entity.getPostId();
	    long parentId = entity.getParentId();
	    long userId = entity.getOwnerUserId();
	    String creationDate = entity.getCreationDate();

	    SemanticTagEntity semanticEntity = semanticsDao.queryForId(postId);
	    StringBuffer fullSearchableText = new StringBuffer();

	    if (semanticEntity != null) {
		// System.out.println("SEMANTIC TAGS :: " +
		// semanticEntity.getTags());
		fullSearchableText.append(semanticEntity.getTags().replaceAll(",", " "));

		log.info("Semantic searchable texxt:: " + fullSearchableText);

		semanticDataDoc.add(new StringField("postid", String.valueOf(postId), Field.Store.YES));
		semanticDataDoc.add(new StringField("parentid", String.valueOf(parentId), Field.Store.YES));
		semanticDataDoc.add(new StringField("creationDate", creationDate, Field.Store.YES));
		semanticDataDoc.add(new StringField("userid", String.valueOf(userId), Field.Store.YES));
		semanticDataDoc.add(new TextField("searchableText", fullSearchableText.toString(), Field.Store.YES));
	    }

	    indexWriter.addDocument(semanticDataDoc);
	}

	if (indexWriter != null) {
	    // TODO: Add relative path of the the index directory to the
	    // datainfo table.
	    indexWriter.close();
	}
    }
}
