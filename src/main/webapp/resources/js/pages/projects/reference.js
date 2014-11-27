(function (angular, $, _) {

  function ProjectFileService($rootScope, R) {
    "use strict";
    var svc = this,
        id = $rootScope.projectId,
        base = R.all('projects/' + id + '/ajax/reference');
    svc.files = [];

    svc.getFiles = function getFiles() {
      return base.customGET("all").then(function (data) {
        angular.copy(data.files, svc.files);
      });
    };

    $rootScope.$on('FILE_DELETED', function (e, args) {
      angular.copy(_.filter(svc.files, function (f) {
        return args.id !== f.id;
      }), svc.files);
    });

    $rootScope.$on('NEW_FILE', function(e, args) {
      svc.files.push(args.file);
    });
  }

  function FileService($rootScope, $modal, BASE_URL, R, notifications) {
    "use strict";
    var svc = this,
        projectId = $rootScope.projectId,
        api = R.all('referenceFiles');

    svc.download = function (id) {
      var iframe = document.createElement("iframe");
      iframe.src = BASE_URL + "referenceFiles/download/" + id;
      iframe.style.display = "none";
      document.body.appendChild(iframe);
    };

    svc.deleteFile = function (file) {
      var modalInstance = $modal.open({
        templateUrl: BASE_URL + 'projects/templates/referenceFiles/delete',
        controller : 'DeleteCtrl as dCtrl',
        resolve    : {
          file: function () {
            return file;
          }
        }
      });

      modalInstance.result.then(function () {
        api.customPOST({
          fileId   : file.id,
          projectId: projectId
        }, "delete").then(function (data) {
          notifications.show({msg: data.msg, type: data.result});
          $rootScope.$broadcast("FILE_DELETED", {id: file.id});
        });
      });
    }
  }

  function FileUploadService($rootScope, $upload, BASE_URL) {
    "use strict";
    var svc = this,
        projectId = $rootScope.projectId;

    svc.uploadFiles = function($files) {
      // TODO: add a check to see if this file has already been upload and give a warning if it has.
      _.each($files, function(file) {
        $upload.upload({
          url: BASE_URL + "referenceFiles/project/" + projectId + "/new",
          file: file
        }).success(function(data) {
          $rootScope.$broadcast('NEW_FILE', {file: data.result})
        }).error(function(data) {
          console.log("error");
          console.log(data);
        });
      });
    };
  }

  function bytes() {
    return function(bytes, precision) {
      if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return bytes;
      if (typeof precision === 'undefined') precision = 1;
      var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PB'],
          number = Math.floor(Math.log(bytes) / Math.log(1024));
      return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];
    };
  }

  function DeleteCtrl($modalInstance, file) {
    "use strict";
    var vm = this;
    vm.file = file;

    vm.delete = function () {
      $modalInstance.close();
    };

    vm.close = function () {
      $modalInstance.dismiss();
    };
  }

  function FilesCtrl(ProjectFileService, FileService) {
    "use strict";
    var vm = this;

    vm.download = function (id) {
      FileService.download(id);
    };

    vm.deleteFile = function (file) {
      FileService.deleteFile(file);
    };

    ProjectFileService.getFiles().then(function () {
      vm.files = ProjectFileService.files;
    });
  }

  function FileUploadCtrl(FileUploadService) {
    "use strict";
    var vm = this;

    vm.onFileSelect = function($files) {
      FileUploadService.uploadFiles($files);
    };
  }

  angular.module('References', ['angularFileUpload'])
    .service('ProjectFileService', ['$rootScope', 'Restangular', ProjectFileService])
    .service('FileService', ['$rootScope', '$modal', 'BASE_URL', 'Restangular', 'notifications', FileService])
    .service('FileUploadService', ['$rootScope', '$upload', 'BASE_URL', FileUploadService])
    .filter('bytes', [bytes])
    .controller('FilesCtrl', ['ProjectFileService', 'FileService', FilesCtrl])
    .controller('DeleteCtrl', ['$modalInstance', 'file', DeleteCtrl])
    .controller('FileUploadCtrl', ['FileUploadService', FileUploadCtrl])
  ;
})(angular, jQuery, _);