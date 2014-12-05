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

<c:set var="context" scope="session" value="${pageContext.request.contextPath}/pages/${site.externalId}/admin"/>

${portal.angularToolkit()}
<link href="${pageContext.request.contextPath}/bennu-admin/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/bennu-admin/fancytree/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bennu-admin/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<script type="text/javascript">
    context = "${context}";
</script>

<script src="${pageContext.request.contextPath}/cms/angular-file-upload.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/cms/pages.js" type="text/javascript"></script>

<h1><spring:message code="label.pages.management"/></h1>

<br/>

<div ng-app="pagesApp">
    <div ng-controller="PagesCtrl">

        <div class="alert alert-danger" ng-if="error"><strong></strong><spring:message code="label.error"/>: </strong><spring:message code="label.error.tryAgain"/></div>

        <div class="row" style="min-height:400px">

            <div class="col-md-3">
                <div id="tree"  style="border: dotted 2px #eee;"></div>
                <br/>
                <p>
                    <button ng-click="createChild()" class="btn btn-default" ng-disabled="!selected.key && !selected.root">
                        <span class="glyphicon glyphicon-plus"></span> <spring:message code="action.create"/>
                    </button>
                </p>
            </div>

            <div class="col-md-7"  style="border-left: solid 1px #eee">
                <h3>{{ selected.node.title }}</h3>

                <hr />

                <div ng-show="!selected.root">
                    <ul class="nav nav-pills" role="tablist">
                        <li class="active"><a id="pageTabLink" href="#page" role="tab" data-toggle="tab"><spring:message code="label.page"/></a></li>
                        <li><a href="#files" id="pageFilesLink" role="tab" data-toggle="tab" ng-if="selected.key"><spring:message code="label.files"/></a></li>
                        <li><a href="#permissions" role="tab" data-toggle="tab" ng-if="selected.key"><spring:message code="label.permissions"/></a></li>
                        <li><a href="{{selected.pageAddress}}" role="tab" ng-if="selected.key" target="_blank"><spring:message code="label.preview"/></a></li>
                    </ul>

                    <div class="tab-content">

                        <!-- Tab to manage the page content -->
                        <div class="tab-pane fade in active" id="page">
                            <br/>
                            <fieldset class="form-horizontal">
                                <label for="title"><spring:message code="label.title"/></label>
                                <input type="text" id="title" name="title" ng-localized-string="selected.title" required class="form-control" placeholder="<spring:message code="label.title"/>"/>

                                <label for="body"><spring:message code="label.content"/></label>
                                <textarea bennu-localized-string ng-html-editor="selected.body" placeholder="<spring:message code="label.content"/>" id="body" class="form-control"></textarea>

                                <div>
                                    <div class="pull-right">
                                        <button data-toggle="modal" data-target="#itemDeleteModal" class="btn btn-danger btn-sm" ng-disabled="selected.root">
                                            <spring:message code="action.delete"/>
                                        </button>
                                        <button ng-click="saveSelected()" class="btn btn-primary btn-sm">
                                            <spring:message code="action.save"/>
                                        </button>
                                    </div>
                                </div>
                            </fieldset>
                        </div>

                        <!-- Tab to manage the page files -->
                        <div class="tab-pane fade" id="permissions">
                            <br/>
                            <p><spring:message code="label.permissions.group"/></p>
                            <fieldset class="form-horizontal">
                                <div class="input-group" ng-repeat="group in groups" ng-click="selected.canViewGroupIndex = $index" >
                                    <input type="radio" ng-checked="$index == selected.canViewGroupIndex" />
                                    {{ group.name }}
                                </div>

                                <div>
                                    <div class="pull-right">
                                        <button ng-click="saveSelected()" class="btn btn-primary btn-sm">
                                            <spring:message code="action.save"/>
                                        </button>
                                    </div>
                                </div>
                            </fieldset>
                        </div>

                        <!-- Tab to manage the page files -->
                        <div class="tab-pane fade" id="files">
                            <br/>
                            <p>
                                <button ng-file-select ng-model="files" multiple="true"><spring:message code="action.add.file"/></button>
                            </p>

                            <table ng-if="selected.files" class="table table-striped table-bordered">
                                <thead>
                                <tr>
                                    <th class="col-md-1 text-center">#</th>
                                    <th class="col-md-7"><spring:message code="theme.view.label.name"/></th>
                                    <th class="col-md-4"><spring:message code="label.operations"/></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr  ng-repeat="file in selected.files">
                                    <td class="center">
                                        <h5>{{ $index + 1 }}</h5>
                                    </td>

                                    <td>
                                        <a href="{{ file.downloadUrl }}" target="_blank"><h5>{{ file.name }}</h5></a>
                                    </td>

                                    <td>
                                        <a href="#files" class="btn btn-default btn-sm" ng-click="updateFilePosition(file, $index+1)" ng-disabled="$last"><span class="glyphicon glyphicon-chevron-down"></span></a>
                                        <a href="#files" class="btn btn-default btn-sm" ng-click="updateFilePosition(file, $index-1)" ng-disabled="$first"><span class="glyphicon glyphicon-chevron-up"></span></a>
                                        <a href="#files" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#fileDeleteModal" ng-click="selectFile(file)"><span class="glyphicon glyphicon-trash"></span></a>
                                        <a href="{{ file.downloadUrl }}" target="_blank" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-download-alt"></span></a>
                                    </td>

                                </tr>
                                </tbody>
                            </table>
                            <p ng-if="selected.files.length==0"><spring:message code="label.files.empty"/></p>
                        </div>

                        <div class="modal fade" id="fileDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal">
                                            <span aria-hidden="true">&times;</span>
                                            <span class="sr-only"><spring:message code="action.close"/></span></button>
                                        <h4><spring:message code="action.delete"/></h4>
                                    </div>

                                    <div class="modal-body">
                                        <spring:message code="theme.view.label.delete.confirmation"/> <b id="fileName"></b>?
                                    </div>

                                    <div class="modal-footer">
                                        <button type="submit" class="btn btn-danger" ng-click="deleteSelectedFile()" data-dismiss="modal"><spring:message code="label.yes"/></button>
                                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.no"/></button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="modal fade" id="itemDeleteModal" role="dialog" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal">
                                            <span aria-hidden="true">&times;</span>
                                            <span class="sr-only"><spring:message code="action.close"/></span></button>
                                        <h4><spring:message code="action.delete"/></h4>
                                    </div>

                                    <div class="modal-body">
                                        <p><spring:message code="action.delete.page.confirmation" /> '{{ selected.title | i18n }}' ?</p>
                                    </div>

                                    <div class="modal-footer">
                                        <button type="submit" class="btn btn-danger" data-dismiss="modal" data-target="#itemDeleteModal" ng-click="deleteSelected()"><spring:message code="label.yes"/></button>
                                        <button type="button" class="btn btn-default" data-dismiss="modal" data-target="#itemDeleteModal"><spring:message code="label.no"/></button>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<style>

    .fancytree-container {
        outline: none;
    }
    #tree {
        margin-top: 9px;
    }
    #tree > .fancytree-container {
        border: none;
    }
    .info {
        border-left: 1px solid #eee;
    }
</style>
