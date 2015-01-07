<div ng-app="pagesApp">
    <div ng-controller="PagesCtrl">

        <div class="alert alert-danger" ng-if="error"><strong></strong><spring:message code="label.error"/>: </strong><spring:message code="label.error.tryAgain"/></div>

        <div class="row" style="min-height:400px" ng-show="loaded">

            <div class="col-md-3">
                <div id="tree"  style="border: dotted 2px #eee;"></div>
                <br/>
                <p>
                    <button ng-click="createChild()" class="btn btn-default" ng-disabled="!selected.key && !selected.root">
                        <span class="glyphicon glyphicon-plus"></span> <spring:message code="action.create"/>
                    </button>
                </p>
            </div>

            <div class="col-md-9"  style="border-left: solid 1px #eee">
                <h3>{{ selected.node.title }}</h3>

                <hr />

                <div ng-show="selected.loaded">
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
                            <fieldset class="form-horizontal" ng-show="selected.loaded">
                                <label for="title"><spring:message code="label.title"/></label>
                                <input type="text" id="title" name="title" ng-localized-string="selected.title" required class="form-control" placeholder="<spring:message code="label.title"/>"/>

                                <label for="body"><spring:message code="label.content"/></label>
                                <textarea bennu-localized-string ng-html-editor="selected.body" id="body" class="form-control"></textarea>

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
                                <div class="radio" ng-repeat="group in groups" ng-click="selected.canViewGroupIndex = $index">
                                    <label>
                                    <input type="radio" ng-checked="$index == selected.canViewGroupIndex" />
                                    {{ group.name }}
                                    </label>
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
                                    <th class="col-sm-1 text-center">#</th>
                                    <th><spring:message code="theme.view.label.name"/></th>
                                    <th><spring:message code="label.permissions"/></th>
                                    <th><spring:message code="label.operations"/></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr  ng-repeat="file in selected.files">
                                    <td class="center">
                                        {{ $index + 1 }}
                                    </td>

                                    <td>
                                        <a href="{{ file.downloadUrl }}" target="_blank">{{ file.name }}</a>
                                        <div ng-show="file.name != file.filename">
                                            <em class="small">{{ file.filename }}</em>
                                        </div>
                                    </td>

                                    <td>
                                        <small>{{ groups[file.group].name }}</small>
                                    </td>

                                    <td ng-init="file.position = $index">
                                        <a href="#" class="btn btn-default btn-xs" ng-click="selectFile(file)" data-toggle="modal" data-target="#fileEditModal"><span class="glyphicon glyphicon-pencil"></span></a>
                                        <a href="#" class="btn btn-default btn-xs" ng-click="updateFile(file, file.position+1)" ng-disabled="$last"><span class="glyphicon glyphicon-chevron-down"></span></a>
                                        <a href="#" class="btn btn-default btn-xs" ng-click="updateFile(file, file.position-1)" ng-disabled="$first"><span class="glyphicon glyphicon-chevron-up"></span></a>
                                        <a href="#" class="btn btn-danger btn-xs" data-toggle="modal" data-target="#fileDeleteModal" ng-click="selectFile(file)"><span class="glyphicon glyphicon-trash"></span></a>
                                        <a href="{{ file.downloadUrl }}" target="_blank" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-download-alt"></span></a>
                                    </td>

                                </tr>
                                </tbody>
                            </table>
                            <p ng-if="selected.files.length==0"><spring:message code="label.files.empty"/></p>
                        </div>

                        <div class="modal fade" id="fileEditModal" tabindex="-1" role="dialog" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal">
                                            <span aria-hidden="true">&times;</span>
                                            <span class="sr-only"><spring:message code="action.close"/></span></button>
                                        <h4><spring:message code="action.edit"/> '{{selectedFile.name}}'</h4>
                                    </div>

                                    <div class="modal-body">
                                        <form class="form-horizontal">
                                            <div class="form-group">
                                                <label class="control-label col-sm-2"><spring:message code="label.name"/></label>
                                                <div class="col-sm-10">
                                                    <input type="text" class="form-control" ng-model="selectedFile.name"/>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="control-label col-sm-2"><spring:message code="label.permissions"/></label>
                                                <div class="col-sm-10">
                                                    <div class="radio" ng-repeat="group in groups" ng-click="selectedFile.group = $index" >
                                                        <label>
                                                        <input type="radio" ng-checked="$index == selectedFile.group" />
                                                        {{ group.name }}
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </form>
                                    </div>

                                    <div class="modal-footer">
                                        <button type="submit" class="btn btn-primary" ng-click="updateFile(selectedFile, selectedFile.position)" data-dismiss="modal"><spring:message code="action.save"/></button>
                                    </div>
                                </div>
                            </div>
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
                                        <spring:message code="theme.view.label.delete.confirmation"/> <strong>{{selectedFile.name}}</strong>?
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
        <div ng-show="!loaded">
            <em><spring:message code="label.loading"/></em>
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