(function(angular, TL, PAGE) {
  'use strict';

  /**
   * Controller for the modal to confirm removing a sequenceFile
   *
   * @param {Object} $uibModalInstance Handle on the current modal
   * @param {long} id id for the file to delete.
   * @param {String} label Name of the file to remove from the Sample.
   * @constructor
   */
  function FileDeletionController($uibModalInstance, id, label) {
    var vm = this;
    vm.fileId = id;
    vm.label = label;

    vm.deleteFile = function() {
      $uibModalInstance.close();
    };

    vm.cancel = function() {
      $uibModalInstance.dismiss();
    };
  }

  /**
   * Controller for the Buttons in the list of sequence files.
   *
   * @param {Object} fileService Service for API calls for sequenceFiles
   * @param {Object} $uibModal Angular modal
   * @constructor
   */
  function FileController(fileService, $uibModal) {
    var vm = this;

    /**
     * Click handler for the download button for a sequenceFile
     *
     * @param {long} id Id for the sequenceFile to download
     */
    vm.download = function(id) {
      var url = TL.BASE_URL + 'sequenceFiles/download/' + id;
      fileService.download(url);
    };

    /**
     * Click handler for the delete button for a sequenceFile
     *  Displays a confirmation modal
     *
     * @param {long} id Id for the sequenceFile to delete
     * @param {String} label Name of the sequenceFile to delete
     */
    vm.deleteFile = function(id, label) {
      $uibModal.open({
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
   * Controller to handle uploading new sequence files
   *
   * @param {Obect} Upload
   * @param {Object} $timeout
   * @param {Object} $window
   * @constructor
   */
  function FileUploadController(Upload, $timeout, $window, $uibModal) {
    var vm = this,
      fileUpload = undefined,
      url = TL.BASE_URL + 'samples/' + PAGE.sample.id +
      '/sequenceFiles/upload';

    function uploadGoodFiles(files) {
      vm.uploading = true;

      $window.onbeforeunload = function() {
        return PAGE.i18n.leaving;
      };

      fileUpload = Upload.upload({
        url: url,
        file: files
      }).progress(function(evt) {
        vm.progress = parseInt(100.0 * evt.loaded / evt.total);
        if (vm.progress >= 99) {
          vm.uploading = false;
          vm.processing = true;
        }
      }).success(function() {
        $window.onbeforeunload = undefined;
        $timeout(function() {
          vm.uploading = false;
          // TODO: This should be an ajax refresh of the files table.
          $window.location.reload();
          vm.processing = false;
        }, 100);
      }).error(function(data) {
        $window.onbeforeunload = undefined;
        vm.processing = false;
        vm.uploading = false;
        vm.errorMessage = data.error_message;
      });
    }

    vm.uploadFiles = function($files, $event, $rejectedFiles) {
      if ($files.length === 0 && $rejectedFiles.length === 0) {
        return;
      }

      if ($rejectedFiles && $rejectedFiles.length > 0) {
        $uibModal.open({
          animation: true,
          templateUrl: '/upload-error.html',
          controllerAs: 'rejectModalCtrl',
          controller: ['$uibModalInstance', 'rejects', 'files', function(
            $uibModalInstance, rejects, files) {
            var vm = this;
            vm.rejects = rejects;
            vm.good = files;

            vm.cancel = function() {
              $uibModalInstance.dismiss();
            };

            vm.finish = function() {
              $uibModalInstance.close(_.filter(files, function(file) {
                return file.selected;
              }));
            };
          }],
          resolve: {
            rejects: function() {
              return $rejectedFiles;
            },
            files: function() {
              return $files;
            }
          }
        }).result.then(function(files) {
          uploadGoodFiles(files);
        });
      } else {
        uploadGoodFiles($files);
      }
    };

    vm.cancel = function() {
      if (fileUpload !== undefined) {
        fileUpload.abort();
        fileUpload = undefined;
        vm.uploading = false;
      }
    };

  }

  angular.module('irida.sample.files', ['ngAnimate', 'ui.bootstrap',
      'file.utils', 'ngFileUpload'
    ])
    .controller('FileUploadController', ['Upload', '$timeout', '$window',
      '$uibModal', FileUploadController
    ])
    .controller('FileController', ['FileService', '$uibModal', FileController])
    .controller('FileDeletionController', ['$uibModalInstance', 'id', 'label',
      FileDeletionController
    ]);
})(window.angular, window.TL, window.PAGE);
