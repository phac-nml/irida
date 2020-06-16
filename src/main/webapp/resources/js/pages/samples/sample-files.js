const angular = require("angular");
import "angular-ui-bootstrap";
import { convertFileSize } from "../../utilities/file-utilities";

require("../../../css/pages/sample-files.css");

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

  this.deleteFile = function () {
    $uibModalInstance.close();
  };

  this.cancel = function () {
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
  this.deleteFile = function (id, label) {
    $uibModal.open({
      templateUrl: "/confirm.html",
      controller: "FileDeletionController as deleteCtrl",
      resolve: {
        id: function () {
          return id;
        },
        label: function () {
          return label;
        },
      },
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
  this.deleteFile = function (id, label) {
    $uibModal.open({
      templateUrl: "/confirm_assembly.html",
      controller: "FileDeletionController as deleteCtrl",
      resolve: {
        id: function () {
          return id;
        },
        label: function () {
          return label;
        },
      },
    });
  };
}

const filesModule = angular
  .module("irida.sample.files", ["ui.bootstrap"])
  .filter("humanReadableBytes", humanReadableBytes)
  .controller("FileController", ["$uibModal", FileController])
  .controller("AssemblyFileController", ["$uibModal", AssemblyFileController])
  .controller("FileDeletionController", [
    "$uibModalInstance",
    "id",
    "label",
    FileDeletionController,
  ]).name;

angular.module("irida").requires.push(filesModule);
