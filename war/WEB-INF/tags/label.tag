<%@ tag isELIgnored="false" body-content="scriptless"
 dynamic-attributes="true"%><%@
 attribute name="entity" required="true" type="java.lang.Object"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="functions"%>
<label for="${entity.name}">
<c:if test="${entity.required}"><span class="required-color">*</span></c:if>
${f:space(entity.name)}
</label>