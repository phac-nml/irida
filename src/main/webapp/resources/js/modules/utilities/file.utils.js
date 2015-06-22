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
  function FileService($rootScope, $q, $log, upload) {
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
      var defer = $q.defer();
      var promises = [];

      files.forEach(function (file) {
        var currentUpload = upload.upload({
          url: url,
          file: file
        }).success(function () {
          defer.resolve();
        });
        $rootScope.$broadcast(UPLOAD_EVENT, {
          file: file,
          progress: currentUpload.progress,
          count: files.length
        });

        promises.push(currentUpload);
      });

      $q.all(promises);

      return defer.promise;
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
        $scope.files = [];
        $scope.upload = null;

        $scope.$on('NEW_UPLOAD', function (evt, args) {
          console.log('new Upload');
          $scope.closeProgress();
        });

        $scope.$on(UPLOAD_EVENT, function (evt, args) {
          var file = {};
          $scope.count = args.count;
          args.progress(function (evt) {
            file.progress = parseInt(100.0 * evt.loaded / evt.total);
            file.filename = evt.config.file.name;
          });
          $scope.files.push(file);
          $scope.uploading = true;
        });

        $scope.closeProgress = function () {
          $scope.files = [];
          $scope.uploading = false;
        };
      }]
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

  angular.module('file.utils', ['ngFileUpload'])
    .factory('FileService', ['$rootScope', '$q', '$log', 'Upload', FileService])
    .filter('humanReadableBytes', [humanReadableBytes])
    .directive('fileUploadProgress', [fileUploadProgress])
    .directive('uploadError', [uploadError])
  ;
})(window.angular);