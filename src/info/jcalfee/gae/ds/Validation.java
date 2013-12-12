package info.jcalfee.gae.ds;

import info.jcalfee.xsi.FunctionUtil;
import info.jcalfee.xsi.client.DirectResponse;
import info.jcalfee.xsi.client.XSD_Appinfo;
import info.jcalfee.xsi.client.XmlSchemaInstance;
import info.jcalfee.xsi.client.XmlSchema.Attribute;
import info.jcalfee.xsi.client.XmlSchema.Element;
import info.jcalfee.xsi.client.XmlSchema.SimpleType;
import info.jcalfee.xsi.client.XmlSchema.Type;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Documentation;
import info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction;
import info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction.Enumeration;
import info.jcalfee.xsi.client.XmlSchema.Use.Prohibited;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Validation {

    /**
     * <b>True</b> diverges from the xml schema behavior, but this is the
     * expected behavior.
     * 
     */
    static boolean EMPTY_STRING_IS_NULL = true;

    static SimpleDateFormat ISO8601Local =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat[] simpleTimeFormats =
        { new SimpleDateFormat("hh:mm:ss a"), new SimpleDateFormat("hh:mm:ss"),
            new SimpleDateFormat("hh:mm a"), new SimpleDateFormat("hh:mm") };

    public static void optimisticLocking(XmlSchemaInstance schema,
        HashMap<String, String[]> parameters) {
        String[] version = parameters.get("xsi_version");
        if (version != null) {
            schema.values.put("xsi_version", version[0]);
        }
        /*
         * if (version == null || !xsiVersion.toString().equals(version[0])) {
         * response.message = element + " has already been updated.";
         * response.success = false; // give the new entity to the client so it
         * can merge the // changes HashMap<String, Object> oldMap = new
         * HashMap<String, Object>(); oldMap.putAll(oldProperties);
         * oldMap.put("xsi_version", xsiVersion); response.data = oldMap;
         * schema.response = response; return; } xsiVersion =
         * xsiVersion.longValue() + 1; schema.values.put("xsi_version",
         * xsiVersion);
         */
    }

    /**
     * Copies and validates parameters storing converted data types in
     * schema.values. If there is an error, Schema.resonse is updated with the
     * new response object and any errors contained within.
     * 
     * <br>
     * Calls toBaseType to convert parameter strings into their proper data
     * object.
     * 
     * @param schema
     *            (update schema.values and if failed schema.response)
     * @param parameters
     *            input
     * @return success
     */
    public static boolean update(XmlSchemaInstance schema,
        HashMap<String, String[]> parameters) {

        Element element = schema.element;
        DirectResponse response = new DirectResponse();

        for (Attribute attribute : element.getAllAttributes()) {
            String attributeId = attribute.getIdOrName();
            String value = null;
            {
                String[] values = parameters.get(attributeId);
                if (values != null && values.length != 1) {
                    fail(response, attributeId,
                        "More than one value submitted for " + attribute.name);

                    log(element, attribute,
                        "Client submitted more than one value.");
                    continue;
                }
                value = values == null ? null : values[0];
            }

            if (EMPTY_STRING_IS_NULL && "".equals(value)) {
                value = null;
            }

            XSD_Appinfo appinfo = new XSD_Appinfo(attribute);
            if (value == null) {
                if (appinfo.isReadonly())
                    continue;
                // gets confused by unspecified non required booleans
                // if ("boolean".equals(attribute.getType().name))
                // // support for a check-box boolean
                // value = "false";
                // else {
                if (attribute.isRequired())
                    fail(response, attributeId, "This is required.");

                // }
            }

            if (appinfo.isReadonly()) {
                log(element, attribute,
                    "Client trying to change read-only field");

                fail(response, attributeId, "Can not change read-only field.");
                continue;
            }

            if (attribute.getUse() instanceof Prohibited) {
                fail(response, attributeId,
                    "Use of this attribute is prohibited");

                log(element, attribute, "Use of a prohibitied attribute.");
            }

            String fixedValue = attribute.getFixedValue();
            if (fixedValue != null) {
                if (!fixedValue.equals(value)) {
                    fail(response, attributeId, "This must be a fixed value: "
                        + fixedValue);

                    continue;
                }
            }

            Type attributeType = attribute.getType();
            String baseType = attributeType.getBaseName();
            if (attributeType instanceof SimpleType) {
                SimpleType simpleType = (SimpleType) attributeType;
                for (Restriction restriction : simpleType.getAllRestrictions()) {
                    Integer maxLength = restriction.getMaxLength();
                    if (maxLength != null && value != null
                        && value.length() > maxLength) {
                        int diff = value.length() - maxLength;
                        if (diff == 1)
                            fail(response, attributeId,
                                "This field is 1 character too long.");
                        else
                            fail(response, attributeId, "This field is " + diff
                                + " characters too long.");

                        continue;
                    }

                    Integer minLength = restriction.getMinLength();
                    if (minLength != null
                        && (value == null || value.length() < minLength)) {

                        int diff = minLength;
                        if (value != null)
                            diff = minLength - value.length();

                        if (diff == 1)
                            fail(response, attributeId,
                                "This field is 1 character too short.");
                        else
                            fail(response, attributeId, "This field is " + diff
                                + " characters too short.");

                        continue;
                    }

                    boolean match = true;
                    if (value != null) {
                        for (Enumeration e : restriction.enumerations) {
                            match = value.equals(e.getIdOrValue());
                            if (match)
                                break;
                        }
                        if (!match) {
                            fail(response, attributeId,
                                "This field did not one of the items in the list.");

                            continue;
                        }
                    }

                    if (value != null) {
                        patterns: for (LinkedList<Restriction.Pattern> andRestrictions : restriction.patterns) {
                            // list could be empty
                            boolean patternMatch = true;
                            for (Restriction.Pattern orRestriction : andRestrictions) {

                                Pattern pattern = pattern(orRestriction.value);

                                if (pattern.matcher(value).find())
                                    break;// OR conditions, only 1 needs to
                                // match

                                patternMatch = false;

                            }
                            if (!patternMatch) {
                                Documentation d = attribute.getDocumentation();
                                if (d != null)
                                    fail(response, attributeId, d.text);
                                else
                                    fail(response, attributeId,
                                        "This field is formatted incorrectly."
                                            + debugInfoList(
                                                "Regular Expression",
                                                andRestrictions));

                                break patterns;

                            }
                        }
                    }
                }

            }

            try {
                Object objValue = toBaseType(baseType, value);
                schema.values.put(attributeId, objValue);
            } catch (Error e) {
                fail(response, attributeId, e.getMessage());
                continue;
            }
        }

        if (!response.success) {
            // send the response back to the client
            schema.response = response;
            int errCount = response.data.size();
            if (errCount == 1)
                response.message = "There was 1 error";
            else
                response.message = "There were " + errCount + " errors";

        }
        return response.success;
    }

    public static String debugInfoList(String prefix, LinkedList<?> list) {
        return "<span class='xsi-validation-debug'>" + prefix + " "
            + FunctionUtil.joinList(0, list, "<br>") + "</span>";
    }

    public static Object toBaseType(String baseType, String value) throws Error {
        // http://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding#Default_data_type_bindings

        if (value == null)
            return null;

        if (baseType.equals("string") || baseType.equals("anySimpleType"))
            return value;

        value = value.trim();

        if (baseType.equals("anyURI")) {
            // http://www.w3.org/TR/xmlschema-2/#anyURI
            return value;// no validation
        }

        if (baseType.equals("boolean")) {
            if ("true".equals(value) || "1".equals(value))
                return true;

            if ("false".equals(value) || "0".equals(value))
                return false;

            throw new Error(
                "This value should be either: true (1) or false (0)");
        }

        if (baseType.equals("byte")) {
            try {
                return new Byte(value);
            } catch (NumberFormatException e) {
                throw new Error("This byte must have a value between: "
                    + "-127 and 127");
            }
        }

        if (baseType.equals("date")) {
            try {
                return simpleDateFormat.parse(value);
            } catch (ParseException e) {
                Calendar cal = Calendar.getInstance();
                throw new Error("A valid date "
                    + "should look something like this: "
                    + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH)
                    + "-" + cal.get(Calendar.DAY_OF_MONTH));
            }
        }

        if (baseType.equals("time")) {
            return time(value);
        }

        if (baseType.equals("dateTime")) {
            try {
                return ISO8601Local.parse(value);
            } catch (ParseException e) {
                throw new Error("A valid date and time value "
                    + "should look something like this: 2002-05-30T09:00:00");
            }
        }

        if (baseType.equals("decimal")) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid decimal type "
                    + "(positive, negative, whole or floating point).");
            }
        }

        if (baseType.equals("unsignedLong")) {
            try {
                BigDecimal d = new BigDecimal(value);
                if (d.compareTo(BigDecimal.ZERO) >= 0
                    && d.compareTo(new BigDecimal(Long.MAX_VALUE * 2 + 1)) <= 0)

                    return d;

                throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid unsigned long "
                    + "(positive whole number).");
            }
        }

        if (baseType.equals("double")) {
            try {
                return new Double(value);
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid double type "
                    + "(positive, negative, whole or floating point).");
            }
        }

        // if (baseType.equals("duration")) {
        // try {
        // return new Duration(value);
        // } catch (NumberFormatException e) {
        // throw new Error("This must be any valid double type "
        // + "(positive, negative, whole or floating point).");
        // }
        // }

        if (baseType.equals("float")) {
            try {
                return new Float(value);
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid float type "
                    + "(positive, negative, whole or floating point).");
            }
        }

        if (baseType.equals("int") || baseType.equals("integer")) {
            try {
                return new Integer(value);
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid int type "
                    + "(positive or negative whole number).");
            }
        }

        if (baseType.equals("unsignedShort")) {
            try {
                Integer i = new Integer(value);
                if (i >= 0 && i <= Short.MAX_VALUE * 2 + 1)
                    return i;

                throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new Error("This must be an unsigned short "
                    + "(short positive whole number).");
            }
        }

        if (baseType.equals("negativeInteger")) {
            try {
                Integer i = new Integer(value);
                if (i < 0)
                    return i;

                throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new Error("This must be a negative integer "
                    + "(whole number, not zero).");
            }
        }

        if (baseType.equals("nonNegativeInteger")) {
            try {
                Integer i = new Integer(value);
                if (i >= 0)
                    return i;

                throw new NumberFormatException();

            } catch (NumberFormatException e) {
                throw new Error("This must be a positive integer "
                    + "(whole number or zero).");
            }
        }

        if (baseType.equals("positiveInteger")) {
            try {
                BigInteger i = new BigInteger(value);
                if (i.compareTo(BigInteger.ZERO) > 0)
                    return i;

                throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new Error("This must be a positive integer "
                    + "(whole number, not zero).");
            }
        }

        if (baseType.equals("nonPositiveInteger")) {
            try {
                Integer i = new Integer(value);
                if (i <= 0)
                    return i;

                throw new NumberFormatException();

            } catch (NumberFormatException e) {
                throw new Error("This must be a negative integer "
                    + "(whole number or zero).");
            }
        }

        if (baseType.equals("long")) {
            try {
                return new Long(value);
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid long type "
                    + "(positive or negative whole number).");
            }
        }

        if (baseType.equals("unsignedInt")) {
            try {
                Long l = new Long(value);
                if (l >= 0 && l <= Integer.MAX_VALUE * 2 + 1)
                    return l;

                throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new Error("This must be any an unsigned int "
                    + "(positive whole number).");
            }
        }

        if (baseType.equals("short")) {
            try {
                return new Short(value);
            } catch (NumberFormatException e) {
                throw new Error("This must be any valid short type "
                    + "(short positive or negative whole number).");
            }
        }

        if (baseType.equals("unsignedByte")) {
            try {
                Short s = new Short(value);
                if (s >= 0 && s <= Byte.MAX_VALUE * 2 + 1)
                    return s;

                throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new Error("This must be an unsigned byte "
                    + "(small positive whole number).");
            }
        }

        return value;
    }

    public static Date parseDate(SimpleDateFormat[] formatters, String value)
        throws ParseException {
        ParseException pe = null;
        for (int i = 0; i < formatters.length; i++) {
            try {
                SimpleDateFormat fmt = formatters[i]; 
                fmt.setLenient(false);
                Date date = fmt.parse(value);
                //fmt.format(date);
                return date;
            } catch (ParseException e) {
                pe = e;
            }
        }
        throw pe;
    }

    public static Date time(String value) {
        try {
            return parseDate(simpleTimeFormats, value);
        } catch (ParseException e) {
            Calendar cal = Calendar.getInstance();
            throw new Error("A valid time "
                + "should look something like this: "
                + simpleTimeFormats[0].format(cal.getTime()));
        }
    }

    static HashMap<String, Pattern> patternMap = new HashMap<String, Pattern>();

    /**
     * @param regex
     * @return cached and compiled pattern
     */
    static Pattern pattern(String regex) {
        regex = '^' + regex + '$';
        Pattern p = patternMap.get(regex);
        if (p != null)
            return p;

        p = Pattern.compile(regex);
        patternMap.put(regex, p);
        return p;
    }

    static void fail(DirectResponse response, String attributeId, String message) {
        response.success = false;
        response.data.put(attributeId, message);
    }

    static void log(Element element, Attribute attribute, String message) {

    }
}
