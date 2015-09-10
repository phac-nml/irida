(function(angular, TL, PAGE) {
  'use strict';

  /**
   * Controller for the modal to confirm removing a sequenceFile
   * @param $modalInstance Handle on the current modal
   * @param id id for the file to delete.
   * @param label Name of the file to remove from the Sample.
   * @constructor
   */
  function FileDeletionController($modalInstance, id, label) {
    var vm = this;
    vm.fileId = id;
    vm.label = label;

    vm.deleteFile = function() {
      $modalInstance.close();
    };

    vm.cancel = function() {
      $modalInstance.dismiss();
    };
  }

  /**
   * Controller for the Buttons in the list of sequence files.
   * @param fileService Service for API calls for sequenceFiles
   * @param $modal Angular modal
   * @constructor
   */
  function FileController(fileService, $modal) {
    var vm = this;

    /**
     * Click handler for the download button for a sequenceFile
     * @param id Id for the sequenceFile to download
     */
    vm.download = function(id) {
      var url = TL.BASE_URL + 'sequenceFiles/download/' + id;
      fileService.download(url);
    };

    /**
     * Click handler for the delete button for a sequenceFile
     *  Displays a confirmation modal
     * @param id Id for the sequenceFile to delete
     * @param label Name of the sequenceFile to delete
     */
    vm.deleteFile = function(id, label) {
      $modal.open({
        templateUrl: '/confirm.html',
        controller: 'FileDeletionController as deleteCtrl',
        resolve: {
          id: function() {
            return id;
          },
          label: function() {
            return label;
          }
        }
      });
    };
    
    vm.deletePair = function(id, label1, label2) {
      $modal.open({
        templateUrl: '/confirm_pair.html',
        controller: 'FileDeletionController as deleteCtrl',
        resolve: {
          id: function() {
            return id;
          },
          label: function() {
            return label1 + ", " + label2;
          }
        }
      });
    };
  }

  /**
   * Directive to open the file chooser for selecting sequence files.
   * @returns {{restrict: string, template: string, controllerAs: string, controller: *[]}}
   */
  function fileUpload() {
    return {
      'restrict': 'E',
      'templateUrl': '/upload-btn.html',
      'controllerAs': 'uploadCtrl',
      'controller': ['$scope', '$timeout', '$modal', 'FileService', function($scope, $timeout, $modal, fileService) {
        var vm = this;
        vm.files = [];
        /**
         * Open the modal for file selection
         */
        vm.open = function() {
          $scope.$broadcast('NEW_UPLOAD');

          $modal.open({
              animation: true,
              templateUrl: '/upload.html',
              controllerAs: 'modalCtrl',
              controller: 'UploadModalController'
            })
            .result.then(function(files) {
              var url = TL.BASE_URL + 'samples/' + PAGE.sample.id + '/sequenceFiles/upload';
              fileService.uploadBulk(url, files).then(function() {
                $timeout(function () {
                  window.location.href = window.location.href;
                }, 500);
              });
            });
        };
      }]

    };
  }

  /**
   * Controller to handle uploading new sequence files
   * @param Upload
   * @param $timeout
   * @param $window
   * @constructor
   */
  function FileUploadController(Upload, $timeout, $window) {
    var vm = this,
    url = TL.BASE_URL + 'samples/' + PAGE.sample.id + '/sequenceFiles/upload';
    vm.uploading = false;

    vm.uploadFiles = function($files) {
      if(!$files || $files.length === 0) {return;}

      vm.uploading = true;
      Upload.upload({
        url: url,
        file: $files
      }).progress(function (evt) {
        vm.progress = parseInt(100.0 * evt.loaded / evt.total);
      }).success(function () {
        $timeout(function () {
          vm.uploading = false;
          // TODO: This should be an ajax refresh of the files table.
          $window.location.reload();
        }, 2000);
      });
    };
  }

  angular.module('irida.sample.files', ['ngAnimate', 'ui.bootstrap', 'file.utils', 'ngFileUpload'])
    .directive('fileUpload', [fileUpload])
    .controller('FileUploadController', ['Upload', '$timeout', '$window', FileUploadController])
    .controller('FileController', ['FileService', '$modal', FileController])
    .controller('FileDeletionController', ['$modalInstance', 'id', 'label', FileDeletionController]);
})(window.angular, window.TL, window.PAGE);