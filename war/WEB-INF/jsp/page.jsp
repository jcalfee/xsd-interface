<%@ page isELIgnored="false"
	import="info.jcalfee.xsi.*, info.jcalfee.xsi.client.XmlSchema.*,info.jcalfee.gae.ds.DataStore"%>
<%@ taglib prefix="xsi" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="f" uri="functions"%>
<%@ taglib prefix="d" uri="dataStore"%>

<jsp:include page="tabs.jsp"></jsp:include>

<c:if test="${!empty xsi.hierarchy}">

	<c:set var="xsi" value="${xsi.last}" />

	<c:forEach var="child" items="${xsi.children}">
		<h2><a
			href="${child.element.name}(${row['xsi_key']})/${child.element.name}">${f:space(child.element.name)}</a>
		</h2>
		<c:set var="rows" value="${d:list(child)}" />
		<c:if test="${!empty rows}">
			<div class="xsi-list-table">
			<form method="post">
			<table>
				<thead>
					<th></th>
					<c:forEach var="attribute" items="${child.element.attributes}">
						<th>${f:space(attribute.name)}</th>
					</c:forEach>
				</thead>
				<tbody>
					<c:forEach var="row" items="${rows}">

						<tr class="${xsi_selected}">
							<td><c:set var="checkboxName"
								value="${row['xsi_key']}_checked" /> <input type="checkbox"
								name="${checkboxName}" ${param[checkboxName] == null ? '' : ' checked'}/></td>
							<c:forEach var="attribute" items="${child.element.attributes}">
								<td><c:out value="${row[attribute.name]}" escapeXml="true" /></td>
							</c:forEach>
							<td><a href="${xsi.element.name}(${row['xsi_key']})?edit">Edit</a></td>

						</tr>
					</c:forEach>
				</tbody>
			</table>
			<input type="submit" name="delete_checked" value="Delete" />
			(checked items)</form>

			</div>
		</c:if>
	</c:forEach>

	<c:choose>
		<c:when test="${!empty xsi.response}">
			<span
				class="${xsi.response.success ? 'xsi-response-success' : 'xsi-response-fail'}">
			${xsi.response.message}</span>
		</c:when>
	</c:choose>

	<c:if test="${!empty xsi.element.attributes}">
		<form method="post"><input name="id" type="hidden"
			value="${
    param['edit'] != null && empty param['id'] ? xsi.elementKey : param['id']
    }" />
		<input type="hidden" name="xsi_version"
			value="${
      xsi.response.data != null ? 
        xsi.response.data['xsi_version'] : 
        xsi.values['xsi_version']}" />
		<input type="hidden" name="edit" />
		<table>
			<thead>
				<th colspan="2">${param['edit'] == null || empty xsi.elementKey
				? 'New' : 'Editing'} ${f:space(xsi.element.name)}</th>
			</thead>
			<tbody>
				<c:forEach var="attribute" items="${xsi.element.attributes}">
					<tr>
						<td>${f:space(attribute.name)}</td>
						<td><xsi:formInput entity="${attribute}"
							databaseValue="${param['edit'] == null ? '' : xsi.values[attribute.name]}" />
						<c:if
							test="${xsi.response.data != null and xsi.response.data[attribute.name] !=  param[attribute.name]}">
                        ${f:showNonNull('<br><div class="xsi-response-fail">', xsi.response.data[attribute.name], '</div>')}</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<input type="submit" value="Save" /> <a href="/${xsi.urlString}">New</a></form>

	</c:if>
	<%-- if attributes --%>

	<div class="xsi-navigation">
	<table>
		<thead>
			<th colspan="2"><a href="/${xsi.name }/">${f:space(xsi.name)
			}</a></th>
		</thead>
		<tbody>
			<c:forEach var="xsi" items="${xsi.hierarchy}" varStatus="status">${d:load(xsi)}
                
                <tr>
					<td><c:choose>
						<c:when test="${!empty xsi.elementKey }">
							<a href="/${xsi.urlString}">${f:space(xsi.element.name)}</a>
						</c:when>
						<c:otherwise>${f:space(xsi.element.name)}</c:otherwise>
					</c:choose></td>
					<c:if test="${!empty xsi.values['name'] }">
						<td>${xsi.values['name']}</td>
					</c:if>
				</tr>

			</c:forEach>
		</tbody>
	</table>
	</div>
</c:if>