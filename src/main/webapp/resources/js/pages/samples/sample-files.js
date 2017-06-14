const angular = require('angular');
require('./../../modules/utilities/file.utils');
require('./../../../css/pages/sample_files.css');

/**
 * Controller for the modal to confirm removing a sequenceFile
 *
 * @param {Object} $uibModalInstance Handle on the current modal
 * @param {long} id id for the file to delete.
 * @param {String} label Name of the file to remove from the Sample.
 * @constructor
 */
function FileDeletionController($uibModalInstance, id, label) {
  this.fileId = id;
  this.label = label;

  this.deleteFile = function() {
    $uibModalInstance.close();
  };

  this.cancel = function() {
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
  /**
   * Click handler for the download button for a sequenceFile
   *
   * @param {long} objectId Id for the sample?
   * @param {long} id Id for the sequenceFile to download
   */
  this.download = function(objectId, id) {
    const url = window.PAGE.URLS.sequenceFile
      .replace('OBJECT_ID', objectId)
      .replace('FILE_ID', id);
    fileService.download(url);
  };

  /**
   * Click handler for the delete button for a sequenceFile
   *  Displays a confirmation modal
   *
   * @param {long} id Id for the sequenceFile to delete
   * @param {String} label Name of the sequenceFile to delete
   */
  this.deleteFile = function(id, label) {
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
 * @param {object} Upload angular file upload
 * @param {object} $timeout angular window timeout
 * @param {object} $window angular window object
 * @param {object} $uibModal angular-ui modal
 * @constructor
 */
function FileUploadController(Upload, $timeout, $window, $uibModal) {
  const vm = this;
  let fileUpload;

  /**
   * Upload good sequence files to the server.
   * @param {list} files of files to send.
   */
  function uploadGoodFiles(files) {
    vm.uploading = true;

    $window.onbeforeunload = function() {
      return $window.PAGE.i18n.leaving;
    };

    fileUpload = Upload.upload({
      url: $window.PAGE.URLS.upload,
      data: {
        files: files
      },
      arrayKey: ''
    }).then(function() {
      $window.onbeforeunload = undefined;
      $timeout(function() {
        vm.uploading = false;
        $window.location.reload();
        vm.processing = false;
      }, 100);
    }, function(data) {
      $window.onbeforeunload = undefined;
      vm.processing = false;
      vm.uploading = false;
      vm.errorMessage = data.error_message;
    }, function(evt) {
      vm.progress = parseInt(100.0 * evt.loaded / evt.total, 0);
      if (vm.progress >= 99) {
        vm.uploading = false;
        vm.processing = true;
      }
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
        controller: ['$uibModalInstance', 'rejects', 'files', function($uibModalInstance, rejects, files) {
          var vm = this;
          vm.rejects = rejects;
          vm.good = files;

          vm.cancel = function() {
            $uibModalInstance.dismiss();
          };

          vm.finish = function() {
            $uibModalInstance.close(files.filter(file => {
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
