<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="functions"%>
<h1>${f:space(xsi.name) }</h1>
<table>
	<tr>
		<c:forEach var="child" items="${xsi.children}">
			<td><a href="/${xsi.name}/${child.element.name}">${f:space(child.element.name)}</a> <c:if
				test="${!empty child.element.children}">
				<code> (+)</code>
			</c:if></td>
		</c:forEach>
	</tr>
</table>
