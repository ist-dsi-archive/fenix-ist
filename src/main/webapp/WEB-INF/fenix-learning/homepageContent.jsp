<%--

    Copyright © ${project.inceptionYear} Instituto Superior Técnico

    This file is part of Fenix IST.

    Fenix IST is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Fenix IST is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<c:set var="context" scope="request" value="${pageContext.request.contextPath}/pages/${homepage.externalId}/admin"/>

${portal.angularToolkit()}
<link href="${pageContext.request.contextPath}/static/lib/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script>
window.tooltip = $.fn.tooltip;
</script>
<script src="${pageContext.request.contextPath}/static/lib/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/lib/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>
<script type="text/javascript"> context = "${context}";</script>
<script src="${pageContext.request.contextPath}/static/lib/angular-file-upload/angular-file-upload.min.js" type="text/javascript"></script>
<script>
$.fn.tooltip = window.tooltip;
</script>
<script src="${pageContext.request.contextPath}/static/pages.js" type="text/javascript"></script>

<h2 class="page-header">
    <spring:message code="title.manage.homepage" />
</h2>

<c:if test="${homepage.published}">
    <ul class="nav nav-pills">
        <li role="presentation"><a href="${pageContext.request.contextPath}/personal-homepage"><spring:message code="label.homepage.options"/></a></li>
        <li role="presentation" class="active"><a href="${pageContext.request.contextPath}/personal-homepage/content"><spring:message code="label.homepage.contents"/></a></li>
        <c:if test="${not empty homepage}">
            <li><a href="${homepage.fullUrl}" target="_blank">Link</a></li>
        </c:if>
    </ul>
    <%@include file="contents.jsp" %>
</c:if>
