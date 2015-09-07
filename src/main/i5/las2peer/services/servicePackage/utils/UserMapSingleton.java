/**
 * 
 */
package i5.las2peer.services.servicePackage.utils;

import i5.las2peer.services.servicePackage.database.entities.UserEntity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

/**
 * A singleton class to create a user map containing detail about each user in
 * the dataset.
 * 
 * @author sathvik
 *
 */
public class UserMapSingleton {
    private static UserMapSingleton instance = null;
    private Map<Long, UserEntity> userId2userObj = new HashMap<Long, UserEntity>();

    private UserMapSingleton() {
    }

    public static synchronized UserMapSingleton getInstance() {
	if (instance == null) {
	    instance = new UserMapSingleton();
	}

	return instance;
    }

    public Map<Long, UserEntity> getUserMap(ConnectionSource connectionSource) throws SQLException {
	Dao<UserEntity, Long> userDao = DaoManager.createDao(connectionSource, UserEntity.class);
	List<UserEntity> user_entites = userDao.queryForAll();
	for (UserEntity entity : user_entites) {
	    userId2userObj.put(entity.getUserId(), entity);
	}
	return userId2userObj;
    }
}

