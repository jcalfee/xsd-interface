//import static org.compass.core.mapping.rsem.builder.RSEM.id;
//import static org.compass.core.mapping.rsem.builder.RSEM.property;
//import static org.compass.core.mapping.rsem.builder.RSEM.resource;
//
//import java.util.Date;
//
//import org.compass.core.Compass;
//import org.compass.core.CompassHits;
//import org.compass.core.CompassSearchSession;
//import org.compass.core.CompassSession;
//import org.compass.core.Property;
//import org.compass.core.Resource;
//import org.compass.core.ResourceFactory;
//import org.compass.core.config.CompassConfiguration;
//import org.compass.core.config.CompassEnvironment;
//
//import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

// TODO Complete
public class CompassPMF {

//    private static final LocalServiceTestHelper dataStoreHelper = new LocalServiceTestHelper(
//            new LocalDatastoreServiceTestConfig());
//
//    private static final Compass compass;
//
//    // private static final CompassGps compassGps;
//
//    static {
//        dataStoreHelper.setUp();
//
//        CompassConfiguration conf = new CompassConfiguration();
//        conf.setConnection("gae://index").setSetting(
//                CompassEnvironment.ExecutorManager.EXECUTOR_MANAGER_TYPE,
//                "disabled");// .addScan("package")
//
//        // mirror JDO API
//        // compassGps = new SingleCompassGps(compass);
//        // compassGps.addGpsDevice(new Jdo2GpsDevice("appengine", PMF.get()));
//        // compassGps.start();
//        //
//        // // complete re-indexing
//        // compassGps.index();
//
//        conf.addMapping(resource("a").add(id("id")));
//        conf.addMapping(resource("b").add(id("id1")).add(id("id2")));
//        conf.addMapping(resource("c").add(id("id1")).add(property("value1"))
//                .add(
//                        property("value2").store(Property.Store.YES).index(
//                                Property.Index.ANALYZED)).add(
//                        property("value3").store(Property.Store.COMPRESS)
//                                .index(Property.Index.ANALYZED)).add(
//                        property("value4").store(Property.Store.YES).index(
//                                Property.Index.NOT_ANALYZED)).add(
//                        property("value5").store(Property.Store.YES).index(
//                                Property.Index.NO)/* .converter("my-date") */));// Failed
//        // to
//        // find
//        // converter
//        // by
//        // name
//        // [my-date]
//
//        compass = conf.buildCompass();
//    }
//
//    public void tearDown() {
//        dataStoreHelper.tearDown();
//    }
//
//    public static Compass getCompass() {
//        return compass;
//    }
//
//    static String p(String label, Object text) {
//        System.out.println("CompassPMF " + label + " " + text);
//        return text == null ? null : text.toString();
//    }
//
//    /**
//     * file:///home/jcalfee/java/compass-2.3.0-beta1/docs/reference/html_single/
//     * index.html#core-rsem
//     */
//    private void saveResources() {
//        // add the resource
//        ResourceFactory resourceFactory = compass.getResourceFactory();
//        Resource r1 = resourceFactory.createResource("a");
//        Property id = resourceFactory.createProperty("id", "1",
//                Property.Store.YES, Property.Index.NOT_ANALYZED);
//        r1.addProperty(id);
//        r1.addProperty(resourceFactory.createProperty("mvalue",
//                "property test", Property.Store.YES, Property.Index.ANALYZED));
//
//        // resource-property mappings can simplify Resource construction code
//        Resource r2 = resourceFactory.createResource("c");
//        r2.addProperty("id1", 1);
//        r2.addProperty("value1", "this is a sample value");
//        r2.addProperty("value5", new Date()); // will use the my-date
//        // converter (using the format
//        // defined there)
//
//        CompassSession session = compass.openSession();
//        try {
//            session.save(r1);
//            session.save(r2);
//        } finally {
//            session.close();
//        }
//    }
//
//    // public void testInsert() {
//    // CompassSession session = compass.openSession();
//    // try {
//    // CompassTransaction tx = session.beginTransaction();
//    // Resource authorResource = session.createResource("author");
//    // Property authorIdProp = session.createProperty("id", "AUTHOR0812",
//    // Property.Store.YES, Property.Index.UN_TOKENIZED);
//    // Property authorNameProp = session.createProperty("name",
//    // "Jack London", Property.Store.YES, Property.Index.TOKENIZED);
//    // authorResource.addProperty(authorIdProp);
//    // authorResource.addProperty(authorNameProp);
//    // session.save(resource);
//    // tx.commit();
//    // } finally {
//    // session.close();
//    // }
//    // }
//
//    public void testSearch() {
//        saveResources();
//        Compass compass = CompassPMF.getCompass();
//        try {
//            CompassSearchSession search = compass.openSearchSession();
//            try {
////                CompassDetachedHits hits;
////                CompassSession session = compass.openSession();
////                try {
////                    CompassQueryBuilder queryBuilder = session.queryBuilder();
////                    CompassHits h = queryBuilder.bool().addMust(
////                            queryBuilder.alias("a")).addMust(
////                            queryBuilder.term("a.familyName", "london"))
////                            .toQuery().hits();
////                    hits = h.detach();
////                } finally {
////                    session.close();
////                }
//                CompassHits hits = search.find((String) p("search", "sample"));
//                try {
//                    for (int i = 0; i < hits.length(); i++) {
//                        {
//                            // Object o = null;
//                            p("result", /* o = */hits.data(i));
//                        }
//                        Resource resource = hits.resource(i);// loads from
//                        // data-store
//                        Property property = resource
//                                .getProperty("resourceProperty");
//                        String value = resource.getValue("resourceProperty");
//                        p("resourceProperty prop", property);
//                        p("resourceProperty value", value);
//                    }
//                } finally {
//                    //hits.close();
//                }
//            } finally {
//                search.close();
//            }
//        } finally {
//            compass.close();
//        }
//    }
//
//    public static void main(String[] args) {
//        CompassPMF c = new CompassPMF();
//        try {
//            c.testSearch();
//        } finally {
//            c.tearDown();
//        }
//    }

}
