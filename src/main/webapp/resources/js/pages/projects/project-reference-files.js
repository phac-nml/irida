import angular from "angular";
import "./../../modules/utilities/file.utils";
import { showNotification } from "../../modules/notifications";

/**
 * Angular service for project files.
 * @param {object} $rootScope angular DOM rootscope
 * @param {object} $http angular http provider
 * @constructor
 */
function ProjectFileService($rootScope, $http) {
  "use strict";
  const svc = this;
  svc.files = [];

  svc.getFiles = function getFiles() {
    return $http.get(window.PAGE.urls.get).then(function(response) {
      angular.copy(response.data.files, svc.files);
    });
  };

  $rootScope.$on("FILE_DELETED", function(e, args) {
    const filtered = svc.files.filter(file => args.id !== file.id);
    angular.copy(filtered, svc.files);
  });

  $rootScope.$on("NEW_FILE", function(e, args) {
    args.file.newFile = true;
    svc.files.push(args.file);
  });
}

/**
 * Angular Service for Reference Files.
 * @param {object} $rootScope angular DOM root scope
 * @param {object} $uibModal angular-ui modal
 * @param {object} $http angular http object
 * @constructor
 */
function ReferenceFileService($rootScope, $uibModal, $http) {
  "use strict";
  const svc = this;

  svc.deleteFile = function(file) {
    const modalInstance = $uibModal.open({
      templateUrl: "/delete-modal.html",
      controller: "DeleteCtrl as dCtrl",
      resolve: {
        file: function() {
          return file;
        }
      }
    });

    modalInstance.result.then(function() {
      $http({
        method: "POST",
        url: window.PAGE.urls.remove,
        data: $.param({
          fileId: file.id,
          projectId: window.project.id
        }),
        headers: { "Content-Type": "application/x-www-form-urlencoded" }
      }).then(function(response) {
        showNotification({
          text: response.data.msg,
          type: response.data.result
        });
        $rootScope.$broadcast("FILE_DELETED", { id: file.id });
      });
    });
  };
}

/**
 * Angular controller for deleting a file.
 * @param {object} $uibModalInstance angular-ui modal
 * @param {object} file to delete
 * @constructor
 */
function DeleteCtrl($uibModalInstance, file) {
  "use strict";
  this.file = file;

  this.delete = function() {
    $uibModalInstance.close();
  };

  this.close = function() {
    $uibModalInstance.dismiss();
  };
}

/**
 * Angular controller for all files on the page.
 * @param {object} ProjectFileService for files
 * @param {object} projectFilesService service for reference files.
 * @constructor
 */
function FilesCtrl(ProjectFileService, projectFilesService) {
  "use strict";

  this.getRowClass = function(file) {
    let rowClass = "";
    if (!angular.isNumber(file.size)) {
      rowClass = "danger";
    } else if (file.newFile) {
      rowClass = "success";
    }
    return rowClass;
  };

  this.deleteFile = function(file) {
    projectFilesService.deleteFile(file);
  };

  ProjectFileService.getFiles().then(() => {
    this.files = ProjectFileService.files;
  });
}

/**
 * Angular controller for uploading reference file.
 * @param {object} $timeout angular window timeout object
 * @param {object} fileService service for files.
 * @constructor
 */
function FileUploadCtrl($timeout, fileService) {
  var vm = this;

  vm.onFileSelect = function($files) {
    if ($files && $files.length) {
      fileService.upload(window.PAGE.urls.upload, $files).then(function() {
        $timeout(function() {
          window.location.href = window.location.href;
        }, 500);
      });
    }
  };
}

const refModule = angular
  .module("References", ["file.utils"])
  .service("ProjectFileService", ["$rootScope", "$http", ProjectFileService])
  .service("ReferenceFileService", [
    "$rootScope",
    "$uibModal",
    "$http",
    ReferenceFileService
  ])
  .controller("FilesCtrl", [
    "ProjectFileService",
    "ReferenceFileService",
    FilesCtrl
  ])
  .controller("DeleteCtrl", ["$uibModalInstance", "file", DeleteCtrl])
  .controller("FileUploadCtrl", ["$timeout", "FileService", FileUploadCtrl])
  .name;

angular.module("irida").requires.push(refModule);
