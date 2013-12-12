import info.jcalfee.gae.ds.DataStore;
import info.jcalfee.gae.ds.PMF;
import info.jcalfee.xsi.client.User;
import info.jcalfee.xsi.client.XmlSchema;
import info.jcalfee.xsi.client.XmlSchemaInstance;
import info.jcalfee.xsi.client.XmlSchema.Attribute;
import info.jcalfee.xsi.client.XmlSchema.ComplexType;
import info.jcalfee.xsi.client.XmlSchema.Element;
import info.jcalfee.xsi.client.XmlSchema.Type;

import java.util.LinkedList;
import java.util.StringTokenizer;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DataStoreTest {
    
    private final LocalServiceTestHelper dataStoreHelper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    public void setUp() {
        dataStoreHelper.setUp();
    }
    
    public XmlSchemaInstance instance(String objString) {
        XmlSchema schema = new XmlSchema();
        XmlSchemaInstance xi = new XmlSchemaInstance(schema, "Test");
        xi.user = new User(); 
        XmlSchemaInstance xiRoot = null;
        String[] tokens = objString.split("->");
        for (int i = 0; i < tokens.length; i++) {
            
            Element element = new Element();
            xi = xi.add(element, true);
            if(xiRoot == null)
                xiRoot = xi;
            
            ComplexType ctype = new ComplexType();
            element.type = ctype;
            
            String elementName = tokens[i].trim();
            int attyIndex = elementName.indexOf('{');
            if (attyIndex > 0) {
                String attyStr = elementName.substring(attyIndex + 1, elementName.length() - 1);
                elementName = elementName.substring(0, attyIndex);
                StringTokenizer st = new StringTokenizer(attyStr, ", ");
                while (st.hasMoreElements()) {
                    String atty = (String) st.nextElement();
                    String[] nameValue = atty.split("=");
                    if (nameValue.length != 2)
                        p("Expecting name=value format: " + atty);

                    Attribute attribute = new Attribute();
                    attribute.type = new Type() {
                        {
                            name = "string";
                        }
                    };
                    attribute.name = nameValue[0];
                    ctype.attributes.add(attribute);
                    xi.values.put(nameValue[0], nameValue[1]);
                }
            }
            element.name = elementName;
        }
        return xiRoot;
    }

    public static void p(String s) {
        System.out.println(s);
    }

    public void tearDown() {
        dataStoreHelper.tearDown();
    }
    
    public void testTx() {
        XmlSchemaInstance parent = instance("hr -> person");
        LinkedList<XmlSchemaInstance> list = new LinkedList<XmlSchemaInstance>();
        list.addAll(parent.hierarchy);
        
        Element jack = instance("employee{name=jack}").element; 
        list.add(parent.add(jack, false));
        
        Element joe = instance("employee{name=joe}").element; 
        list.add(parent.add(joe, false));
        
        DataStore.save(list);
        p("Done");
    }
    
    public static void main(String[] args) {
        DataStoreTest test = new DataStoreTest();
        test.setUp();
        test.testTx();
        test.tearDown();
        PMF.pm().close();
    }
    
}
