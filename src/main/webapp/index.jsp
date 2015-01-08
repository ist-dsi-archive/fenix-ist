<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty LOGGED_USER_ATTRIBUTE}">
	<c:set var="url" scope="request" value="${pageContext.request.contextPath}/login"/>
</c:if>

<c:if test="${not empty LOGGED_USER_ATTRIBUTE}">
	<c:set var="url" scope="request" value="${pageContext.request.contextPath}/home.do"/>
</c:if>

<%
	response.sendRedirect((String) pageContext.findAttribute("url"));
%>