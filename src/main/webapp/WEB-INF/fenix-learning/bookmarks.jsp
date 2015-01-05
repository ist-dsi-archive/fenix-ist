<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="bookmarks.title"/></h1>

<c:if test="${empty bookmarks}">
	<em><spring:message code="bookmarks.empty.message"/></em>
</c:if>

<c:if test="${not empty bookmarks}">
<table class="table table-condensed">
	<thead>
		<tr>
			<th><spring:message code="channels.label.name"/></th>
			<th></th>
			<th><spring:message code="channels.label.bookmark"/></th>
			<th>RSS</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="category" items="${bookmarks}">
			<tr>
				<td>${category.site.name.content}</td>
				<td>${category.name.content}</td>
				<td>
					<c:choose>
						<c:when test="${bookmarks.contains(category)}">
							<spring:message code="label.yes"/> (<a href="${pageContext.request.contextPath}/learning/bookmarks/remove/${category.externalId}"><spring:message code="action.remove"/></a>)
						</c:when>
						<c:otherwise>
							<spring:message code="label.no"/> (<a href="${pageContext.request.contextPath}/learning/bookmarks/add/${category.externalId}"><spring:message code="action.add"/></a>)
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<a href="${category.rssUrl}" data-toggle="tooltip" title="${category.site.name.content} - ${category.name.content}" data-placement="left">
						<img src="${pageContext.request.contextPath}/image/rss.svg" width="15" height="15" />
					</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</c:if>
<script>$(function () {
  $('[data-toggle="tooltip"]').tooltip()
});</script>