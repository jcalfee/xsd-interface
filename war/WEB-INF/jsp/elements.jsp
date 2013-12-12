<%@ page isELIgnored="false"
    import="info.jcalfee.xsi.*,
    info.jcalfee.xsi.client.XmlSchema.*"%>
<%@ taglib prefix="xsi" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="functions"%>
<%@ taglib prefix="d" uri="dataStore"%>
<c:choose>
    <c:when test="${empty xsi.hierarchy}">
        <h1>${f:space(xsi.name) }</h1>
        <ol>
            <c:forEach var="child" items="${xsi.children}">
                <li><a href="${child.element.name}">${f:space(child.element.name)}</a><c:if
                    test="${!empty child.element.children}">
                    <code> (+)</code>
                </c:if></li>
            </c:forEach>
        </ol>
    </c:when>
    <c:otherwise>
        <c:set var="xsi" value="${xsi.last}" />
        <c:set var="parent" value="${xsi.parent}"/>
        <c:if test="${!empty parent }">
        <h1><a href="/${parent.urlString }">${f:space(parent.element.name)}</a></h1>
        </c:if>
        <xsi:listTable xsi="${xsi}" />
        <p></p>
        <xsi:response value="${xsi.response }"/>
        
        <form method="post">
        <input name="id" type="hidden" value="${
        param['edit'] != null && empty param['id'] ? xsi.elementKey : param['id']
        }" />
        <input type="hidden" name="xsi_version"
            value="${
          xsi.response.data != null ? 
            xsi.response.data['xsi_version'] : 
            xsi.values['xsi_version']}" />
        <input type="hidden" name="edit" />
        
        <xsi:style type="border">
        <xsi:style type="title">${param['edit'] == null ||
            empty xsi.elementKey ? 'New' : 'Editing'
            } ${f:space(xsi.element.name)}</xsi:style>
        <table style="border-collapse: collapse">
        <tbody>
        <td></td>
        <table>
        <c:forEach var="entity" items="${xsi.element.attributes}">
            <xsi:input xsi="${xsi }" entity="${entity }"/>
        </c:forEach>
        <c:forEach var="group"  items="${xsi.element.attributeGroups}">
        <tr>
        <td colspan="2">
        
        <table width="100%">
        <thead>
            <th colspan="2">${f:space(group.name) }</th>
        </thead>
        <tbody>
        <c:forEach var="entity" items="${group.attributes}">
        <xsi:input xsi="${xsi }" entity="${entity }"/>
        </c:forEach>
        </tbody>
        </table>
        
        </td>
        </tr>                        
        </c:forEach>
        </table>
        </tbody>
        </table>
        </xsi:style>
        <input type="submit" value="Save" /> 
        <a href="/${xsi.urlString}?new">New</a>
        </form>
<%/*         
        <div class="xsi-navigation">
        <table>
            <thead>
                <th colspan="2"><a href="/${xsi.name }/">${f:space(xsi.name)
                }</a></th>
            </thead>
            <tbody>
                <c:forEach var="xsi" items="${xsi.hierarchy}"
                    varStatus="status">${d:load(xsi)}
                <c:if test="${!empty xsi.values['name'] }">
                        <tr>
                            <td><a href="/${xsi.urlString}">${f:space(xsi.element.name)}</a></td>
                            <td>${xsi.values['name']}</td>
                        </tr>
                    </c:if>
                </c:forEach>
            </tbody>
        </table>
        </div>
*/%>
    </c:otherwise>
</c:choose>

