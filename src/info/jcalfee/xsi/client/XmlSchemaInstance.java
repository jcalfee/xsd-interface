package info.jcalfee.xsi.client;

import info.jcalfee.xsi.client.XmlSchema.ComplexType;
import info.jcalfee.xsi.client.XmlSchema.Element;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Data Container for the XmlSchema and one of its Elements. Also, contains
 * extra information and logic to process information in the schema. This object
 * may be instantiated many times in a single request for each element
 * reference. The XmlSchema reference, however, does not contain any data values
 * and is shared by objects of this type using the same schema.
 * 
 * @author jcalfee
 * 
 */
public class XmlSchemaInstance {

    public XmlSchema schema;

    /**
     * Usually the name of the XML Schema file. No path, just a short
     * descriptive name.
     */
    public String name;

    /**
     * User making the request
     */
    public User user;

    /**
     * A Path through the relationships (or nested elements) to this.element.
     */
    public LinkedList<XmlSchemaInstance> hierarchy =
        new LinkedList<XmlSchemaInstance>();

    /**
     * Access point to the XML Schema Data.
     */
    public Element element;

    /**
     * The values for this element
     */
    public HashMap<String, Object> values = new HashMap<String, Object>();

    /**
     * Response from the server about the last operation.
     */
    public DirectResponse response;

    /**
     * Primary lookup key
     */
    public String elementKey;

    public XmlSchemaInstance() {
    }

    /**
     * New schema containing references to all immutable objects from the
     * provided schema.
     * 
     * @param schema
     */
    public XmlSchemaInstance(XmlSchemaInstance schema) {
        this.schema = schema.schema;
        this.user = schema.user;
        this.name = schema.name;
        this.element = schema.element;
    }

    /**
     * The Root of the XML Schema. There is no one single element.
     * 
     * @param schema
     */
    public XmlSchemaInstance(XmlSchema schema, String schemaName) {
        this.schema = schema;
        this.name = schemaName;
    }
    
    /**
     * @param element
     *            new element
     * @param sameHierarchy
     *            Does the new element share this hierarchy? If not, the new element
     *            (child) will get a clone of the hierarchy updated with a
     *            reference to its self, and the parent's hierarchy (this) will remain
     *            unchanged.
     * 
     * @return new element encapsulated in a schema instance object
     */
    @SuppressWarnings("unchecked")
	public XmlSchemaInstance add(Element element, boolean sameHierarchy) {
        XmlSchemaInstance i = new XmlSchemaInstance(schema, name);
        i.element = element;
        i.user = user;
        if (sameHierarchy)
            i.hierarchy = hierarchy;
        else
            i.hierarchy = (LinkedList<XmlSchemaInstance>) hierarchy.clone();

        i.hierarchy.add(i);
        return i;
    }

    public String getElementKey() {
        return elementKey;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
    
//    public XmlSchema getSchema() {
//        return schema;
//    }

    public XmlSchemaInstance getFirst() {
        return hierarchy.get(0);
    }

    public XmlSchemaInstance getLast() {
        return hierarchy.get(hierarchy.size() - 1);
    }

    public XmlSchemaInstance getParent() {
        int loc = hierarchy.indexOf(this);
        if (loc < 1)
            return null;

        return hierarchy.get(loc - 1);
    }

    public String getUrlString() {
        String urlStr = name;
        for (XmlSchemaInstance i : hierarchy) {
            urlStr += "/" + i.element.toString();
            if (i.elementKey != null)
                urlStr += "(" + i.elementKey + ")";

            if (element.toString().equals(i.element.toString()))
                return urlStr;
        }
        return urlStr;
    }

    // public String getNewUrlString() {
    // String urlStr = name;
    // XmlSchemaInstance lastI = hierarchy.getLast();
    // for (XmlSchemaInstance i : hierarchy) {
    // if (i == lastI)
    // // leave off the ID
    // urlStr += "/" + i.element.name;
    // else
    // // include the ID
    // urlStr += "/" + i.element.toString();
    //
    // if (element.toString().equals(i.element.toString()))
    // return urlStr;
    // }
    // return urlStr;
    // }

    public LinkedList<XmlSchemaInstance> getHierarchy() {
        return hierarchy;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public HashMap<String, Object> getValues() {
        return values;
    }

    public DirectResponse getResponse() {
        if (response == null)
            response = new DirectResponse();

        return response;
    }

    public Element find(String elementName, Element parentElement) {
        if (parentElement == null)
            for (Element element : schema.elements) {
                if (element.name.equals(elementName))
                    return element;
            }
        else
            return parentElement.getChild(elementName);

        return null;
    }

    public LinkedList<ComplexType> getComplexTypes() {
        return schema.getComplexTypes();
    }

    public LinkedList<XmlSchemaInstance> getChildren() {
        if (element == null)
            return toInst(schema.getElements());

        if (!(element.type instanceof ComplexType))
            return toInst(new LinkedList<Element>());

        ComplexType complexType = (ComplexType) element.type;
        if (complexType.sequence == null)
            return toInst(new LinkedList<Element>());

        return toInst(complexType.sequence.elements);
    }

    private LinkedList<XmlSchemaInstance> toInst(LinkedList<Element> el) {
        LinkedList<XmlSchemaInstance> list =
            new LinkedList<XmlSchemaInstance>();
        for (Element e : el) {
            XmlSchemaInstance xsi = new XmlSchemaInstance(this);
            xsi.element = e;
            list.add(xsi);
        }
        return list;
    }

    public HashMap<String, String> getNamespaceMap() {
        return schema.getNamespaceMap();
    }

}
