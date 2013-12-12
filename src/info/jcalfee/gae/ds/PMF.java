package info.jcalfee.gae.ds;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {
    }

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
    
    public static PersistenceManager pm() {
        return pmfInstance.getPersistenceManager();
    }

    public static DatastoreService ds() {
        return DatastoreServiceFactory.getDatastoreService();
    }

}
