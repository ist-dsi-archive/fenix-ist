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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<c:set var="context" scope="session" value="${pageContext.request.contextPath}/teacher/${executionCourse.externalId}/announcements"/>


<h2 class="page-header"><spring:message code="label.announcements"/></h2>

<!-- Button for announcements creation --->
<div class="btn-group">
    <a href="#" data-toggle="modal" data-target="#createModal" class="btn btn-primary" role="button">
        <spring:message code="action.create"/>
    </a>
</div>

<div style="padding: 15px;"></div>


<c:choose>

    <c:when test="${announcements.size() > 0}">
        <c:forEach var="announcement" items="${announcements}">

            <!-- announcements rendering --->
            <div class="announcement">
                <div class="row">
                    <div class="col-md-8 col-md-offset-1">
                        <h4><a href="${announcement.address}" target="_blank">${announcement.name.content}</a></h4>
                        <small>
                            <a href="mailto:${announcement.createdBy.email}">${announcement.createdBy.name}</a>
                              -
                            ${announcement.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}
                        </small>
                        <h5>
                            ${announcement.body.content}
                        </h5>
                    </div>

                    <div class="btn-group col-md-2">
                        <div class="pull-right">
                            <a href="#" class="btn btn-danger" onclick="showDeleteConfirmation('${announcement.slug}');">
                                <spring:message code="action.delete"/>
                            </a>
                            <a href="#" class="btn btn-default" data-toggle="modal" data-target="#editModal">
                                <spring:message code="action.edit"/>
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal panel for editing an announcement -->
            <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-hidden="true">
                <form method="post" action="${context}/${announcement.slug}/edit">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">

                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal">
                                    <span aria-hidden="true">&times;</span>
                                    <span class="sr-only"><spring:message code="action.close"/></span>
                                </button>
                                <h4>${announcement.name.content}</h4>
                            </div>

                            <div class="modal-body">
                                <div class="form-group">
                                    <label for="name" class="control-label">
                                        <spring:message code="label.announcement.title"/>
                                    </label>

                                    <input bennu-localized-string required-any name="name" id="name"
                                        placeholder="<spring:message code="label.announcement.title"/>"
                                        value='${announcement.name.json()}'>
                                </div>

                                <div class="form-group">
                                    <label for="body" class="control-label">
                                        <spring:message code="label.announcement.content"/>
                                    </label>

                                    <input bennu-localized-string bennu-html-editor required-any name="body" id="body"
                                        placeholder="<spring:message code="label.announcement.content"/>"
                                        value='${announcement.body.json()}'>
                                      <br/>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" data-dismiss="modal" class="btn btn-default">
                                    <spring:message code="action.cancel"/>
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <spring:message code="action.save"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

        </c:forEach>
    </c:when>

    <c:otherwise>
        <h4><i>${fr:message("resources.FenixEduCMSResources", "label.announcements.emtpy")}</i></h4>
    </c:otherwise>

</c:choose>

<!-- Modal panel for creating an announcement -->
<div class="modal fade" id="createModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form method="post" action="${context}/create">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">
                            ${fr:message("resources.FenixEduCMSResources", "action.cancel" )}
                        </span>
                    </button>
                    <h4>${fr:message("resources.FenixEduCMSResources", "action.create.announcement")}</h4>
                </div>

                <div class="modal-body">
                    <div class="form-group">
                        <label for="name" class="control-label">
                            ${fr:message("resources.FenixEduCMSResources", "label.announcement.title" )}
                        </label>

                        <input bennu-localized-string required-any name="name" id="name"
                               placeholder="${fr:message("resources.FenixEduCMSResources", "label.announcement.title" )}">
                    </div>

                    <div class="form-group">
                        <label for="announcementBody" class="control-label">
                            ${fr:message("resources.FenixEduCMSResources", "label.announcement.content")}
                        </label>

                        <input bennu-localized-string bennu-html-editor required-any name="body" id="body"
                                   placeholder="${fr:message("resources.FenixEduCMSResources", "label.announcement.content" )}">
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" data-dismiss="modal" class="btn btn-default">
                        ${fr:message("resources.FenixEduCMSResources", "action.cancel" )}
                    </button>
                    <button type="submit" class="btn btn-primary">
                        ${fr:message("resources.FenixEduCMSResources", "action.save" )}
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

<!-- Modal panel for deleting an announcement -->
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form id="deleteForm" method="post" action="#">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                        <span class="sr-only">
                            ${fr:message("resources.FenixEduCMSResources", "action.cancel" )}
                        </span>
                    </button>
                    <h4>${fr:message("resources.FenixEduCMSResources", "action.delete" )}</h4>
                </div>
                <div class="modal-body">
                    <p>${fr:message("resources.FenixEduCMSResources", "label.announcement.delete.confirmation")}</p>
                </div>
                <div class="modal-footer">
                    <button type="button" data-dismiss="modal" class="btn btn-default">
                        ${fr:message("resources.FenixEduCMSResources", "action.cancel" )}
                    </button>
                    <button type="submit" class="btn btn-danger">
                        ${fr:message("resources.FenixEduCMSResources", "action.delete" )}
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/font-awesome.css"/>
<script src="${pageContext.request.contextPath}/static/js/toolkit.js" defer></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/toolkit/toolkit.css"/>

<script>
    function showDeleteConfirmation(announcementSlug) {
        $('#deleteForm').attr('action', '${context}/' + announcementSlug + '/delete');
        $('#confirmDeleteModal').modal('show');
        return false;
    }
</script>