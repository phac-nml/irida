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
      $scope.show = {sequenceOptions: 0}; // TODO: (Josh: 2013-06-24) Maybe rename this variable 
//      $scope.allFiles = false;
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
        var fileIndexes = angular.element('input[name="selectedFiles"]:checked');
        // Get sample information
        if (fileIndexes) {

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
        angular.forEach(fileIndexes, function (value) {
          var index = $(value).val();
          ajaxService.create(link, {
            'sequenceFileId': $scope.project.sequenceFiles[index].identifier
          }).then(function () {
              // Remove from sequenceFile list
              $scope.project.sequenceFiles.splice(index, 1);
            });
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
        ajaxService.deleteItem($scope.project.links.self).then(function () {
          $rootScope.$broadcast('PROJECT_DELETED', {
            'name': $scope.project.name
          });
          $location.path('/');
        });
      };

      $scope.downloadFile = function (e, url, type) {
        e.preventDefault();
        console.log(url);
        $window.open(url.links.self + '.' + type, '_blank');
      };

      $scope.checkSelectedFiles = function () {
        $scope.show.sequenceOptions = angular.element('input[name=\'selectedFiles\']:checked').length;
      };

      $scope.modifyAllCbSelection = function () {
        $scope.allFiles = !$scope.allFiles;
//        $scope.show.sequenceOptions = angular.element('input[name=\'selectedFiles\']:checked').length;
        if ($scope.allFiles) {
          $scope.show.sequenceOptions = 100;
        }
        else {
          $scope.show.sequenceOptions = 0;
        }
      };
    }
  ]);
})(angular, NGS);