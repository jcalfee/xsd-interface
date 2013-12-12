package info.jcalfee.xsi;

import info.jcalfee.xsi.client.XmlSchema;
import info.jcalfee.xsi.client.XmlSchema.Annotatable;
import info.jcalfee.xsi.client.XmlSchema.Attribute;
import info.jcalfee.xsi.client.XmlSchema.AttributeGroup;
import info.jcalfee.xsi.client.XmlSchema.ComplexType;
import info.jcalfee.xsi.client.XmlSchema.Element;
import info.jcalfee.xsi.client.XmlSchema.SimpleType;
import info.jcalfee.xsi.client.XmlSchema.Type;
import info.jcalfee.xsi.client.XmlSchema.Use;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Appinfo;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Documentation;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Appinfo.AnyNode;
import info.jcalfee.xsi.client.XmlSchema.ComplexType.Sequence;
import info.jcalfee.xsi.client.XmlSchema.Element.BaseKey;
import info.jcalfee.xsi.client.XmlSchema.Element.Key;
import info.jcalfee.xsi.client.XmlSchema.Element.KeyRef;
import info.jcalfee.xsi.client.XmlSchema.Element.Unique;
import info.jcalfee.xsi.client.XmlSchema.Element.BaseKey.Field;
import info.jcalfee.xsi.client.XmlSchema.Element.BaseKey.Selector;
import info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction;
import info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction.Enumeration;
import info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction.Pattern;
import info.jcalfee.xsi.client.XmlSchema.Use.Optional;
import info.jcalfee.xsi.client.XmlSchema.Use.Prohibited;
import info.jcalfee.xsi.client.XmlSchema.Use.Required;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * See this.main() for usage. When done, this.schema will contain the XML Schema
 * data.
 * 
 * @author jcalfee
 * 
 */
public class XSDUnmarshal extends DefaultHandler {

    public XmlSchema schema = new XmlSchema();

    private Stack<Object> stack = new Stack<Object>();

    private Object stackPeek;

    /**
     * Resolves back-references to complex types. Back-reference is reference in
     * the XML to a complex type before it is definition.
     */
    private HashMap<String/* complexType */, LinkedList<Element>> elementTypeMap = new HashMap<String/* complexType */, LinkedList<Element>>();

    private HashMap<String/* simpleType */, LinkedList<Attribute>> simpleTypeMap = new HashMap<String/* simpleType */, LinkedList<Attribute>>();

    private HashMap<String/* simpleType */, LinkedList<Restriction>> restrictionMap = new HashMap<String/* simpleType */, LinkedList<Restriction>>();

    private HashMap<String/* attributeGroup */, LinkedList<AttributeGroup>> attributeGroupMap = new HashMap<String/* attributeGroup */, LinkedList<AttributeGroup>>();

    public XSDUnmarshal(InputSource src, String saxParserClass)
            throws SAXException, IOException {
        XMLReader rdr = XMLReaderFactory.createXMLReader(saxParserClass);

        rdr.setContentHandler(this);
        rdr.parse(src);
    }

