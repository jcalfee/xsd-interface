<%@ tag isELIgnored="false" body-content="tagdependent"
    import="info.jcalfee.gae.ds.DataStore,
    info.jcalfee.xsi.client.XmlSchema.Element"%>
<%@ taglib prefix="xsi" tagdir="/WEB-INF/tags"%>
<%@ attribute name="xsi" required="true" type="java.lang.Object"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="functions"%>
<%@ taglib prefix="d" uri="dataStore"%>
<%-- Record the current element in the XSI request object --%>
<c:set var="rows" value="${d:list(xsi)}" />
<c:if test="${!empty rows}">
    <xsi:style type="border">
    <form method="post">
    <table class="title">
        <thead >
            <th colspan="3">${f:space(xsi.element.name)}</th>
            <c:forEach var="attribute" items="${xsi.element.allAttributes}">
                <th>${f:space(attribute.name)}</th>
            </c:forEach>
        </thead>
        <tbody>
            <c:forEach var="row" items="${rows}">
                <c:if test="${row['xsi_key'] == xsi.elementKey}">
                <c:set var="xsi_selected" value="${xsi}" />
                </c:if>
                <tr class="${row['xsi_key'] == xsi.elementKey ? 'xsi-selected' : ''}">
                    <td><c:set var="checkboxName"
                        value="${row['xsi_key']}_checked" /> <input
                        type="checkbox" name="${checkboxName}"
                        ${param[checkboxName] == null ? '' : ' checked'}/></td>
                    <td>[<a
                        href="${xsi.element.name}(${row['xsi_key']})?edit">Edit</a>]</td>
                    <td><c:forEach var="child" items="${xsi.children}">
                        <a href="${xsi.element.name}(${row['xsi_key']})/${child.element.name}">${f:space(child.element.name)}</a>|
                    </c:forEach></td>
                    <c:forEach var="attribute"
                        items="${xsi.element.allAttributes}">
                        <td><c:out value="${f:format(attribute, row[attribute.name])}"
                            escapeXml="true" /></td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </xsi:style>
    <input type="submit" name="delete_checked" value="Delete" />
    (checked items)</form>
</c:if>

