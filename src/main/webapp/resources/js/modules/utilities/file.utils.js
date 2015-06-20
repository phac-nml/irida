/**
 * Use for uploading and downloading files through the UI.
 * Ensure to include the templates:
 *    <div th:replace="templates/_file.utils :: upload-progress"></div>
 *    <div th:replace="templates/_file.utils :: upload-error"></div>
 */
(function (angular){
  'use strict';
  var UPLOAD_ERROR = 'upload_error',
    UPLOAD_EVENT = 'FILE_UPLOAD_EVENT';

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
    function download(url) {
      var iframe = document.createElement('iframe');
      iframe.src = url;
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
    }

    /**
     * Uploads list of files
     * @param url Url to upload files to.
     * @param files Array of files.
     */
    function uploadFiles(url, files) {
      if (files && files.length) {
        var currentUpload = upload.upload({
          url: url,
          file: files
        })
          .error(function () {
            $rootScope.$broadcast(UPLOAD_ERROR);
          });
        $rootScope.$broadcast(UPLOAD_EVENT, {
          files: files,
          progress: currentUpload.progress
        });
      }
      return currentUpload;
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
      controller: ['$scope', function($scope) {
        $scope.hasError = false;
        $scope.$on(UPLOAD_ERROR, function() {
          $scope.hasError = true;
        });
      }]
    };
  }

  /**
   * Directive to show the percent complete of a list of files being uploaded.
   * @returns {{restrict: string, require: string, templateUrl: string, controller: *[]}}
   */
  function fileUploadProgress() {
    return {
      restrict: 'E',
      require: '^filesUploading',
      templateUrl: '/files-upload-progress.html',
      controller: ['$scope', function($scope) {
        $scope.upload = null;
        $scope.$on(UPLOAD_EVENT, function (evt, args) {
          $scope.uploading = true;
          $scope.count = args.files.length;
          args.progress(function (evt) {
            $scope.progress = parseInt(100.0 * evt.loaded / evt.total);
          });
        });

        $scope.$on(UPLOAD_ERROR, function() {
          $scope.uploading = false;
        });
      }]
    };
  }

  /**
   * Filter for formatting the files size.
   * @returns {Function}
   */
  function humanReadableBytes() {
    return function(bytes) {
      if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) {return bytes;}
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

  angular.module('file.utils', ['ngFileUpload'])
    .factory('FileService', ['$rootScope', 'Upload', FileService])
    .filter('humanReadableBytes', [humanReadableBytes])
    .directive('fileUploadProgress', [fileUploadProgress])
    .directive('uploadError', [uploadError])
  ;
})(window.angular);