package info.jcalfee.xsi.client;

import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Documentation;
import info.jcalfee.xsi.client.XmlSchema.Use.Optional;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * SFr - Schema Framework
 * 
 * @author james
 * 
 */
public class XmlSchema {

    public HashMap<String, String> namespaceMap = new HashMap<String, String>();

    public LinkedList<Element> elements = new LinkedList<Element>();

    public LinkedList<ComplexType> complexTypes = new LinkedList<ComplexType>();

    public LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();

    public LinkedList<AttributeGroup> attributeGroups =
        new LinkedList<AttributeGroup>();

    public static class Annotatable {

        public Annotation annotation = null;

        public static class Annotation {

            public Appinfo appinfo = null;

            public Documentation documentation = null;

            public static class Appinfo {

                public LinkedList<AnyNode> anyNodes = new LinkedList<AnyNode>();

                public static class AnyNode {

                    public String qName;

                    public String text = "";

                    public HashMap<String, String> attributes =
                        new HashMap<String, String>();

                    public LinkedList<AnyNode> anyNodes =
                        new LinkedList<AnyNode>();

                    public AnyNode(String qName) {
                        this.qName = qName;
                    }

                    public String getName() {
                        return qName;
                    }

                    public String getText() {
                        return text;
                    }

                    public String toString() {
                        return text;
                    }

                    public HashMap<String, String> getAttributes() {
                        return attributes;
                    }

                    public LinkedList<AnyNode> getAnyNodes() {
                        return anyNodes;
                    }

                }

                public LinkedList<AnyNode> getAnyNodes() {
                    return anyNodes;
                }

            }

            public static class Documentation {
                public String text = "";

                public String getText() {
                    return text;
                }

                public String toString() {
                    return text;
                }

            }

            public Appinfo getAppinfo() {
                return appinfo;
            }

            public Documentation getDocumentation() {
                return documentation;
            }

        }

        public Annotation getAnnotation() {
            return annotation;
        }
    }

    public static class Type extends Annotatable {
        public String name = "anySimpleType";// string,boolean, etc..

        public String getName() {
            return name;
        }

        public String getBaseName() {
            return name;
        }

    }

    public static class SimpleType extends Type {

        public Restriction restriction;

        public static class Restriction extends Annotatable {

            public Type base;

            public SimpleType simpleType;

            private Integer minLength;

            private Integer maxLength;

            public LinkedList<LinkedList<Pattern>> patterns =
                new LinkedList<LinkedList<Pattern>>();

            public Restriction() {
                patterns.add(new LinkedList<Pattern>());
            }

            public LinkedList<Enumeration> enumerations =
                new LinkedList<Enumeration>();

            public static class Pattern extends Annotatable {
                public String value;

                public String getValue() {
                    return value;
                }

                public String toString() {
                    return value;
                }
            }

            public static class Enumeration extends Annotatable {

                public String id;

                public String value;

                public String getId() {
                    return id;
                }

                public String getValue() {
                    return value;
                }

                public String getIdOrValue() {
                    return id == null ? value : id;
                }

                public String toString() {
                    return value == null ? id : value;
                }

            }

            public Type getBase() {
                return base;
            }

            public Integer getMinLength() {
                if (minLength != null)
                    return minLength;

                if (base instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) base;
                    if (simpleType.restriction != null)
                        return simpleType.restriction.getMinLength();
                }

                return null;
            }

            public void setMinLength(Integer minLength) {
                this.minLength = minLength;
            }

            public Integer getMaxLength() {
                if (maxLength != null)
                    return maxLength;

                if (base instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) base;
                    if (simpleType.restriction != null)
                        return simpleType.restriction.getMaxLength();
                }

                return null;
            }

            public void setMaxLength(Integer maxLength) {
                this.maxLength = maxLength;
            }

            public SimpleType getSimpleType() {
                return simpleType;
            }

            public LinkedList<Enumeration> getEnumerations() {
                return enumerations;
            }

