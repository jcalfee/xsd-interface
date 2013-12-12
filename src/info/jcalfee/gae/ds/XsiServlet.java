package info.jcalfee.gae.ds;

import info.jcalfee.xsi.FunctionUtil;
import info.jcalfee.xsi.XSDUnmarshal;
import info.jcalfee.xsi.client.XmlSchema;
import info.jcalfee.xsi.client.XmlSchemaInstance;
import info.jcalfee.xsi.client.XmlSchema.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class XsiServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(XsiServlet.class.getName());

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        XmlSchemaInstance schema = new XmlSchemaInstance();
        user(schema);
        String cmd = schema(req, schema);
        if (cmd == null)
            cmd = "index";

        // create new object
        int size = schema.hierarchy.size();
        if (size == -1)
            log.log(Level.WARNING, "Path does not match schema");

        schema = schema.hierarchy.get(size - 1);
        if (req.getParameter("delete") != null) {
            setKey(schema, req);
            DataStore.delete(schema);
        } else if (req.getParameter("delete_checked") != null) {
            LinkedList<Long> idValues = getIdValues(req, "_checked");
            DataStore.delete(schema, idValues);
        } else {
            setKey(schema, req);

            HashMap<String, String[]> parameters =
                toHashMap(req.getParameterMap());
            
            if (Validation.update(schema, parameters))
                DataStore.save(schema);
            else
                //temp, until olc is a callback to datastore save
                Validation.optimisticLocking(schema, parameters);
        }
        forwardJsp(cmd, req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        XmlSchemaInstance xsi = new XmlSchemaInstance();
        user(xsi);
        String cmd = schema(req, xsi);
        if (cmd == null)
            cmd = "index";

        if (xsi.schema != null && xsi.hierarchy.size() > 0) {
            xsi = xsi.hierarchy.get(xsi.hierarchy.size() - 1);
            setKey(xsi, req);
            DataStore.load(xsi);
        }
        forwardJsp(cmd, req, resp);
    }

    /**
     * Convert Map to HashMap. A HashMap has better compile size when compiled
     * with GWT.
     * 
     * @return
     */
    public static HashMap<String, String[]> toHashMap(
        Map<String, String[]> parameterMap) {
        if (parameterMap instanceof HashMap<?, ?>)
            return (HashMap<String, String[]>) parameterMap;
        else {
            HashMap<String, String[]> parameters =
                new HashMap<String, String[]>();
            for (String key : parameterMap.keySet()) {
                parameters.put(key, parameterMap.get(key));
            }
            return parameters;
        }
    }

    public static LinkedList<Long> getIdValues(HttpServletRequest req,
        String endsWith) {
        LinkedList<Long> ids = new LinkedList<Long>();
        Enumeration<?> en = req.getParameterNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            int flagLen = endsWith.length();
            if (paramName.endsWith(endsWith) && paramName.length() > flagLen) {
                try {
                    long id =
                        Long.parseLong(paramName.substring(0, paramName
                            .length()
                            - flagLen));
                    ids.add(id);
                } catch (NumberFormatException e) {
                }
            }
        }
        return ids;
    }

    public static void fatal(Throwable e) {
        e.printStackTrace();
    }

    void user(XmlSchemaInstance xsi) {
        // maybe optimize by using session
        User user = UserStore.findCurrentUser();
        xsi.user = user.toClientUser();
    }

    /**
     * Parse the request URL, load XSD file, populate instance hierarchy with
     * elements in URL order with IDs (if present).
     * 
     * @param req
     * @param xsi
     * @return unused part of the URL at the end, that is the command
     * @throws IOException
     */
    public String schema(HttpServletRequest req, XmlSchemaInstance xsi)
        throws IOException {
        String uri = req.getRequestURI();
        if (uri.startsWith("/WEB-INF"))
            return null;

        String[] path = uri.split("/");
        if (path.length < 2)
            return null;

        String schemaName = path[1];
        String xsdPath = "/WEB-INF/schema/" + schemaName + ".xsd";
        InputStream in = getServletContext().getResourceAsStream(xsdPath);
        XmlSchema schema = null;
        if (in != null)
            schema =
                new XSDUnmarshal(in, "org.apache.xerces.parsers.SAXParser").schema;

        if (schema == null)
            return uri;

        xsi.schema = schema;
        xsi.name = schemaName;
        req.setAttribute("xsi", xsi);
        String cmd = null;
        int c = 2;
        Element parentElement = null;
        // Walk the path building schema.hierarchy. The first miss starts
        // the beginning of our command path to a JSP page.
        while (c < path.length) {
            String pathStr = path[c];
            String idStr = "";

            // Take the Id out of a path element like: User(14)
            int idIndex = pathStr.indexOf('(');
            if (idIndex != -1 && idIndex != 0) {
                int idIndexEnd = pathStr.indexOf(')');
                if (idIndexEnd != -1 && idIndexEnd > idIndex + 1) {
                    // 14
                    idStr = pathStr.substring(idIndex + 1, idIndexEnd);
                }
                // User
                pathStr = pathStr.substring(0, idIndex);
            }
            Element element = xsi.find(pathStr, parentElement);
            if (element == null) {
                cmd = FunctionUtil.join(path, c, "/");
                break;
            }
            xsi = xsi.add(element, true);

            // primary key (object id) for this element
            try {
                // make sure it is a number
                Integer.parseInt(idStr);
                xsi.elementKey = idStr;
            } catch (NumberFormatException e) {
                // let c go onto the next iteration
            }

            parentElement = element;
            c++;
        }

        // preserve the optimistic locking version across requests
        String version = req.getParameter("xsi_version");
        xsi.values.put("xsi_version", version);
        return cmd;
    }

    public void setKey(XmlSchemaInstance schema, HttpServletRequest req) {
        try {
            // if the keyStr is null, the schema and element is un-effected
            String keyStr = req.getParameter("id");
            if ("".equals(keyStr))
                schema.elementKey = null;
            else {
                // if keyStr is null, will be ignored
                Integer.parseInt(keyStr);
                schema.elementKey = keyStr;
            }
        } catch (NumberFormatException e) {
        }
    }

    public void forwardJsp(String cmd, HttpServletRequest req,
        HttpServletResponse resp) throws IOException {
        RequestDispatcher disp =
            getServletContext().getRequestDispatcher(
                "/WEB-INF/jsp/" + cmd + ".jsp");

        try {
            disp.forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

}
