<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h1><spring:message code="channels.title"/></h1>

<form class="form-horizontal" method="POST">
	<div class="form-group">
		<label class="control-label col-sm-2"><spring:message code="channels.label.execution.year"/></label>
		<div class="col-sm-10">
			<select class="form-control" name="semester" onchange="this.form.submit()">
				<option value="">- <spring:message code="channels.label.choose.an.option"/> -</option>
				<c:forEach var="semester" items="${executions}">
					<option value="${semester.externalId}" ${selectedSemester == semester ? 'selected' : ''}>${semester.qualifiedName}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<c:if test="${not empty selectedSemester}">
	<div class="form-group">
		<label class="control-label col-sm-2"><spring:message code="channels.label.degree"/></label>
		<div class="col-sm-10">
			<select class="form-control" name="degree" onchange="this.form.submit()">
				<option value="">- <spring:message code="channels.label.choose.an.option"/> -</option>
				<c:forEach var="degree" items="${degrees}">
					<option value="${degree.externalId}" ${selectedDegree == degree ? 'selected' : ''}>${degree.presentationName}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	</c:if>

	<c:if test="${courses != null}">
		<table class="table table-condensed">
			<thead>
				<tr>
					<th colspan="2"><spring:message code="channels.label.name"/></th>
					<th><spring:message code="channels.label.bookmark"/></th>
					<th>RSS</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="course" items="${courses}">
				<c:forEach var="slug" items="${slugs}">
					<c:set var="category" value="${course.cmsSite.categoryForSlug(slug)}"/>
					<tr>
						<td>${course.name}</td>
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
							<a href="${category.rssUrl}" data-toggle="tooltip" title="${course.name} - ${category.name.content}" data-placement="left">
								<img src="${pageContext.request.contextPath}/image/rss.svg" width="15" height="15" />
							</a>
						</td>
					</tr>
				</c:forEach>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</form>

<script>$(function () {
  $('[data-toggle="tooltip"]').tooltip()
});</script>