    public XSDUnmarshal(InputStream in, String saxParserClass) {
        try {
            InputSource src = new InputSource(in);
            try {
                XSDUnmarshal saxUms = new XSDUnmarshal(src, saxParserClass);

                this.schema = saxUms.schema;
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        // p("startPrefixMapping(" + prefix + "," + uri);
        // startPrefixMapping(,http://www.w3.org/2001/XMLSchema
        // startPrefixMapping(tns,http://www.example.com/PhoneBanking
        schema.namespaceMap.put(prefix, uri);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        // p("startElement(" + uri + ", " + localName + ", " + qName + ", " +
        // toStr(attributes));

        if (qName.equals("schema")) {
            stack.push(schema);
            // startElement(http://www.w3.org/2001/XMLSchema, schema, schema,
            // Attributes(LocalName=targetNamespace,QName=targetNamespace,Type=CDATA,URI=,Value=http://www.example.com/PhoneBanking)

            return;
        }

        stackPeek = stack.peek();

        if (stackPeek instanceof XmlSchema) {
            if (qName.equals("element")) {// XmlSchema
                // startElement(http://www.w3.org/2001/XMLSchema, element,
                // element,
                // Attributes(LocalName=name,QName=name,Type=CDATA,URI=,Value=AccountInfoLocalName=type,QName=type,Type=CDATA,URI=,Value=tns:AccountType)
                Element element = element(attributes);
                schema.elements.add(element);
                stack.push(element);
                return;
            }

            if (qName.equals("complexType")) {// XmlSchema
                // startElement(http://www.w3.org/2001/XMLSchema, complexType,
                // complexType,
                // Attributes(LocalName=name,QName=name,Type=CDATA,URI=,Value=Payee)
                ComplexType cType = new ComplexType();
                schema.complexTypes.add(cType);
                String nameStr = attributes.getValue("name").trim();
                cType.mixed = Boolean
                        .parseBoolean(attributes.getValue("mixed"));
                cType.name = nameStr;
                stack.push(cType);
                return;
            }

            if (qName.equals("simpleType")) {// XmlSchema
                SimpleType simpleType = new SimpleType();
                simpleType.name = attributes.getValue("name");
                schema.simpleTypes.add(simpleType);
                stack.push(simpleType);
                return;
            }

            if (qName.equals("attributeGroup")) {// XmlSchema
                AttributeGroup attributeGroup = new AttributeGroup();
                attributeGroup.name = attributes.getValue("name");
                schema.attributeGroups.add(attributeGroup);
                stack.push(attributeGroup);
                return;
            }
        }
        if (qName.equals("complexType") && stackPeek instanceof Element) {
            ComplexType cType = new ComplexType();
            cType.mixed = Boolean.parseBoolean(attributes.getValue("mixed"));
            Element element = (Element) stackPeek;
            element.type = cType;
            stack.push(cType);
            return;
        }
        if (stackPeek instanceof ComplexType) {
            if (qName.equals("sequence")) {
                ComplexType complexType = (ComplexType) stackPeek;
                Sequence sequence = new Sequence();
                complexType.sequence = sequence;
                stack.push(sequence);
                return;
            }
            if (qName.equals("attributeGroup")) {
                ComplexType complexType = (ComplexType) stackPeek;
                AttributeGroup attributeGroup = new AttributeGroup();
                String typeStr = attributes.getValue("ref");
                typeStr = typeStr.trim();
                if (typeStr.contains(":")) {
                    String[] s = typeStr.split(":");
                    // String namespace = s[0];//TODO namespace
                    String refStr = s[1];
                    LinkedList<AttributeGroup> list = attributeGroupMap
                            .get(refStr);
                    if (list == null) {
                        list = new LinkedList<AttributeGroup>();
                        attributeGroupMap.put(refStr, list);
                    }
                    list.add(attributeGroup);
                }
                complexType.attributeGroups.add(attributeGroup);
                stack.push(attributeGroup);
                return;

            }
        }
        if (qName.equals("element") && stackPeek instanceof Sequence) {
            Sequence sequence = (Sequence) stackPeek;
            Element element = element(attributes);
            sequence.elements.add(element);
            stack.push(element);
            return;
        }
        if (stackPeek instanceof Element) {
            if (qName.equals("key")) {
                Key key = new Key();
                key.name = attributes.getValue("name");
                Element element = (Element) stackPeek;
                element.keys.add(key);
                stack.push(key);
                return;
            }
            if (qName.equals("keyref")) {
                KeyRef keyref = new KeyRef();
                keyref.name = attributes.getValue("name");
                keyref.refer = attributes.getValue("refer");
                Element element = (Element) stackPeek;
                element.keyRefs.add(keyref);
                stack.push(keyref);
                return;
            }
            if (qName.equals("unique")) {
                Unique unique = new Unique();
                unique.name = attributes.getValue("name");
                Element element = (Element) stackPeek;
                element.unique.add(unique);
                stack.push(unique);
                return;
            }
        }
        if (stackPeek instanceof BaseKey) {
            if (qName.equals("selector")) {
                Selector selector = new Selector();
                selector.xpath = attributes.getValue("xpath");
                BaseKey key = (BaseKey) stackPeek;
                key.selector = selector;
                stack.push(selector);
                return;
            }
            if (qName.equals("field")) {
                Field field = new Field();
                field.xpath = attributes.getValue("xpath");
                BaseKey key = (BaseKey) stackPeek;
                key.field = field;
                stack.push(field);
                return;
            }
        }
        if (qName.equals("attribute") && stackPeek instanceof ComplexType) {
            ComplexType complexType = (ComplexType) stackPeek;
            Attribute attribute = attribute(attributes);
            complexType.attributes.add(attribute);
            stack.push(attribute);
            return;
        }
        if (qName.equals("attribute") && stackPeek instanceof AttributeGroup) {
            AttributeGroup group = (AttributeGroup) stackPeek;
            Attribute attribute = attribute(attributes);
            group.attributes.add(attribute);
            stack.push(attribute);
            return;
        }
        if (qName.equals("simpleType") && stackPeek instanceof Attribute) {
            SimpleType simpleType = new SimpleType();
            Attribute attribute = (Attribute) stackPeek;
            attribute.type = simpleType;
            stack.push(simpleType);
            return;
        }
        if (qName.equals("restriction") && stackPeek instanceof SimpleType) {
            Restriction restriction = new Restriction();
            SimpleType simpleType = (SimpleType) stackPeek;
            simpleType.restriction = restriction;

            String typeStr = attributes.getValue("base");
            typeStr = typeStr.trim();
            if (typeStr.contains(":")) {
                String[] s = typeStr.split(":");
                // String namespace = s[0];//TODO namespace
                String baseTypeStr = s[1];
                LinkedList<Restriction> restrictionList = restrictionMap
                        .get(baseTypeStr);

                if (restrictionList == null) {
                    restrictionList = new LinkedList<Restriction>();
                    restrictionMap.put(baseTypeStr, restrictionList);
                }
                restrictionList.add(restriction);
            } else {
                Type base = new Type();
                base.name = typeStr;
                restriction.base = base;
            }
            stack.push(restriction);
            return;
        }

        if (qName.equals("minLength") && stackPeek instanceof Restriction) {
            Restriction restriction = (Restriction) stackPeek;
            restriction.setMinLength(Integer.parseInt(attributes
                    .getValue("value")));

            stack.push(restriction);
            return;
        }
        if (qName.equals("maxLength") && stackPeek instanceof Restriction) {
            Restriction restriction = (Restriction) stackPeek;
            restriction.setMaxLength(Integer.parseInt(attributes
                    .getValue("value")));

            stack.push(restriction);
            return;
        }
        if (qName.equals("pattern") && stackPeek instanceof Restriction) {
            Restriction restriction = (Restriction) stackPeek;
            Pattern pattern = new Pattern();
            pattern.value = attributes.getValue("value");
            restriction.addPattern(pattern);
            stack.push(pattern);
            return;
        }
        if (qName.equals("enumeration") && stackPeek instanceof Restriction) {
            Restriction restriction = (Restriction) stackPeek;
            Enumeration enumeration = new Enumeration();
            enumeration.id = attributes.getValue("id");
            enumeration.value = attributes.getValue("value");
            restriction.enumerations.add(enumeration);
            stack.push(enumeration);
            return;
        }

        // XML Schema allows nesting of SimpleTypes and Restrictions
        if (qName.equals("simpleType") && stackPeek instanceof Restriction) {
            SimpleType simpleType = new SimpleType();
            Restriction restriction = (Restriction) stackPeek;
            restriction.simpleType = simpleType;
            stack.push(simpleType);
            return;
        }
        if (qName.equals("annotation") && stackPeek instanceof Annotatable) {
            Annotatable annotatable = (Annotatable) stackPeek;
            Annotation annotation = new Annotation();
            stack.push(annotation);
            annotatable.annotation = annotation;
            return;
        }
        if (qName.equals("documentation") && stackPeek instanceof Annotation) {
            Annotation annotation = (Annotation) stackPeek;
            Documentation documentation = new Documentation();
            annotation.documentation = documentation;
            // text retrieved in the 'characters' method
            stack.push(documentation);
            return;
        }
        if (qName.equals("appinfo") && stackPeek instanceof Annotation) {
            Annotation annotation = (Annotation) stackPeek;
            Appinfo info = new Appinfo();
            annotation.appinfo = info;
            stack.push(info);
            return;
        }
        if (stackPeek instanceof Appinfo) {
            Appinfo info = (Appinfo) stackPeek;
            AnyNode newNode = new AnyNode(qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                newNode.attributes.put(attributes.getQName(i), attributes
                        .getValue(i));
            }
            info.anyNodes.add(newNode);
            stack.push(newNode);
            return;
        }
        if (stackPeek instanceof AnyNode) {
            AnyNode node = (AnyNode) stackPeek;
            AnyNode newNode = new AnyNode(qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                newNode.attributes.put(attributes.getQName(i), attributes
                        .getValue(i));
            }
            node.anyNodes.add(newNode);
            stack.push(newNode);
            return;
        }

        stack.push(localName);
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

        stackPeek = stack.peek();

        if (stackPeek instanceof Documentation) {
            Documentation documentation = (Documentation) stackPeek;
            documentation.text += new String(ch, start, length);
            return;
        }

        if (stackPeek instanceof AnyNode) {
            AnyNode node = (AnyNode) stackPeek;
            node.text += new String(ch, start, length);
            return;
        }
    }

    Attribute attribute(Attributes attributes) {
        Attribute attribute = new Attribute();
        attribute.id = attributes.getValue("id");
        attribute.name = attributes.getValue("name").trim();
        attribute.type = new Type();
        String typeStr = attributes.getValue("type");
        if (typeStr != null) {
            attribute.type.name = typeStr.trim();
            typeStr = typeStr.trim();
            if (typeStr.contains(":")) {
                String[] s = typeStr.split(":");
                // String namespace = s[0];//TODO namespace
                String simpleTypeStr = s[1];
                LinkedList<Attribute> attrList = simpleTypeMap
                        .get(simpleTypeStr);
                if (attrList == null) {
                    attrList = new LinkedList<Attribute>();
                    simpleTypeMap.put(simpleTypeStr, attrList);
                }
                attrList.add(attribute);
            }
        }

        attribute.defaultValue = attributes.getValue("default");
        attribute.fixedValue = attributes.getValue("fixed");
        attribute.defaultValue = attributes.getValue("default");
        attribute.use = use(attributes.getValue("use"));
        return attribute;
    }

    public Element element(Attributes attributes) {
        Element element = new Element();
        String maxOccurs = attributes.getValue("maxOccurs");
        if (maxOccurs != null && !"unbounded".equals(maxOccurs))
            element.maxOccures = Integer.parseInt(maxOccurs);

        String minOccurs = attributes.getValue("minOccurs");
        if (minOccurs != null)
            element.minOccures = Integer.parseInt(minOccurs);

        element.id = attributes.getValue("id");
        element.name = attributes.getValue("name").trim();
        element.nillable = Boolean
                .parseBoolean(attributes.getValue("nillable"));
        String typeStr = attributes.getValue("type");
        if (typeStr != null) {
            typeStr = typeStr.trim();
            if (typeStr.contains(":")) {
                String[] s = typeStr.split(":");
                // String namespace = s[0];//TODO namespace
                String complexTypeStr = s[1];
                LinkedList<Element> elements = elementTypeMap
                        .get(complexTypeStr);
                if (elements == null) {
                    elements = new LinkedList<Element>();
                    elementTypeMap.put(complexTypeStr, elements);
                }
                elements.add(element);
            } else {
                Type type = new Type();
                type.name = typeStr;
                element.type = type;
            }
        }

        element.use = use(attributes.getValue("use"));
        return element;
    }

    public static Use use(String useStr) {
        if (useStr != null)
            if (useStr.equals("required"))
                return new Required();
            else if (useStr.equals("optional"))
                return new Optional();
            else if (useStr.equals("prohibited"))
                return new Prohibited();

        return null; // the default
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // endElement(http://www.w3.org/2001/XMLSchema, element, element
        // p("endElement(" + uri + ", " + localName + ", " + qName);
        stack.pop();
    }

    @Override
    public void endDocument() throws SAXException {
        // replace complex type (strings) with object references
        for (ComplexType complexType : schema.complexTypes) {
            LinkedList<Element> elements = elementTypeMap.get(complexType.name);
            if (elements != null)
                for (Element element : elements) {
                    element.type = complexType;
                }
        }
        for (SimpleType simpleType : schema.simpleTypes) {
            LinkedList<Attribute> attributeList = simpleTypeMap
                    .get(simpleType.name);

            if (attributeList != null)
                for (Attribute attribute : attributeList) {
                    if (simpleType.restriction != null)
                        attribute.type = simpleType;
                    // simpleType.list simpleType.union
                }

            LinkedList<Restriction> restrictionList = restrictionMap
                    .get(simpleType.getName());

            if (restrictionList != null)
                for (Restriction restriction : restrictionList) {
                    restriction.base = simpleType;
                }

        }
        for (AttributeGroup attributeGroup : schema.attributeGroups) {
            String name = attributeGroup.name;
            for (AttributeGroup groupRef : attributeGroupMap.get(name)) {
                groupRef.attributes = attributeGroup.attributes;
                groupRef.name = attributeGroup.name;
            }
        }
    }

    // @Override
    // public void endPrefixMapping(String prefix) throws SAXException {
    // // endPrefixMapping(
    // // endPrefixMapping(tns
    // // p("endPrefixMapping(" + prefix);
    // }

    // @Override
    // public void notationDecl(String name, String publicId, String systemId)
    // throws SAXException {
    // p("notationDecl(" + name + ", " + publicId + ", " + systemId);
    // }

    // @Override
    // public void processingInstruction(String target, String data)
    // throws SAXException {
    // p("processingInstruction(" + target + ", " + data);
    // }

    // @Override
    // public void setDocumentLocator(Locator locator) {
    // //setDocumentLocator(org.apache.xerces.parsers.AbstractSAXParser$LocatorProxy@18fef3d
    // p("setDocumentLocator(" + locator);
    // }

    // @Override
    // public InputSource resolveEntity(String publicId, String systemId)
    // throws IOException, SAXException {
    // p("resovledEntity" + publicId + "," + systemId);
    // return super.resolveEntity(publicId, systemId);
    // }

    public static String toStr(Attributes a) {
        String s = "Attributes(";
        int len = a.getLength();

        for (int i = 0; i < len; i++) {
            s += "|LocalName=" + a.getLocalName(i);
            s += ",QName=" + a.getQName(i);
            s += ",Type=" + a.getType(i);
            s += ",URI=" + a.getURI(i);
            s += ",Value=" + a.getValue(i);
        }
        return s + ")";
    }

    static void p(String s) {
        System.out.println(s);
    }

    /**
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException,
            ParserConfigurationException, SAXException {
        String fn = args[0];
        InputStream is = new FileInputStream(fn);
        InputSource src = new InputSource(is);
        XSDUnmarshal saxUms = new XSDUnmarshal(src,
                "org.apache.xerces.parsers.SAXParser");

        is.close();
        XmlSchema schema = saxUms.schema;
        p(schema.toString());
    }

}
