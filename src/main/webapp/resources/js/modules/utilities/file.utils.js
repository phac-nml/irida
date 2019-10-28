import find from "lodash/find";
import { convertFileSize, download } from "../../utilities/file.utilities";

const angular = require("angular");
require("ng-file-upload");

const UPLOAD_ERROR = "upload_error";
const UPLOAD_EVENT = "FILE_UPLOAD_EVENT";
const UPLOAD_PROGRESS = "FILE_PROGRESS";
const UPLOAD_COMPLETE_EVENT = "FILE_UPLOAD_COMPLETE_EVENT";

/**
 * Services to call API for SequenceFile
 * @param {object} $rootScope angular root scope for all DOM
 * @param {object} $q angular promise object
 * @param {object} upload angular FileUpload object
 * @return {object} of available functions
 * @constructor
 */
function FileService($rootScope, $q, upload) {
  return {
    download: download,
    upload: uploadFiles,
    uploadBulk: uploadBulkFiles
  };

  /**
   * Uploads list of files
   * @param {string} url Url to upload files to.
   * @param {list} files Array of files.
   * @return {object} promise of the upload.
   */
  function uploadFiles(url, files) {
    const defer = $q.defer();
    const promises = [];

    for (const file of files) {
      const currentUpload = upload
        .upload({
          url,
          file
        })
        .then(
          function(response) {
            $rootScope.$broadcast(UPLOAD_COMPLETE_EVENT);
            defer.resolve(response);
          },
          function(data) {
            $rootScope.$broadcast(UPLOAD_ERROR, data.error_message);
            defer.reject("Error uploading file");
          },
          function(evt) {
            // Progress handled here
            $rootScope.$broadcast(UPLOAD_PROGRESS, {
              file: file.name,
              progress: parseInt((100.0 * evt.loaded) / evt.total, 10)
            });
          }
        );
      $rootScope.$broadcast(UPLOAD_EVENT, {
        file: file,
        count: files.length
      });

      promises.push(currentUpload);
    }

    $q.all(promises);

    return defer.promise;
  }

  /**
   * Uploads list of files in one request
   * @param {string} url Url to upload files to.
   * @param {list} files Array of files.
   * @return {object} promise of the upload.
   */
  function uploadBulkFiles(url, files) {
    const defer = $q.defer();

    upload
      .upload({
        url: url,
        file: files
      })
      .then(
        function() {
          defer.resolve();
        },
        function(data) {
          $rootScope.$broadcast(UPLOAD_ERROR, data.error_message);
          defer.reject("Error uploading file");
        }
      );

    return defer.promise;
  }
}

/**
 * Directive to show error message if files fail to upload.
 * @return {object} angular directive for upload errors.
 */
function uploadError() {
  return {
    restrict: "E",
    templateUrl: "/upload-error.html",
    controller: [
      "$scope",
      function($scope) {
        $scope.hasError = false;
        $scope.$on(UPLOAD_ERROR, function(event, reason) {
          $scope.errorMessage = reason;
          $scope.hasError = true;
        });
      }
    ]
  };
}

/**
 * Directive to show the percent complete of a list of files being uploaded.
 * @return {object} angular directive for upload progress.
 */
function fileUploadProgress() {
  return {
    restrict: "E",
    require: "^filesUploading",
    templateUrl: "/files-upload-progress.html",
    controller: [
      "$scope",
      function($scope) {
        $scope.files = [];
        $scope.upload = null;

        $scope.$on("NEW_UPLOAD", function() {
          $scope.closeProgress();
        });

        $scope.$on(UPLOAD_PROGRESS, function(evt, args) {
          // Find the upload
          const upload = find($scope.files, ["filename", args.file]);
          if (upload) {
            upload.progress = args.progress;
          }
        });

        $scope.$on(UPLOAD_EVENT, function(evt, args) {
          var file = {
            progress: 0,
            filename: args.file.name
          };
          $scope.count = args.count;
          $scope.files.push(file);
          $scope.uploading = true;
        });

        $scope.$on(UPLOAD_ERROR, function() {
          $scope.uploading = false;
        });

        $scope.$on(UPLOAD_COMPLETE_EVENT, function() {
          $scope.uploading = false;
        });

        $scope.closeProgress = function() {
          $scope.files = [];
          $scope.uploading = false;
        };
      }
    ]
  };
}

/**
 * Filter for formatting the files size.
 * @return {function} filter for file size
 */
function humanReadableBytes() {
  return convertFileSize;
}

angular
  .module("file.utils", ["ngFileUpload"])
  .factory("FileService", ["$rootScope", "$q", "Upload", FileService])
  .filter("humanReadableBytes", [humanReadableBytes])
  .directive("fileUploadProgress", [fileUploadProgress])
  .directive("uploadError", [uploadError]);
