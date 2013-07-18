/*global angular, NGS */
/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
(function (ng, app) {
    'use strict';
    app.controller('ProjectCtrl', [ '$scope', '$rootScope', '$window', 'ajaxService', '$location', 'projectService',
        function ($scope, $rootScope, $window, ajaxService, $location, projectService) {
            projectService.project = $scope.project;

            $scope.data.view = 'samples';
            $scope.data.show = {
              sampleDetails: false
            };

//            $scope.addFilesToSample = function (s) {
//                var fileIndexes = ng.element('input[name="files"]:checked');
//                // Get sample information
//                if (fileIndexes.length) {
//
//                    if (typeof s.data === 'undefined') {
//                        ajaxService.get(s.links.self).then(function (data) {
//                            s.data = data;
//                            addSequenceFileToSample(s, fileIndexes);
//                        });
//                    }
//                    else {
//                        addSequenceFileToSample(s, fileIndexes);
//                    }
//                }
//            };

//            function addSequenceFileToSample(sample, fileIndexes) {
//                var link = sample.data.resource.links['sample/sequenceFiles'];
//                ng.forEach(fileIndexes, function (value) {
//                    var index = $(value).val();
//                    ajaxService.create(link, {
//                        'sequenceFileId': $scope.project.sequenceFiles[index].identifier
//                    }).then(function () {
//                            // Remove from sequenceFile list
//                            $scope.project.sequenceFiles.splice(index, 1);
//                        });
//                });
//                var f = fileIndexes.length > 1 ? "s" : '';
//                $rootScope.$broadcast('NOTIFY', {
//                    'msg': fileIndexes.length + ' file' + f + ' added to ' + sample.label
//                });
//            }

            /**
             * Delete the currently viewed project
             */
            $scope.deleteProject = function () {
                ajaxService.deleteItem($scope.project.links.self).then(function () {
                    $scope.data.show.sampleDetails = false;
                    $rootScope.$broadcast('NOTIFY', {
                        'msg': 'Deleted ' + $scope.project.name,
                        'callback': function () {
                            // TODO: Once undo is set up on the server.
                        }
                    });

                    $location.path('/landing');
                });
            };

            $scope.downloadFile = function (e, fileObject, type) {
                e.preventDefault();
//                $window.open(fileObject.links[type]);
                var sUrl = fileObject.links[type];

                var isChrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
                var isSafari = navigator.userAgent.toLowerCase().indexOf('safari') > -1;

                if (isChrome || isSafari) {
                    //Creating new link node.
                    var link = document.createElement('a');
                    link.href = sUrl;

                    if (link.download !== undefined){
                        //Set HTML5 download attribute. This will prevent file from opening if supported.
                        var fileName = sUrl.substring(sUrl.lastIndexOf('/') + 1, sUrl.length);
                        link.download = fileName;
                    }

                    //Dispatching click event.
                    if (document.createEvent) {
                        var e = document.createEvent('MouseEvents');
                        e.initEvent('click' ,true ,true);
                        link.dispatchEvent(e);
                        return true;
                    }
                }

                // Force file download (whether supported by server).
                var query = '?download';

                window.open(sUrl + query);

            };

            $scope.checkForAllSelected = function (type) {
                var t = ng.element("input[name='" + type + "']").length;
                var c = ng.element("input[name='" + type + "']:checked").length;

                $scope.display[type].checkedCount = c;
                if (t === c) {
                    $scope.display[type].mainCB = true;
                }
                else {
                    $scope.display[type].mainCB = false;
                }
            };

            $scope.display = {
                files: {
                    allCheckboxes: false,
                    mainCB: false,
                    checkedCount: 0
                },
                users: {
                    allCheckboxes: false,
                    mainCB: false,
                    checkedCount: 0
                }
            };

            $scope.checkCount = function (type) {
                return ng.element("input[name='" + type + "']:checked").length;
            };

            $scope.selectAllCheckboxes = function (type, value) {
                ng.forEach(ng.element("input[name='" + type + "']"), function (el) {
                    $(el).prop("checked", value);
                });
                $scope.display[type].checkedCount = ng.element("input[name='" + type + "']:checked").length;
            };


            $scope.changeProjectView = function (view) {
                $location.path('/projects/' + $scope.project.id + '/' + view);
            };

            $scope.removeItemFromProject = function (type) {
                var l = ng.element("input[name='" + type + "']:checked");
                ng.forEach(l, function (item) {
                    var index = $(item).val();
                    ajaxService.deleteItem($scope.project.users[index].links.relationship).then(function () {
                        $scope.project.users.splice(index, 1);
                        $rootScope.$broadcast('NOTIFY', {
                            'msg': 'Deleted ' + type
                        });
                    });
                });
            };

            $scope.gotoUser = function (e, user) {
                e.preventDefault();
                var u = user.match(/\/users\/.*/);
                $location.path(u[0]);
            };
        }
    ]);
})(angular, NGS);
