(function(angular, TL, PAGE) {
  'use strict';
  var UPLOAD_ERROR = 'upload_error';

  /**
   * Services to call API for SequenceFile
   * @param $rootScope
   * @param upload
   * @returns {{download: download, upload: uploadFiles}}
   * @constructor
   */
  function FileService($rootScope, upload) {
    return {
      download: download,
      upload: uploadFiles
    };

    /**
     * Creates a new iFrame to download a file into.
     * @param id Id for the sequenceFile to download
     */
    function download(id) {
      var iframe = document.createElement('iframe');
      iframe.src = TL.BASE_URL + 'sequenceFiles/download/' + id;
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
    }

    /**
     * Uploads list of files
     * @param files Array of files.
     */
    function uploadFiles(files) {
      if (files && files.length) {
        upload.upload({
            url: TL.BASE_URL + 'samples/' + PAGE.sample.id + '/sequenceFiles/upload',
            file: files
          })
          .success(function() {
            window.location.href = window.location.href;
          })
          .error(function() {
            $rootScope.$broadcast(UPLOAD_ERROR);
          });
      }
    }
  }

  /**
   * Directive to show error message if files fail to upload.
   * @returns {{restrict: string, templateUrl: string, controllerAs: string, controller: *[]}}
   */
  function uploadError() {
    return {
      restrict: 'E',
      templateUrl: '/upload-error.html',
      controllerAs: 'errorCtrl',
      controller: ['$scope', function(scope) {
        var vm = this;
        vm.showError = false;
        scope.$on(UPLOAD_ERROR, function() {
          vm.showError = true;
        });
      }]
    };
  }

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
      fileService.download(id);
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
      'controller': ['$modal', 'FileService', function($modal, fileService) {
        var vm = this;
        vm.files = [];
        /**
         * Open the modal for file selection
         */
        vm.open = function() {
          $modal.open({
              animation: true,
              templateUrl: '/upload.html',
              controllerAs: 'modalCtrl',
              controller: 'UploadModalController'
            })
            .result.then(function(files) {
              fileService.upload(files);
            });
        };
      }]

    };
  }

  /**
   * Controller for the modal to upload sequence files.
   * @param $modalInstance
   * @constructor
   */
  function UploadModalController($modalInstance) {
    var vm = this;
    vm.files = [];
    vm.rejects = [];

    /**
     * Add files to a list of files to upload to the server.
     * @param files Array of file objects.
     * @param e event triggering.
     * @param rejects Array of files that do not match the requirements.
     */
    vm.addFiles = function(files, e, rejects) {
      // Filter to ensure that we do not upload the same file twice.
      vm.files = vm.files.concat(files.filter(function(file) {
        return (vm.files.filter(function(f) {
          return (f.path !== file.path);
        }).length === 0);
      }));

      vm.rejects = vm.rejects.concat(rejects.filter(function(reject) {
        return reject.type !== 'directory';
      }));
    };

    /**
     * Remove a file from the list of files to upload
     * @param file
     */
    vm.remove = function(file) {
      vm.files = vm.files.filter(function(f) {
        return f !== file;
      });
    };

    /**
     * Trigger the upload of files.
     */
    vm.ok = function() {
      if (vm.files.length > 0) {
        $modalInstance.close(vm.files);
      }
    };

    /**
     * Clears the upload list and closes the modal.
     */
    vm.cancel = function() {
      $modalInstance.dismiss();
    };
  }

  /**
   * Filter for formatting the files size.
   * @returns {Function}
   */
  function humanReadableBytes() {
    return function(bytes) {
      var thresh = 1024;
      if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
      }
      var units = ['kB', 'MB', 'GB'];
      var u = -1;
      do {
        bytes /= thresh;
        ++u;
      } while (Math.abs(bytes) >= thresh && u < units.length - 1);
      return bytes.toFixed(1) + ' ' + units[u];
    };
  }

  angular.module('irida.sample.files', ['ui.bootstrap', 'ngFileUpload'])
    .filter('humanReadableBytes', [humanReadableBytes])
    .directive('fileUpload', [fileUpload])
    .directive('uploadError', [uploadError])
    .controller('FileController', ['FileService', '$modal', FileController])
    .controller('FileDeletionController', ['$modalInstance', 'id', 'label', FileDeletionController])
    .controller('UploadModalController', ['$modalInstance', UploadModalController])
    .factory('FileService', ['$rootScope', 'Upload', FileService]);
})(window.angular, window.TL, window.PAGE);