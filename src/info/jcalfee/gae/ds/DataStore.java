package info.jcalfee.gae.ds;

import info.jcalfee.xsi.FunctionUtil;
import info.jcalfee.xsi.client.DirectResponse;
import info.jcalfee.xsi.client.XmlSchemaInstance;
import info.jcalfee.xsi.client.XmlSchema.Attribute;
import info.jcalfee.xsi.client.XmlSchema.Element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

/**
 * 
 * @author jcalfee
 */
public class DataStore {

    // Optimistic locking value can be incremented and tested here
    // in the application layer using a transaction, because the
    // transaction is atomic.
    //
    // In other words,
    // " If the entity is updated during the transaction, then the
    // transaction fails with an exception."

    /**
     * When true, DataStore needs to reject attempts to overwrite changed
     * records.
     */
    // public boolean optimisticLocking = true;

    // private static Logger log = Logger.getLogger(DataStore.class.getName());

    /**
     * List of name value pair maps. Each map contains a 'xsi_key' primary key.
     * 
     * @param schema
     * @return
     */
    public static LinkedList<HashMap<String, Object>> list(
        XmlSchemaInstance schema) {

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query query =
            new Query(tableName(schema.name, schema.element.getIdOrName()));
        query.setAncestor(parentKey(schema));
        PreparedQuery prepQuery = ds.prepare(query);
        LinkedList<HashMap<String, Object>> entities =
            new LinkedList<HashMap<String, Object>>();

        for (Entity entity : prepQuery.asIterable()) {
            entities.add(valueMap(schema.element, entity));
        }
        return entities;
    }

    /**
     * 
     * @param schema
     * @param element
     * @return Loads schema's element using schema.elementKey saving values in
     *         schema.values. An empty map is saved if schema.elementKey is
     *         non-existent or not found.
     */
    public static void load(XmlSchemaInstance schema) {
        // Same parentKey allows for same entity space transactions
        Entity entity = null;

        Element element = schema.element;
        if (element == null || schema.elementKey == null)
            return;

        else {
            Key pk = pk(schema);
            try {
                DatastoreService ds =
                    DatastoreServiceFactory.getDatastoreService();

                entity = ds.get(pk);

            } catch (EntityNotFoundException e1) {
                schema.elementKey = null;
                DirectResponse response = new DirectResponse();
                response.message =
                    FunctionUtil.space(element.name) + " was not found.";
                response.success = false;
                schema.response = response;
                return;
            }
        }
        schema.values = valueMap(element, entity);
    }

    static HashMap<String, Object> valueMap(Element element, Entity entity) {
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        for (Attribute attribute : element.getAllAttributes()) {
            Object value = entity.getProperty(attribute.getIdOrName());
            valueMap.put(attribute.name, value);
        }
        valueMap.put("xsi_version", entity.getProperty("xsi_version"));
        valueMap.put("xsi_key", entity.getKey().getId());
        return valueMap;
    }

    /**
     * Use schema's element to inspect and save parameters
     * 
     * @param schemaName
     * @param element
     *            (schema.elementKey is set)
     * @param parameters
     */
    public static void save(XmlSchemaInstance schema) {
        LinkedList<XmlSchemaInstance> list =
            new LinkedList<XmlSchemaInstance>();
        list.add(schema);
        save(list);
    }

