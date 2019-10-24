const angular = require("angular");
import "angular-ui-bootstrap";
require("ng-file-upload");
import { convertFileSize } from "../../utilities/file.utilities";
require("../../../sass/pages/sample-files.scss");

/**
 * Filter for formatting the files size.
 * @return {function} filter for file size
 */
function humanReadableBytes() {
  return convertFileSize;
}

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
 * @param {Object} $uibModal Angular modal
 * @constructor
 */
function FileController($uibModal) {
  /**
   * Click handler for the delete button for a sequenceFile
   *  Displays a confirmation modal
   *
   * @param {long} id Id for the sequenceFile to delete
   * @param {String} label Name of the sequenceFile to delete
   */
  this.deleteFile = function(id, label) {
    $uibModal.open({
      templateUrl: "/confirm.html",
      controller: "FileDeletionController as deleteCtrl",
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
 * Controller for the Buttons in the list of assembly files.
 *
 * @param {Object} $uibModal Angular modal
 * @constructor
 */
function AssemblyFileController($uibModal) {
  /**
   * Click handler for the delete button for an assembly
   *  Displays a confirmation modal
   *
   * @param {long} id Id for the assembly to delete
   * @param {String} label Name of the assembly to delete
   */
  this.deleteFile = function(id, label) {
    $uibModal.open({
      templateUrl: "/confirm_assembly.html",
      controller: "FileDeletionController as deleteCtrl",
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
      arrayKey: ""
    });

    fileUpload.then(
      function() {
        $window.onbeforeunload = undefined;
        $timeout(function() {
          vm.uploading = false;
          $window.location.reload();
          vm.processing = false;
        }, 100);
      },
      function(data) {
        $window.onbeforeunload = undefined;
        vm.processing = false;
        vm.uploading = false;
        vm.errorMessage = data.error_message;
      },
      function(evt) {
        vm.progress = parseInt((100.0 * evt.loaded) / evt.total, 0);
        if (vm.progress >= 99) {
          vm.uploading = false;
          vm.processing = true;
        }
      }
    );
  }

  vm.uploadFiles = function($files) {
    console.log(arguments);

    if ($files.length === 0) {
      return;
    }

    // Check to make sure the files are the right format.
    const fastqregex = /\.fastq(\/gz)?/;
    const goodFiles = [];
    const badFiles = [];
    for (const file of $files) {
      if (file.name.match(fastqregex) === null) {
        badFiles.push(file);
      } else {
        goodFiles.push(file);
      }
    }

    if (badFiles.length > 0) {
      $uibModal
        .open({
          animation: true,
          templateUrl: "/upload-error.html",
          controllerAs: "rejectModalCtrl",
          controller: [
            "$uibModalInstance",
            function($uibModalInstance) {
              const vm = this;
              vm.rejects = badFiles;
              vm.good = goodFiles;

              vm.cancel = function() {
                $uibModalInstance.dismiss();
              };

              vm.finish = function() {
                $uibModalInstance.close(
                  goodFiles.filter(file => {
                    return file.selected;
                  })
                );
              };
            }
          ],
          resolve: {
            rejects: function() {
              return badFiles;
            },
            files: function() {
              return goodFiles;
            }
          }
        })
        .result.then(function(files) {
          uploadGoodFiles(files);
        });
    } else {
      uploadGoodFiles($files);
    }
  };

  vm.cancel = function() {
    if (typeof fileUpload !== "undefined") {
      fileUpload.abort();
      fileUpload = undefined;
      vm.uploading = false;
    }
  };
}

const filesModule = angular
  .module("irida.sample.files", ["ui.bootstrap", "ngFileUpload"])
  .filter("humanReadableBytes", humanReadableBytes)
  .controller("FileUploadController", [
    "Upload",
    "$timeout",
    "$window",
    "$uibModal",
    FileUploadController
  ])
  .controller("FileController", ["$uibModal", FileController])
  .controller("AssemblyFileController", ["$uibModal", AssemblyFileController])
  .controller("FileDeletionController", [
    "$uibModalInstance",
    "id",
    "label",
    FileDeletionController
  ]).name;

angular.module("irida").requires.push(filesModule);
