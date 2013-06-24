/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.controller('ProjectCtrl', [ '$scope', '$rootScope', 'ajaxService', '$location',
    function ($scope, $rootScope, ajaxService, $location) {
      $scope.show = {sequenceOptions: 0};
      $scope.allFiles = false;
      $scope.sample = {};
      $scope.samples = {};
      $scope.list2 = [ ];
      var detailFileDrag = false;

      $scope.addFileToSample = function (evt, ui, url) {
        $scope.dragOverOut(evt);
        addSequenceFileToSample(url);
      };

      $scope.addFileToDetailSample = function (evt, ui) {
        // Need to know if this is a drop to self list
        if (detailFileDrag) {
          $scope.sample.sequenceFiles = $scope.sample.sequenceFiles2.slice(0);
          detailFileDrag = false;
        }
        else {
          addSequenceFileToSample($scope.sample.addUrl, function () {
            getSequnceFilesForSample($scope.sample.addUrl);
          });
        }
      };

      function addSequenceFileToSample(url, callback) {
        ajaxService.create(url, {
          'sequenceFileId': $scope.list2[ 0 ].identifier
        }).then(function () {
            $scope.list2 = [ ];
            if (typeof callback === 'function') {
              callback();
            }
          });
      }

      function getSequnceFilesForSample(url) {
        ajaxService.get(url).then(function (data) {
          $scope.sample.sequenceFiles = data.resource.resources;
          $scope.sample.sequenceFiles2 = data.resource.resources;
        });
      }

      $scope.getSequenceFiles = function (sample) {
        // TODO: (Josh: 2013-06-18) Add loading spinner!
        $scope.sample = {
          name: sample.label,
          addUrl: sample.links['sample/sequenceFiles'],
          sequenceFiles: [],
          sequenceFiles2: [],
          details: true
        };

        getSequnceFilesForSample(sample.links['sample/sequenceFiles']);
      };

      $scope.fileDrag = function (evt) {
        $(evt.target).toggleClass('project__file--drag');
      };

      $scope.dragOverOut = function (evt) {
        $(evt.target).find('.folder').toggleClass('folder--draghover');
      };

      $scope.detailFileDrag = function (evt, ui, file) {
        detailFileDrag = true;
      };

      $scope.detailFileDrop = function () {
//        detailFileDrag = false;
      };

      $scope.deleteProject = function () {
        ajaxService.deleteItem($scope.project.links.self).then(function () {
          $rootScope.$broadcast('PROJECT_DELETED', {
            'name': $scope.project.name
          });
          $location.path('/');
        });
      };

      // NEW
      $scope.downloadFile = function (ev, url) {
        ev.preventDefault();
        console.log(url);
        ajaxService.getFastaFile(url);
      };

      $scope.checkSelectedFiles = function () {
        $scope.show.sequenceOptions = angular.element('input[name=\'selectedFiles\']:checked').length;
      };

      $scope.modifyAllCbSelection = function () {
        $scope.allFiles = !$scope.allFiles;
//        $scope.show.sequenceOptions = angular.element('input[name=\'selectedFiles\']:checked').length;
        if($scope.allFiles) {
          $scope.show.sequenceOptions = 100;
        }
        else {
          $scope.show.sequenceOptions = 0;
        }
      };
    }
  ]);
})(angular, NGS);