<%@ tag isELIgnored="false" body-content="scriptless"
 dynamic-attributes="true"%><%@
 attribute name="value" required="true" type="java.lang.Object"
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${!empty value.message}">
        <span class="${value.success ? 'xsi-response-success' : 'xsi-response-fail'}">
        ${value.message}</span>
    </c:when>
    <c:otherwise><span>&nbsp;</span></c:otherwise>
</c:choose>