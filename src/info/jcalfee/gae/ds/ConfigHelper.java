package info.jcalfee.gae.ds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * <p>
 * All public variables are persisted (including default values and types).
 * </p>
 * 
 * <p>
 * Changed values are updated by calling Config.sync().
 * </p>
 * 
 * <p>
 * Persisted values are loaded upon class loading and upon call to
 * Config.refresh().
 * </p>
 * 
 * @author jcalfee
 * 
 */
public class ConfigHelper {

    public static String getVersion() {
        String version =
            System.getProperty("com.google.appengine.application.version");

        if (version == null)
            version = "dev";
        else {
            int i = version.lastIndexOf('.');
            version = version.substring(0, i);
        }
        return version;
    }

    /**
     * @param class1
     *            persistent
     * @param key
     *            primary using key
     * @param obj
     *            instance, or <b>null</b> to use static fields from class1
     * @param pull
     *            data into class1 or obj, <b>false</b> to write
     * @return write operation performed
     * 
     */
    public static synchronized boolean sync(Class<? extends Object> class1,
        String key, Object obj, boolean pull) {

        Key pk = KeyFactory.createKey(class1.getName(), key);
        return sync(class1, pk, obj, pull);
    }

    public static synchronized boolean sync(Class<? extends Object> class1,
        Key primaryKey, Object obj, boolean pull) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        boolean newEntity = false;
        // try {
        Entity configEntity = null;
        try {
            configEntity = ds.get(primaryKey);
        } catch (EntityNotFoundException e1) {
            if (pull)
                return false;

            newEntity = true;
            configEntity = new Entity(primaryKey);
        }
        boolean entityChange = false;
        Field[] fields = class1.getFields();
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers()))
                continue;

            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);
                Object entityValue = configEntity.getProperty(fieldName);
                if (entityValue == null) {
                    entityChange = true;
                    configEntity.setProperty(fieldName, fieldValue);
                } else if (pull)
                    field.set(obj, entityValue);
                else if (!entityValue.equals(fieldValue)) {
                    entityChange = true;
                    configEntity.setProperty(fieldName, fieldValue);
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (newEntity || entityChange) {
            ds.put(configEntity);
            return true;
        }

        return false;
    }

    public static String toString(Class<Object> configClass, Object obj) {
        String str = configClass.getName() + " [";
        Field[] fields = configClass.getFields();
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers()))
                continue;

            String fieldName = field.getName();
            try {
                Object fieldValue = field.get(obj);
                str += fieldName + "=" + fieldValue + ", ";
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return str.substring(0, str.length() - 2) + "]";
    }
}