            /**
             * The outer list of patterns make up the AND conditions, then inner
             * list make up the OR conditions. The supports dynamic updates to
             * the XML Schema.
             * 
             * @return
             */
            public LinkedList<LinkedList<Pattern>> getPatterns() {
                LinkedList<Pattern> localPatterns = patterns.get(0);
                patterns.clear();// support dynamic updates to the model
                patterns.add(localPatterns);
                if (base instanceof SimpleType) {
                    SimpleType simpleType = (SimpleType) base;
                    if (simpleType.restriction != null) {
                        for (LinkedList<Pattern> patternList : simpleType.restriction
                            .getPatterns()) {
                            patterns.add(patternList);
                        }
                    }
                }
                return patterns;
            }

            public void addPattern(Pattern pattern) {
                patterns.get(0).add(pattern);
            }

        }

        /**
         */
        public Restriction getRestriction() {
            return restriction;
            // Restriction collectiveR = new Restriction();
            // for(Restriction restriction : getAllRestrictions()) {
            // if(collectiveR.maxLength == null && restriction.maxLength !=
            // null)
            // collectiveR.maxLength = restriction.maxLength;
            //                
            // if(collectiveR.minLength == null && restriction.minLength !=
            // null)
            // collectiveR.minLength = restriction.minLength;
            //                
            // if(!restriction.enumerations.isEmpty())
            // collectiveR.enumerations = restriction.enumerations;
            //                
            // if(restriction.annotation)
            // }
        }

        public LinkedList<Restriction> getAllRestrictions() {
            LinkedList<Restriction> restrictions =
                new LinkedList<Restriction>();

            if (restriction != null)
                restrictions.add(restriction);

            if (restriction.base instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) restriction.base;
                if (simpleType.restriction != null)
                    restrictions.addAll(simpleType.getAllRestrictions());
            }
            return restrictions;
        }

        public String getBaseName() {
            if (restriction != null)
                return restriction.base.getName();

            return super.getBaseName();
        }

    }

    public static class ComplexType extends Type {

        public Boolean mixed = Boolean.FALSE;

        public Sequence sequence;

        public LinkedList<Attribute> attributes = new LinkedList<Attribute>();

        public LinkedList<AttributeGroup> attributeGroups =
            new LinkedList<AttributeGroup>();

        public static class Sequence extends Annotatable {
            public LinkedList<Element> elements = new LinkedList<Element>();

            public LinkedList<Element> getElements() {
                return elements;
            }

        }

        public Boolean getMixed() {
            return mixed;
        }

        public Sequence getSequence() {
            return sequence;
        }

        /**
         * Return all attributes and attributes in attribute groups
         * 
         * @return
         */
        public LinkedList<Attribute> getAllAttributes() {
            LinkedList<Attribute> newAttributes = new LinkedList<Attribute>();
            newAttributes.addAll(attributes);
            for (AttributeGroup group : attributeGroups)
                newAttributes.addAll(group.attributes);

            return newAttributes;
        }

        /**
         * See getAllAttributes
         * 
         * @return
         */
        public LinkedList<Attribute> getAttributes() {
            return attributes;
        }

        public LinkedList<AttributeGroup> getAttributeGroups() {
            return attributeGroups;
        }

    }

    /**
     * Attributes common to both element and attribute.
     * 
     * @author jcalfee
     */
    public abstract static class Entity extends Annotatable {
        public String id;

        public String name;

        public Type type;

        public String defaultValue;

        public String fixedValue;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIdOrName() {
            if (id != null)
                return id;

            return name;
        }

        public Type getType() {
            return type;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getFixedValue() {
            return fixedValue;
        }

        abstract public boolean isRequired();

    }

    public static class Element extends Entity {

        public Integer maxOccures = 1;

        public Integer minOccures = 1;

        public Boolean nillable = Boolean.FALSE;

        public Use use = new Optional();

        public LinkedList<Key> keys = new LinkedList<Key>();

        public LinkedList<KeyRef> keyRefs = new LinkedList<KeyRef>();

        public LinkedList<Unique> unique = new LinkedList<Unique>();

        public static class BaseKey extends Annotatable {

            public String name;

            public Selector selector;

            public Field field;

            private static class XPath extends Annotatable {
                @SuppressWarnings("unused")
                public String xpath;

                // public String getXpath() {
                // return xpath;
                // }
            }

            public static class Selector extends XPath {
            };

            public static class Field extends XPath {
            };

            public String getName() {
                return name;
            }

            public Selector getSelector() {
                return selector;
            }

            public Field getField() {
                return field;
            }

        }

        public static class Key extends BaseKey {
        };

        public static class KeyRef extends BaseKey {

            public String refer;

            public String getRefer() {
                return refer;
            }

        };

        public static class Unique extends BaseKey {
        };

        public String getName() {
            return name;
        }

        public String getIdOrName() {
            if (id != null)
                return id;

            return name;
        }

        public Type getType() {
            return type;
        }

        public Boolean getNillable() {
            return nillable;
        }

        public Integer getMaxOccures() {
            return maxOccures;
        }

        public Integer getMinOccures() {
            return minOccures;
        }

        public Use getUse() {
            return use;
        }

        @Override
        public boolean isRequired() {
            return !nillable;
        }

        /**
         * May be used by the data layer in a message to the client.
         */
        @Override
        public String toString() {
            return name;
        }

        public LinkedList<Element> getChildren() {
            if (!(type instanceof ComplexType))
                return new LinkedList<Element>();

            ComplexType complexType = (ComplexType) type;
            if (complexType.sequence == null)
                return new LinkedList<Element>();

            return complexType.sequence.elements;
        }

        public Element getChild(String name) {
            for (Element child : getChildren()) {
                if (child.name.equals(name))
                    return child;
            }
            return null;
        }

        public LinkedList<Attribute> getAttributes() {
            if (!(type instanceof ComplexType))
                return new LinkedList<Attribute>();

            ComplexType complexType = (ComplexType) type;
            return complexType.attributes;
        }

        public LinkedList<Attribute> getAllAttributes() {
            if (!(type instanceof ComplexType))
                return new LinkedList<Attribute>();

            ComplexType complexType = (ComplexType) type;
            return complexType.getAllAttributes();
        }

        public LinkedList<AttributeGroup> getAttributeGroups() {
            if (!(type instanceof ComplexType))
                return new LinkedList<AttributeGroup>();

            ComplexType complexType = (ComplexType) type;
            return complexType.getAttributeGroups();
        }
        
        public LinkedList<Element> getElements() {
            if (!(type instanceof ComplexType))
                return new LinkedList<Element>();

            ComplexType complexType = (ComplexType) type;
            if(complexType.sequence == null)
                return new LinkedList<Element>();
            
            return complexType.sequence.elements;
        }

        public String getId() {
            return id;
        }

        public LinkedList<Key> getKeys() {
            return keys;
        }

        public LinkedList<KeyRef> getKeyRefs() {
            return keyRefs;
        }

        public LinkedList<Unique> getUnique() {
            return unique;
        }

        public boolean isMultiLine() {
            return "string".equals(type.getBaseName());
        }
    }

    public static class AttributeGroup {

        public String name;

        public LinkedList<Attribute> attributes = new LinkedList<Attribute>();

        public String getName() {
            return name;
        }

        public LinkedList<Attribute> getAttributes() {
            return attributes;
        }

    }

    public abstract static class Use {
        public static class Required extends Use {
            public String toString() {
                return "required";
            }
        }

        public static class Prohibited extends Use {
            public String toString() {
                return "prohibited";
            }
        }

        public static class Optional extends Use {
            public String toString() {
                return "optional";
            }
        }

    }

    public static class Attribute extends Entity {

        public Use use = new Use.Optional();

        public Use getUse() {
            return use;
        }

        @Override
        public boolean isRequired() {
            return use instanceof Use.Required || defaultValue != null;
        }

        @Override
        public Annotation getAnnotation() {
            Annotation a = super.getAnnotation();
            if (a == null)
                a = type.getAnnotation();

            return a;
        }

        /**
         * @return local documentation or type's documentation
         */
        public Documentation getDocumentation() {
            if (annotation != null && annotation.documentation != null)
                return annotation.documentation;

            if (type != null && type.annotation != null
                && type.annotation.documentation != null)
                return type.annotation.documentation;

            return null;
        }

    }

    public HashMap<String, String> getNamespaceMap() {
        return namespaceMap;
    }

    public LinkedList<Element> getElements() {
        return elements;
    }

    public LinkedList<ComplexType> getComplexTypes() {
        return complexTypes;
    }

    public LinkedList<SimpleType> getSimpleTypes() {
        return simpleTypes;
    }

    public LinkedList<AttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

}