    public static void save(Iterable<XmlSchemaInstance> schema) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = ds.beginTransaction();
        try {
            save(schema, ds, txn);
            txn.commit();
        } finally {
            if (txn.isActive())
                txn.rollback();
        }
    }

    static void save(Iterable<XmlSchemaInstance> schemaList,
        DatastoreService ds, Transaction txn) {
        for (XmlSchemaInstance schema : schemaList) {
            // TODO: take advantage of Google's Iterable API
            _save(schema, ds, txn);
        }
    }

    static void _save(XmlSchemaInstance schema, DatastoreService ds,
        Transaction txn) {
        DirectResponse response = new DirectResponse();
        Element element = schema.element;
        String keyStr = schema.elementKey;
        // Same parentKey allows for same entity space transactions
        Entity entity = null;
        boolean changed = false;
        Key parentKey = parentKey(schema);
        if (keyStr == null) {
            changed = true;
            entity =
                new Entity(
                    tableName(schema.name, schema.element.getIdOrName()),
                    parentKey);
        } else {
            Key pk =
                KeyFactory.createKey(parentKey, tableName(schema.name,
                    schema.element.getIdOrName()), Integer.parseInt(keyStr));
            try {
                entity = ds.get(txn, pk);
            } catch (EntityNotFoundException e1) {
                response.message = "Could not find " + element + " for update.";
                response.success = false;
                schema.response = response;
                return;
            }
        }

        Map<String, Object> oldProperties = entity.getProperties();
        for (Attribute attribute : element.getAllAttributes()) {
            Object value = schema.values.get(attribute.getIdOrName());
            String key = attribute.getIdOrName();
            Object oldValue = oldProperties.get(key);

            if ((value == null && oldValue != null)
                || (oldValue == null && value != null)
                || (value != null && !value.equals(oldValue))) {
                entity.setProperty(key, value);
                changed = true;
            }
        }
        if (changed) {
            // optimistic locking
            {
                Long xsiVersion = (Long) entity.getProperty("xsi_version");
                if (xsiVersion != null) {
                    String version = (String) schema.values.get("xsi_version");
                    if (version == null
                        || !xsiVersion.toString().equals(version)) {
                        
                        
                        response.message =
                            "<b>"
                                + element
                                + "</b> was updated previously. Review the changes below and re-save.";
                        response.success = false;
                        // give the new entity to the client so it can merge the
                        // changes
                        HashMap<String, String> oldMap =
                            new HashMap<String, String>();
                        boolean differ = false;
                        for (Attribute attribute : element.getAllAttributes()) {
                            String key = attribute.getIdOrName();
                            Object value = oldProperties.get(key);
                            Object newValue = schema.values.get(attribute.getIdOrName());
                            if(!value.equals(newValue)) {
                                differ = true;
                                oldMap.put(key, FunctionUtil.format(attribute,
                                    value));
                            }
                        }
                        oldMap.put("xsi_version", xsiVersion.toString());
                        if(differ) {
                            response.data = oldMap;
                            schema.response = response;
                        }
                        return;
                    }
                    xsiVersion = xsiVersion.longValue() + 1;
                    schema.values.put("xsi_version", xsiVersion);
                    entity.setProperty("xsi_version", xsiVersion);
                } else {
                    xsiVersion = new Long(1);
                    entity.setProperty("xsi_version", xsiVersion);
                    schema.values.put("xsi_version", xsiVersion);
                }
            }
            schema.elementKey = String.valueOf(ds.put(txn, entity).getId());
            response.message = "Updated " + FunctionUtil.space(element.name);
            response.success = true;
            schema.response = response;
        } else {
            {
                // Upgrade their version in-case it is an old one.
                // This is fine since we know the element is identical.
                Long xsiVersion = (Long) entity.getProperty("xsi_version");
                schema.values.put("xsi_version", xsiVersion);
            }
            response.message =
                FunctionUtil.space(element.name) + " is already up-to-date ";
            response.success = true;
            schema.response = response;
        }
    }

    public static void delete(XmlSchemaInstance schema) {
        Element element = schema.element;
        if (element == null || schema.elementKey == null)
            return;

        Key pk = pk(schema);
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        ds.delete(pk);
        DirectResponse response = new DirectResponse();
        response.message = "Deleted " + FunctionUtil.space(element.name);
        response.success = true;
        schema.response = response;
    }

    public static void delete(XmlSchemaInstance schema,
        LinkedList<Long> idValues) {
        Element element = schema.element;
        if (element == null || idValues.isEmpty())
            return;

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        LinkedList<Key> pkList = pkList(schema, idValues);
        ds.delete(pkList);

        DirectResponse response = new DirectResponse();
        response.message =
            "Deleted " + pkList.size() + " '"
                + FunctionUtil.space(element.name) + "' records.";

        response.success = true;
        schema.response = response;
    }

    public static interface RenameCallback {
        public String newName(String name);
    }

    // public static void copy(XmlSchemaInstance schema, RenameCallback cb) {
    // Element element = schema.element;
    // DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    // Query query = new Query(tableName(schema.name,
    // schema.element.getIdOrName()));
    // query.setAncestor(parentKey(schema));
    // PreparedQuery prepQuery = ds.prepare(query);
    //
    // for (Entity entity : prepQuery.asIterable()) {
    // Key key = entity.getKey();
    // Key newKey = KeyFactory.createKey(kind, id)
    // Entity newEntity = new Entity();
    // newEntity.setPropertiesFrom(entity)
    // }
    //        
    // }

    static Key pk(XmlSchemaInstance schema) {
        Key pk =
            KeyFactory.createKey(parentKey(schema), tableName(schema.name,
                schema.element.getIdOrName()), Long
                .parseLong(schema.elementKey));

        return pk;
    }

    /**
     * Top-most parent is the version of this application. Uses
     * xmlSchema.hierarchy.
     * 
     * 
     * @param schema
     * @return
     */
    static Key parentKey(XmlSchemaInstance schema) {
        XmlSchemaInstance parent = schema.getParent();
        if (schema.user == null)
            throw new Error("Schema user may not be null");

        if (parent == null)
            return KeyFactory.createKey(schema.name, ConfigHelper.getVersion());

        Key parentKey =
            KeyFactory.createKey(parentKey(parent), tableName(parent.name,
                parent.element.getIdOrName()), Long
                .parseLong(parent.elementKey));

        return parentKey;
    }

    static LinkedList<Key> pkList(XmlSchemaInstance schema,
        LinkedList<Long> idValues) {
        LinkedList<Key> pkList = new LinkedList<Key>();
        Key parentKey = parentKey(schema);
        for (Long id : idValues) {
            Key pk =
                KeyFactory.createKey(parentKey, tableName(schema.name,
                    schema.element.getIdOrName()), id);
            pkList.add(pk);
        }
        return pkList;
    }

    static String tableName(String schemaName, String elementIdOrName) {
        return schemaName + '_' + elementIdOrName;
    }

}
