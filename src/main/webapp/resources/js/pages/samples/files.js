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
   * Controller to handle uploading new sequence files
   * @param Upload
   * @param $timeout
   * @param $window
   * @constructor
   */
  function FileUploadController(Upload, $timeout, $window, $modal) {
    var vm = this,
        fileUpload = undefined,
    url = TL.BASE_URL + 'samples/' + PAGE.sample.id + '/sequenceFiles/upload';

    function uploadGoodFiles(files) {
      vm.uploading = true;

      $window.onbeforeunload = function() {
        return PAGE.i18n.leaving;
      };

      fileUpload = Upload.upload({
        url: url,
        file: files
      }).progress(function (evt) {
        vm.progress = parseInt(100.0 * evt.loaded / evt.total);
        if(vm.progress >= 99) {
          vm.uploading = false;
          vm.processing = true;
        }
      }).success(function () {
        $window.onbeforeunload = undefined;
        $timeout(function () {
          vm.uploading = false;
          // TODO: This should be an ajax refresh of the files table.
          $window.location.reload();
          vm.processing = false;
        }, 100);
      }).error(function (data) {
        $window.onbeforeunload = undefined;
        vm.processing = false;
        vm.uploading = false;
        vm.errorMessage = data.error_message;
      });
    }

    vm.uploadFiles = function($files, $event, $rejectedFiles) {
      if($files.length === 0 && $rejectedFiles.length === 0 ) {
        return;
      }

      if($rejectedFiles && $rejectedFiles.length > 0) {
        $modal.open({
          animation: true,
          templateUrl: '/upload-error.html',
          controllerAs: 'rejectModalCtrl',
          controller: ['$modalInstance', 'rejects', 'files', function ($modalInstance, rejects, files) {
            var vm = this;
            vm.rejects = rejects;
            vm.good = files;

            vm.cancel = function () {
              $modalInstance.dismiss();
            };

            vm.finish = function() {
              $modalInstance.close(_.filter(files, function (file) {
                return file.selected;
              }));
            };
          }],
          resolve: {
            rejects: function () {
              return $rejectedFiles;
            },
            files: function () {
              return $files;
            }
          }
        }).result.then(function (files) {
          uploadGoodFiles(files);
        });
      }
      else {
        uploadGoodFiles($files);
      }
    };

    vm.cancel = function () {
      if(fileUpload !== undefined) {
        fileUpload.abort();
        fileUpload = undefined;
        vm.uploading = false;
      }
    };

    vm.closeFastqWarning = function() {

    };
  }

  angular.module('irida.sample.files', ['ngAnimate', 'ui.bootstrap', 'file.utils', 'ngFileUpload'])
    .controller('FileUploadController', ['Upload', '$timeout', '$window', '$modal', FileUploadController])
    .controller('FileController', ['FileService', '$modal', FileController])
    .controller('FileDeletionController', ['$modalInstance', 'id', 'label', FileDeletionController]);
})(window.angular, window.TL, window.PAGE);
