package info.jcalfee.xsi;

import info.jcalfee.gae.ds.Validation;
import info.jcalfee.xsi.client.XmlSchema.Entity;
import info.jcalfee.xsi.client.XmlSchema.Type;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class FunctionUtil {

    public static int size(Collection<?> c) {
        return c.size();
    }

    private final static String SPACE_TRIGGER =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String format(Object entity, Object value) {
        if (value == null || value.toString().trim().equals(""))
            return "";

        Type type = ((Entity) entity).getType();
        String baseType = type.getBaseName();
        if ("date".equals(baseType)) {
            Date date = null;
            if (value instanceof Date)
                date = (Date) value;
            else {
                date = (Date) Validation.toBaseType(baseType, value.toString());
            }
            try {
                try {
                    return Validation.simpleDateFormat.format(date);
                } catch (Error e) {
                    return value.toString();
                }
            } catch (Error e) {
                return value.toString();
            }
        }
        if ("time".equals(baseType)) {
            Date date = null;
            if (value instanceof Date)
                date = (Date) value;
            else {
                try {
                    date =
                        (Date) Validation
                            .toBaseType(baseType, value.toString());
                } catch (Error e) {
                    return value.toString();
                }
            }
            try {
                return Validation.simpleTimeFormats[0].format(date);
            } catch (Error e) {
                return value.toString();
            }
        }
        return value.toString();
    }

    public static String space(String str) {
        if (str.length() == 0)
            return str;

        String str2 = String.valueOf(str.charAt(0)).toUpperCase();
        int strLength = str.length();
        for (int i = 1; i < strLength; i++) {
            char ch = str.charAt(i);
            char prevCh = str.charAt(i - 1);
            if (" ".equals(prevCh)) {
                str2 += Character.toUpperCase(ch);
                continue;
            }
            if (ch == '_' || ch == '-') {
                i++;
                if (i < strLength) {
                    str2 += ' ';
                    str2 += Character.toUpperCase(str.charAt(i));
                }
            } else if (SPACE_TRIGGER.indexOf(str.charAt(i)) > -1
                && SPACE_TRIGGER.indexOf(prevCh) == -1)
                str2 += " " + ch;
            else
                str2 += ch;
        }
        return str2;
    }

    public static String join(String[] s, int startIndex, String delimiter) {
        String str = "";
        for (int i = startIndex; i < s.length; i++) {
            str += s[i];
            if (i + 1 < s.length)
                str += delimiter;
        }
        return str;
    }

    public static String joinList(int startIndex, LinkedList<?> list,
        String delimiter) {
        String str = "";
        int len = list.size();
        for (int i = startIndex; i < len; i++) {
            if (i + 1 == len)
                str += list.get(i);
            else
                str += list.get(i) + delimiter;
        }
        return str;
    }

    public static String showNonNull(String s1, String s2, String s3) {
        if (s1 == null || s2 == null || s3 == null)
            return "";

        return s1 + s2 + s3;
    }

    static void p(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        p(space("Email"));
        p(space("email"));
        p(space("eMail"));
        p(space("SMS"));

    }
}
