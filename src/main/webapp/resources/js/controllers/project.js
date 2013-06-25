/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.controller('ProjectCtrl', [ '$scope', '$rootScope', '$window', 'ajaxService', '$location',
    function ($scope, $rootScope, $window, ajaxService, $location) {
      $scope.sample = {};
      $scope.samples = {};
//      $scope.list2 = [ ];
//      var detailFileDrag = false;

//      $scope.addFileToSample = function (evt, ui, url) {
//        $scope.dragOverOut(evt);
//        addSequenceFileToSample(url);
//      };
//
//      $scope.addFileToDetailSample = function (evt, ui) {
//        // Need to know if this is a drop to self list
//        if (detailFileDrag) {
//          $scope.sample.sequenceFiles = $scope.sample.sequenceFiles2.slice(0);
//          detailFileDrag = false;
//        }
//        else {
//          addSequenceFileToSample($scope.sample.addUrl, function () {
//            getSequnceFilesForSample($scope.sample.addUrl);
//          });
//        }
//      };

      $scope.addFilesToSample = function (s) {
        debugger;
        var fileIndexes = ng.element('input[name="files"]:checked');
        // Get sample information
        if (fileIndexes.length) {

          if (typeof s.data === 'undefined') {
            ajaxService.get(s.links.self).then(function (data) {
              s.data = data;
              addSequenceFileToSample(s, fileIndexes);
            });
          }
          else {
            addSequenceFileToSample(s, fileIndexes);
          }
        }
      };

      function addSequenceFileToSample(sample, fileIndexes) {
        var link = sample.data.resource.links['sample/sequenceFiles'];
        ng.forEach(fileIndexes, function (value) {
          var index = $(value).val();
          ajaxService.create(link, {
            'sequenceFileId': $scope.project.sequenceFiles[index].identifier
          }).then(function () {
              // Remove from sequenceFile list
              $scope.project.sequenceFiles.splice(index, 1);
            });
        });
        var f = fileIndexes.length > 1 ? "s" : '';
        $rootScope.$broadcast('NOTIFY', {
          'msg': fileIndexes.length + ' file' + f + ' added to ' + sample.label
        });
      }

//      function getSequnceFilesForSample(url) {
//        ajaxService.get(url).then(function (data) {
//          $scope.sample.sequenceFiles = data.resource.resources;
//          $scope.sample.sequenceFiles2 = data.resource.resources;
//        });
//      }

//      $scope.getSequenceFiles = function (sample) {
//        // TODO: (Josh: 2013-06-18) Add loading spinner!
//        $scope.sample = {
//          name: sample.label,
//          addUrl: sample.links['sample/sequenceFiles'],
//          sequenceFiles: [],
//          sequenceFiles2: [],
//          details: true
//        };
//
//        getSequnceFilesForSample(sample.links['sample/sequenceFiles']);
//      };

//      $scope.fileDrag = function (evt) {
//        $(evt.target).toggleClass('project__file--drag');
//      };
//
//      $scope.dragOverOut = function (evt) {
//        $(evt.target).find('.folder').toggleClass('folder--draghover');
//      };
//
//      $scope.detailFileDrag = function (evt, ui, file) {
//        detailFileDrag = true;
//      };

//      $scope.detailFileDrop = function () {
////        detailFileDrag = false;
//      };

      /**
       * Delete the currently viewed project
       */
      $scope.deleteProject = function () {
        ajaxService.deleteItem($scope.project.links.self).then(function (data) {
          $rootScope.$broadcast('NOTIFY', {
            'msg': 'Deleted ' + $scope.project.name,
            'callback': function () {
              alert("THIS NEEDS TO BE IMPLEMENTED");
            }
          });

          $location.path('/landing');
        });
      };

      $scope.downloadFile = function (e, fileObject, type) {
        e.preventDefault();
        $window.open(fileObject.links[type], '_blank');
      };

      $scope.checkForAllSelected = function (type) {
        var t = ng.element("input[name='" + type +"']").length;
        var c = ng.element("input[name='" + type +"']:checked").length;

        $scope.display[type].checkedCount = c;
        if(t === c){
          $scope.display[type].mainCB = true;
        }
        else {
          $scope.display[type].mainCB = false;}
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

      $scope.modifyDisplayOptions = function (type) {
        $scope.display[type].allCheckboxes = !$scope.display[type].allCheckboxes;
        if ($scope.display[type].allCheckboxes) {
          $scope.display[type].mainCB = true;
          $scope.display[type].checkedCount = 100;
        }
        else {
          $scope.display[type].mainCB = false;
          $scope.display[type].checkedCount = 0;
        }
      };

      $scope.removeItemFromProject = function(type) {
        var l = ng.element("input[name='" + type +"']:checked");
        ng.forEach(l, function(item) {
          var index = $(item).val();
          ajaxService.deleteItem($scope.project.users[index].links.relationship).then(function () {
            $scope.project.users.splice(index, 1);
          });
        });
      };

      $scope.gotoUser = function (e, user) {
        e.preventDefault();
        var u = user.match(/\/users\/.*/);
        $location.path(u[0]);
      };

      $scope.gotoProject = function (e, url) {
        e.preventDefault();
        var l = url.match(/\/projects\/.*/);
        $location.path(l[0]);
      };
    }
  ]);
})(angular, NGS);