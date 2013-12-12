//import static org.compass.core.mapping.xsem.builder.XSEM.content;
//import static org.compass.core.mapping.xsem.builder.XSEM.id;
//import static org.compass.core.mapping.xsem.builder.XSEM.property;
//import static org.compass.core.mapping.xsem.builder.XSEM.xml;
//
//import java.io.IOException;
//import java.io.StringReader;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.compass.core.Compass;
//import org.compass.core.CompassHits;
//import org.compass.core.CompassSearchSession;
//import org.compass.core.CompassSession;
//import org.compass.core.Property;
//import org.compass.core.Resource;
//import org.compass.core.config.CompassConfiguration;
//import org.compass.core.config.CompassEnvironment;
//import org.compass.core.xml.AliasedXmlObject;
//import org.compass.core.xml.javax.NodeAliasedXmlObject;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

// TODO Complete
public class CompassPMF_XML {

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
//        conf.addMapping(xml("data1").xpath("/xml-fragment/data[1]").add(
//                id("id/@value").indexName("id")).add(property("data1/@value"))
//                .add(property("data1").indexName("eleText")));
//
//        conf.addMapping(xml("data2").xpath("/xml-fragment/data").add(
//                id("id/@value").indexName("id")).add(property("data1/@value"))
//                .add(property("data1").indexName("eleText")));
//
//        conf.addMapping(xml("data3").xpath("/xml-fragment/data").add(
//                id("id/@value").indexName("id")).add(property("data1/@value"))
//                .add(property("data1").indexName("eleText")).add(
//                        content("content")));
//
//        // Based on internal performance testing, the preferable configuration
//        // is a pooled converter that uses either dom4j or JDOM with a pull
//        // parser (StAX or XPP).
//        
//        conf.setSetting(CompassEnvironment.Xsem.XmlContent.TYPE,
//                CompassEnvironment.Xsem.XmlContent.JDom.TYPE_STAX);
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
//     * 
//     * @throws ParserConfigurationException
//     * @throws IOException
//     * @throws SAXException
//     */
//    private void saveResources() throws SAXException, IOException,
//            ParserConfigurationException {
//        String xml = "<xml-fragment>" + "<data>" + "<id value=\"1\"/>"
//                + "<data1 value=\"data11attr\">data11</data1>"
//                + "<data1 value=\"data12attr\">data12</data1>" + "</data>"
//                + "<data>" + "<id value=\"2\"/>"
//                + "<data1 value=\"data21attr\">data21</data1>"
//                + "<data1 value=\"data22attr\">data22</data1>" + "</data>"
//                + "</xml-fragment>";
//
//        // used the registered converter (or the one configured against the xml-content mapping for the given alias)
//        //AliasedXmlObject rawXmlDocumentBuilderFactoryObject = new RawAliasedXmlObject("xoRaw", xml);
//        
//        Document doc = DocumentBuilderFactory.newInstance()
//                .newDocumentBuilder().parse(
//                        new InputSource(new StringReader(xml)));
//
//        AliasedXmlObject xmlObject = new NodeAliasedXmlObject("xoJSE5", doc);
//        CompassSession session = compass.openSession();
//        try {
//            session.save(xmlObject);
//            //session.save(rawXmlObject);
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
//    public void testSearch() throws SAXException, IOException,
//            ParserConfigurationException {
//        saveResources();
//        Compass compass = CompassPMF_XML.getCompass();
//        try {
//            CompassSearchSession search = compass.openSearchSession();
//            try {
//                // CompassDetachedHits hits;
//                // CompassSession session = compass.openSession();
//                // try {
//                // CompassQueryBuilder queryBuilder = session.queryBuilder();
//                // CompassHits h = queryBuilder.bool().addMust(
//                // queryBuilder.alias("a")).addMust(
//                // queryBuilder.term("a.familyName", "london"))
//                // .toQuery().hits();
//                // hits = h.detach();
//                // } finally {
//                // session.close();
//                // }
//                CompassHits hits = search.find((String) p("search", "1"));
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
//                    // hits.close();
//                }
//            } finally {
//                search.close();
//            }
//        } finally {
//            compass.close();
//        }
//    }
//
//    public static void main(String[] args) throws SAXException, IOException,
//            ParserConfigurationException {
//        CompassPMF_XML c = new CompassPMF_XML();
//        try {
//            c.testSearch();
//        } finally {
//            c.tearDown();
////            System.exit(0);
//        }
//    }

}
