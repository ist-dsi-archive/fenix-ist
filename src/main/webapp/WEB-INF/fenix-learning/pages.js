var teacherApp = angular.module('pagesApp', ['bennuToolkit', 'angularFileUpload']);

teacherApp.controller('PagesCtrl', [ '$scope', '$http', '$upload', function ($scope, $http, $upload) {

    $scope.selectedFile = undefined;

    $scope.context = context || window.location.pathname;

    $scope.groups = [];

    $scope.handleError = function (data) {
        $scope.error = data;
        $scope.saving = false;
    };

    $scope.selectFile = function(file) {
        $scope.selectedFile = file;
    }

    var add = function (item, parent) {
        var isFolder = item.children && item.children.length > 0;
        item.node = parent.addChildren({ key: item.key, title: i18n(item.title), folder: isFolder, item: item });
        if(!item.body) {
            item.body = emptyLocalizedString();
        }
        if (isFolder) {
            for (var i = 0; i < item.children.length; ++i) {
                item.children[i] && add(item.children[i], item.node);
            }
        }
        return item.node;
    };

    $scope.createChild = function () {
        var newItem = { title: initialTitle(), body: emptyLocalizedString(), position: 0 };
        add(newItem, $scope.selected.node).setActive(true);
        $('#pageTabLink').tab('show');
    };

    $scope.deleteSelected = function () {
        var node = $scope.selected.node;
        var parent = $scope.selected.node.parent;
        if ($scope.selected.key) {
            $http.delete($scope.context + "/" + $scope.selected.key)
                .success(function(){ node.remove(); })
                .error($scope.handleError);
        } else {
            node.remove();
        }
        $scope.selected = parent.data.item;
        parent.setActive(true);
    };

    $scope.deleteSelectedFile = function() {
        if($scope.selectedFile) {
            $http.delete($scope.context + "/attachment/" + $scope.selected.key + "/" + $scope.selectedFile.externalId)
                .success(function(data) {
                    $scope.selected.files = data;
                })
                .error($scope.handleError);
        }
    }

    $scope.saveSelected = function () {
        var data = {
            title: $scope.selected.title,
            body: $scope.selected.body,
            position: $scope.selected.node.getIndex(),
            menuItemId: $scope.selected.key,
            menuItemParentId:  $scope.selected.node.parent.data.item.key,
            canViewGroupIndex: $scope.selected.canViewGroupIndex
        };
        $scope.data = data;
        var promise;
        if ($scope.selected.key) {
            promise = $http.put($scope.context, data);
        } else {
            promise = $http.post($scope.context, JSON.stringify(data));
        }
        $scope.error = null;
        $scope.saving = true;
        promise.success(function (newItem) {
            if(Object.keys(newItem.body).length == 0) {
                newItem.body = emptyLocalizedString();
            }
            var node = $scope.selected.node;
            node.parent.folder = node.parent.children && node.parent.children.length > 0;
            $scope.selected = newItem;
            newItem.node = node;
            node.setTitle(i18n(newItem.title));
            node.data.item = newItem;
        }).error($scope.handleError);
    };

    $http.get($scope.context + "/data").success(function (data) {
        $("#tree").fancytree({ source: [], extensions: ["dnd"],
            dnd: {
                preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                autoExpandMS: 400,
                dragStart: function (node, data) {
                    $scope.selected = node.data.item;
                    return true;
                },
                dragEnter: function (node, data) {  return true; },
                dragDrop: function (node, data) {
                    if(!node.data.item.root || data.hitMode !== "before") {
                        if (data.hitMode === "before") {
                            //if placed before -> take the position of that item
                            $scope.selected.position = node.data.item.position;
                            $scope.selected.menuItemParentId = node.parent.key;
                        }
                        else if (data.hitMode === "after") {
                            //if placed after -> take the position after that item
                            $scope.selected.position = node.data.item.position + 1;
                            $scope.selected.menuItemParentId = node.parent.data.item.key;
                        } else if (data.hitMode === "over") {
                            //over a given item -> take the position of first child of that item
                            $scope.selected.position = 0;
                            $scope.selected.menuItemParentId = node.data.item.key;
                        }
                        data.otherNode.moveTo(node, data.hitMode);
                        $scope.saveSelected();
                    }
                }
            }
        });

        var tree = $("#tree").fancytree("getTree");

        $("#tree").bind("fancytreeactivate", function (event, data) {
            $scope.selected = data.node.data.item;
            $scope.error = null;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        });

        $scope.groups = data.groups;
        add(data.root, tree.rootNode);
        tree.rootNode.children[0].setActive(true);
        tree.rootNode.children[0].setExpanded(true);
        $("#tree ul").focus();

        if(window.location.hash) {
            tree.activateKey(window.location.hash.split('#')[1]);
            $('#pageFilesLink').tab('show');
        }
    });

    $scope.updateFilePosition = function(file, newPosition) {
        var msg = { menuItemId: $scope.selected.key, fileId: file.externalId, position: newPosition, name: file.name };
        $scope.error = null;
        $scope.saving = true;
        $http.put($scope.context + "/attachment", msg)
            .success(function (updatedFiles) { $scope.selected.files = updatedFiles; })
            .error($scope.handleError);
    };

    function emptyLocalizedString() {
        var mlsBody = {};
        mlsBody[BennuPortal.locale.tag] = '';
        return mlsBody;
    }

    function initialTitle() {
        var mlsBody = {};
        mlsBody[BennuPortal.locale.tag] = 'New Entry';
        return mlsBody;
    }

    $scope.$watch('files', function() {
        for (var i = 0; i < $scope.files.length; i++) {
            console.log(JSON.stringify($scope.files[i]));
            var file = $scope.files[i];
            $scope.upload = $upload.upload({
                url: $scope.context + '/attachment/' + $scope.selected.key,
                method: 'POST',
                file: file,
                fileName: file.name
            }).progress(function(evt) {
                console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
            }).success(function(data, status, headers, config) {
                console.log('file ' + config.file.name + 'is uploaded successfully. Response: ' + JSON.stringify(data));
                $scope.selected.files = data;
            });
        }
    });

}]);

function i18n(input) {
    return Bennu.localizedString.getContent(input, Bennu.locale);
}