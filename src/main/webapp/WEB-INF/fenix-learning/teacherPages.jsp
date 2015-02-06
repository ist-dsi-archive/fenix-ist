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

<c:set var="context" scope="session" value="${pageContext.request.contextPath}/pages/${site.externalId}/admin"/>
<c:set var="teacherContext" scope="request" value="${pageContext.request.contextPath}/teacher/${executionCourse.externalId}/pages"/>

${portal.angularToolkit()}
<link href="${pageContext.request.contextPath}/static/lib/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script>
window.tooltip = $.fn.tooltip;
</script>
<script src="${pageContext.request.contextPath}/static/lib/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/lib/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<script type="text/javascript">
    context = "${context}";
    $.fn.tooltip = window.tooltip;
</script>

<script src="${pageContext.request.contextPath}/static/lib/angular-file-upload/angular-file-upload.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/pages.js" type="text/javascript"></script>

<h2>
    <spring:message code="label.pages.management"/>
    <div class="button-group pull-right">
        <a data-toggle="modal" data-target="#copyContentModal" href="#" class="btn btn-default">
            <span class="glyphicon glyphicon-copy" aria-hidden="true"></span> <spring:message code="action.import.site"/> </a>

        <a data-toggle="modal" data-target="#optionsModal" href="#" class="btn btn-default">
            <span class="glyphicon glyphicon-cog" aria-hidden="true"></span> <spring:message code="label.homepage.options" /></a>
    </div>
</h2>
<hr />
<c:if test="${not empty importError}">
    <div class="alert alert-danger" role="alert">
        <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
        <span class="sr-only"></span>
        <spring:message code="label.error"/> :
        <spring:message code="label.error.tryAgain"/>
    </div>
</c:if>

<div class="modal fade" id="optionsModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only"><spring:message code="action.close"/></span></button>
                <h4><spring:message code="label.settings"/></h4>
            </div>
            <form role="form" method="post" action="${teacherContext}/options" class="form-horizontal" id="homepage-publish-form">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-2">
                            <spring:message code='label.alternativeSite'/>
                        </label>
                        <div class="col-sm-10">
                            <input name="alternativeSite" type="text" class="form-control" type="url"
                                   value="${site.alternativeSite}" placeholder="<spring:message code='label.alternativeSite.placeholder'/>" />
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">
                        <spring:message code="action.save"/>
                    </button>
                </div>
            </form>

        </div>
    </div>
</div>



<div class="modal fade" id="copyContentModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only"><spring:message code="action.close"/></span></button>
                <h4><spring:message code="label.import.previousSite"/></h4>
            </div>
            <form role="form" method="post" action="${teacherContext}/copyContent" class="form-horizontal" id="homepage-publish-form">
                <div class="modal-body">
                    <div class="form-group">
                        <select class="form-control" name="previousExecutionCourse">
                            <option value="">- <spring:message code="label.select.site"/> -</option>
                            <c:forEach var="ec" items="${previousExecutionCourses}">
                                <option value="${ec.externalId}" ${ec.externalId == previousExecutionCourse ? 'selected' : ''}>
                                        ${ec.executionPeriod.qualifiedName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">
                        <span class="glyphicon glyphicon-paste" aria-hidden="true"></span>
                        <spring:message code="action.import.site"/>
                    </button>
                </div>
            </form>

        </div>
    </div>
</div>



<%@include file="contents.jsp" %>