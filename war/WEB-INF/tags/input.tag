<%@ tag isELIgnored="false" body-content="scriptless"
 dynamic-attributes="true"%><%@
 attribute name="xsi" required="true" type="java.lang.Object"
%><%@ taglib prefix="xsi" tagdir="/WEB-INF/tags"%>
<%@ attribute name="entity" required="true" type="java.lang.Object"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="functions"%>
<tr>
<td class="label">
    <xsi:label entity="${entity}"/></td>
<td>
    <xsi:formInput entity="${entity}"
        databaseValue="${param['edit'] == null ? '' : xsi.values[entity.name]}" />
    <%/*<xsi response entityName="${entity.name}" responseData="${xsi.response }
     and xsi.response.data[entity.name] !=  param[entity.name] ???
    */%>
    <c:if test="${xsi.response.data != null}">
    ${f:showNonNull('<br><div class="xsi-response-fail">', xsi.response.data[entity.name], '</div>')}
    </c:if>
    <xsi:documentation entity="${entity}"/>
</td>
</tr>