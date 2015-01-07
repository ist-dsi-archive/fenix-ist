<%--

Copyright © 2014 Instituto Superior Técnico

This file is part of FenixEdu CMS.

FenixEdu CMS is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FenixEdu CMS is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link href="${pageContext.request.contextPath}/static/lib/bootstrap-switch/css/bootstrap3/bootstrap-switch.min.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/lib/bootstrap-switch/js/bootstrap-switch.min.js"></script>

<c:set var="context" scope="request" value="${pageContext.request.contextPath}/personal-homepage"/>
${portal.toolkit()}

<h2 class="page-header">
    <spring:message code="title.manage.homepage" />
</h2>
<c:if test="${homepage.published}">
    <ul class="nav nav-pills">
        <li role="presentation" class="active"><a href="${pageContext.request.contextPath}/personal-homepage"><spring:message code="label.homepage.options"/></a></li>
        <li role="presentation"><a href="${pageContext.request.contextPath}/personal-homepage/content"><spring:message code="label.homepage.contents"/></a></li>
        <c:if test="${not empty homepage}">
            <li><a href="${homepage.fullUrl}" target="_blank">Link</a></li>
        </c:if>
    </ul>
</c:if>

<br/>


<div class="homepage-options">
    <form role="form" method="post" action="${context}/options" class="form-horizontal" id="homepage-publish-form">
    <c:if test="${not empty person}">
        <div class="form-group">
            <label class="col-sm-3 control-label">
                <spring:message code="label.homepage.activated" />
            </label>
            <div class="col-sm-3">
                <div class="checkbox">
                    <input name="published" type="checkbox" value="true" ${homepage.published ? "checked='checked'" : ""} onchange="$('#homepage-publish-form').submit()">
                </div>
            </div>
        </div>

        <c:if test="${not empty homepage && homepage.published}">
            <p>
                <h3 class="page-header"><spring:message code="label.homepage.components" />
                    <a href="#" data-toggle="modal" data-target="#activePagesModal" class="btn btn-default pull-right" role="button">
                        <span class="glyphicon glyphicon glyphicon-cog" aria-hidden="true"></span> <spring:message code="label.homepage.active.pages"/>
                    </a>
                </h3>
            </p>

            <!-- Working Unit -->
            <c:if test="${not empty person.employee.currentWorkingContract.workingUnit}">
                <div class="form-group">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.homepage.showUnit" />:
                    </label>
                    <div class="col-sm-2">
                        <div class="checkbox">
                            <input name="showUnit" type="checkbox" value="true" ${homepage.showUnit ? "checked='checked'" : ""}>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <p>${person.employee.currentWorkingContract.workingUnit.name}</p>
                    </div>
                </div>
                <hr/>
            </c:if>

            <!-- Photo -->
            <c:if test="${not empty person.user.profile && not empty person.user.profile.avatarUrl}">
                <div class="form-group">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.homepage.showPhoto" />:
                    </label>
                    <div class="col-sm-2">
                        <div class="checkbox">
                            <input name="showPhoto" type="checkbox" value="true" ${homepage.showPhoto ? "checked='checked'" : ""}>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <img class="img-circle" alt="photo" src="${person.user.profile.avatarUrl}"/>
                    </div>
                </div>
                <hr/>
            </c:if>

            <!-- Teacher -->
            <c:if test="${not empty person.teacher && not empty person.teacher.activeContractedTeacher}">
                <div class="form-group">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.homepage.showCategory" />:
                    </label>
                    <div class="col-sm-2">
                        <div class="checkbox">
                            <input name="showCategory" type="checkbox" value="true" ${homepage.showCategory ? "checked='checked'" : ""}>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <c:if test="${not empty person.teacher.category}">
                            ${person.teacher.category.name.content}
                        </c:if>
                    </div>
                </div>
                <hr/>

                <div class="form-group">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.homepage.showCurrentExecutionCourses" />:
                    </label>
                    <div class="col-sm-2">
                        <div class="checkbox">
                            <input name="showCurrentExecutionCourses" type="checkbox" value="true" ${homepage.showCurrentExecutionCourses ? "checked='checked'" : ""}>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <c:forEach var="executionCourse" items="${person.teacher.currentExecutionCourses}">
                            <a href="${executionCourse.cmsSite.address}" target="_blank">${executionCourse.nome}</a>
                        </c:forEach>
                    </div>
                </div>
                <hr />

                <div class="form-group">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.homepage.showResearchUnitHomepage" />:
                    </label>
                    <div class="col-sm-2">
                        <div class="checkbox">
                            <input name="showResearchUnitHomepage" type="checkbox" value="true" ${homepage.showResearchUnitHomepage ? "checked='checked'" : ""}>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <c:if test="${not empty person.teacher.category}">
                            <div class="form-group">
                                <label class="control-label col-sm-2">
                                    <spring:message code="label.homepage.research.unit.homepage" />:
                                </label>
                                <div class="col-sm-8">
                                    <input type="url" name="researchUnitHomepage" class="form-control" value="${homepage.researchUnitHomepage}">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2">
                                    <spring:message code="label.homepage.research.unit.name" />:
                                </label>
                                <div class="col-sm-8">
                                    <input type="text" name="researchUnitName" bennu-localized-string class="form-control" value='${homepage.researchUnitName.json()}'>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                <hr/>
            </c:if>


            <!-- Active curricular plans -->
            <c:if test="${not empty person.activeStudentCurricularPlansSortedByDegreeTypeAndDegreeName && not empty person.activeStudentCurricularPlansSortedByDegreeTypeAndDegreeName}">
                <div class="form-group">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.homepage.showActiveStudentCurricularPlans" />:
                    </label>
                    <div class="col-sm-2">
                        <div class="checkbox">
                            <input name="showActiveStudentCurricularPlans" type="checkbox" value="true" ${homepage.showActiveStudentCurricularPlans ? "checked='checked'" : ""}>
                        </div>
                    </div>
                    <div class="col-sm-8">
                        <c:forEach var="studentCurricularPlan" items="${person.activeStudentCurricularPlansSortedByDegreeTypeAndDegreeName}">
                            <c:if test="${not empty studentCurricularPlan.degreeCurricularPlan.degree.site}">

                                <a href="studentCurricularPlan.degreeCurricularPlan.degree.site.address" target="_blank"></a>
                                <c:choose>
                                    <c:when test="${not empty studentCurricularPlan.specialization.name}">
                                        <c:choose>
                                            <c:when test="${studentCurricularPlan.specialization.name==STUDENT_CURRICULAR_PLAN_SPECIALIZATION}">
                                                ${fr:message('resources.EnumerationResources', studentCurricularPlan.specialization.name)}
                                            </c:when>

                                            <c:when test="${studentCurricularPlan.specialization.name!=STUDENT_CURRICULAR_PLAN_SPECIALIZATION}">
                                                ${fr:message('resources.EnumerationResources', studentCurricularPlan.degree.tipoCurso.name)}
                                            </c:when>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        ${fr:message('resources.EnumerationResources', studentCurricularPlan.degreeCurricularPlan.degree.tipoCurso.name)}
                                        </c:otherwise>
                                    </c:choose>
                                    <spring:message code="label.in" /> ${studentCurricularPlan.registration.degreeName}
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                    <hr/>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">
                            <spring:message code="label.homepage.showCurrentAttendingExecutionCourses" />:
                        </label>
                        <div class="col-sm-2">
                            <div class="checkbox">
                                <input name="showCurrentAttendingExecutionCourses" type="checkbox" value="true" ${homepage.showCurrentAttendingExecutionCourses ? "checked='checked'" : ""}>
                            </div>
                        </div>
                        <div class="col-sm-8">
                            <c:forEach var="attend" items="${personAttends}">
                                <a href="${attend.disciplinaExecucao.cmsSite.address}">${attend.disciplinaExecucao.nome}</a>
                            </c:forEach>
                        </div>
                    </div>
                    <hr />
                </c:if>

                <c:if test="${isAlumni}">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">

                     <spring:message code="label.homepage.showAlumniDegrees" />:
                        </label>
                        <div class="col-sm-2">
                            <div class="checkbox">
                                <input name="showAlumniDegrees" type="checkbox" value="true" ${homepage.showAlumniDegrees ? "checked='checked'" : ""}>
                            </div>
                        </div>
                        <div class="col-sm-8">
                            <c:forEach var="studentCurricularPlan" items="person.completedStudentCurricularPlansSortedByDegreeTypeAndDegreeName">
                                <a href="${studentCurricularPlan.degreeCurricularPlan.degree.cmsSite.address}">
                                    <c:choose>
                                        <c:when test="${not empty studentCurricularPlan.specialization.name}">
                                            <c:choose>
                                                <c:when test="${not empty studentCurricularPlan.specialization.name}">
                                                    ${fr:message('resources.EnumerationResources', studentCurricularPlan.specialization.name)}
                                                </c:when>
                                                <c:otherwise>
                                                    ${fr:message('resources.EnumerationResources', studentCurricularPlan.degreeCurricularPlan.degree.tipoCurso.name)}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            ${fr:message('resources.EnumerationResources', studentCurricularPlan.degreeCurricularPlan.degree.tipoCurso.name)}
                                        </c:otherwise>
                                    </c:choose>
                                    <spring:message code="label.in" />${studentCurricularPlan.degreeCurricularPlan.degree.name}
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                    <hr />
                </c:if>

                <div class="btn-group pull-right" role="group">
                    <button type="reset" class="btn btn-default"><spring:message code="action.cancel" /></button>
                    <button type="submit" class="btn btn-primary"><spring:message code="action.save" /></button>
                </div>
            </c:if>
        </c:if>
    </form>

    <div class="modal fade" id="activePagesModal" tabindex="-1" role="dialog" aria-hidden="true">
        <form method="post" action="${context}/activePages" class="form-horizontal">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            <span aria-hidden="true">&times;</span><span class="sr-only">
                            <spring:message code="action.cancel" />
                        </span>
                        </button>
                        <h4><spring:message code="label.homepage.active.pages"/></h4>
                    </div>

                    <div class="modal-body">
                        <div class="form-group">
                            <spring:message code="title.homepage.activePages"></spring:message>
                        </div>
                        <div class="form-group">
                            <c:forEach var="page" items="${dynamicPages}">
                                <div class="form-group">
                                    <label class="col-sm-6 control-label">
                                            ${page.name.content}:
                                    </label>
                                    <div class="col-sm-6">
                                        <div class="checkbox">
                                            <input name="${page.slug}" type="checkbox" value="true" ${page.published ? "checked='checked'" : ""}>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" data-dismiss="modal" class="btn btn-default">
                            <spring:message code="action.cancel" />
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <spring:message code="action.save" />
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <script type="text/javascript">
        !$(".checkbox > input").addClass('bootstrap-switch-mini');
        !$(".checkbox > input").bootstrapSwitch();
    </script>
</div>