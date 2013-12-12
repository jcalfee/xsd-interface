<%@ tag isELIgnored="false" body-content="tagdependent"
    import="info.jcalfee.xsi.client.XmlSchema.Type,
  info.jcalfee.xsi.client.XmlSchema.SimpleType,
  info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction,
  info.jcalfee.xsi.client.XmlSchema.SimpleType.Restriction.Enumeration,
  info.jcalfee.xsi.client.XmlSchema.Entity, 
  info.jcalfee.xsi.client.XSD_Appinfo,
  java.util.*"%><%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="xsi" tagdir="/WEB-INF/tags"%>
<%@ attribute name="entity" required="true" type="java.lang.Object"%>
<%@ attribute name="databaseValue" required="true" type="java.lang.Object"%>
<%@ taglib prefix="f" uri="functions"%>
<%
    Entity entity = ((Entity)jspContext.findAttribute("entity"));//jstl parm problem with inner static class
    String defaultValue = entity.getDefaultValue();
    Type type = entity.getType();
    XSD_Appinfo appinfo = new XSD_Appinfo(entity);
    String attrParmValue = request.getParameter(entity.getName());
    Object databaseValue = jspContext.getAttribute("databaseValue");
    if("".equals(databaseValue))
        databaseValue = null;
    
    if(attrParmValue != null && !appinfo.isReadonly()) {
        jspContext.setAttribute("value", attrParmValue);
    } else if(databaseValue != null) {
        jspContext.setAttribute("value", databaseValue);
    } else if(defaultValue != null) {
        jspContext.setAttribute("value", defaultValue);
    }
    Restriction restriction = null;
    LinkedList<Enumeration> enumerations = null;
    if(type instanceof SimpleType) {
        SimpleType simpleType = (SimpleType)type;
        restriction = simpleType.getRestriction();
        if(restriction != null) {
            jspContext.setAttribute("maxLength", restriction.getMaxLength());
            jspContext.setAttribute("minLength", restriction.getMinLength());
            enumerations = restriction.getEnumerations();
        }
    }
    if("boolean".equals(type.getBaseName())) {
        //name.CheckboxSubmit is used to know if we can count on the absence
          //of a checkbox value and use that to represent false
          boolean checkboxSubmit = request.getParameter(entity.getIdOrName() + ".CheckboxSubmit") != null;
          if(!appinfo.isReadonly() && checkboxSubmit) {
              if(!"".equals(attrParmValue))
                  jspContext.setAttribute("checked", attrParmValue == null ? false : 
                      Boolean.parseBoolean(attrParmValue));
              
          } else if(databaseValue != null) {
              jspContext.setAttribute("checked", Boolean.parseBoolean(databaseValue.toString()));
          } else if(defaultValue != null) {
              jspContext.setAttribute("checked", 
                  "1".equals(defaultValue) || 
                  Boolean.parseBoolean(defaultValue));
          }
          
          if(appinfo.isReadonly()) {
              %><span class="xsi-value">${empty checked ? '' : (checked ? 'Yes' : 'No')}</span><%
          } else { %>
                  <input type="hidden" name="${entity.idOrName}.CheckboxSubmit" value=""/>
                  <input type="radio" id="${entity.idOrName}" name="${entity.idOrName}" value="true" <%
                  %>${checked ? 'checked' : ''}/>Yes&nbsp; 
                  <input type="radio" id="${entity.idOrName}" name="${entity.idOrName}" value="false" <%
                  %>${!checked ? 'checked' : '' }/>No&nbsp;<% 
              if(!entity.isRequired()) { %>             
                  <input type="radio" id="${entity.idOrName}" name="${entity.idOrName}" value="" ${
                    empty checked ? 'checked' : ' ' } ${
                    appinfo.readonly ? 'disabled' : ' '}/>Unspecified<% 
              }
          }
      } else {//not a boolean
        if(appinfo.isReadonly()) { %>
            <span class="xsi-value"><c:choose>
            <c:when test="${empty value}">&nbsp;</c:when>
            <c:otherwise><c:out value="${value}" escapeXml="true"/>
            </c:otherwise></c:choose></span><%
        } else { 
            if(enumerations == null || enumerations.size() == 0) {
        %>
                <input type="text" id="${entity.idOrName}" name="${entity.idOrName}" <%
                %>value="<c:out value="${f:format(entity, value)}" escapeXml="true"/>" <%
                %>maxLength="${maxLength}" minLength="${minLength}"/><%
            } else { %>
                <select id="${entity.idOrName}" name="${entity.idOrName}"> <%
                    for(Enumeration e : enumerations) {
                        jspContext.setAttribute("e", e);
                %>
                    <option value="${e.idOrValue}"${value == e.idOrValue ? ' selected' : ''}>${e}</option><%
                    }
                    %>
                </select><%
            }
        }
    }
%>