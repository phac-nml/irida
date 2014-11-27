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
      console.log("deleteing")
      angular.copy(_.filter(svc.files, function (f) {
        return args.id !== f.id;
      }), svc.files);
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

  angular.module('References', [])
    .service('ProjectFileService', ['$rootScope', 'Restangular', ProjectFileService])
    .service('FileService', ['$rootScope', '$modal', 'BASE_URL', 'Restangular', 'notifications', FileService])
    .controller('FilesCtrl', ['ProjectFileService', 'FileService', FilesCtrl])
    .controller('DeleteCtrl', ['$modalInstance', 'file', DeleteCtrl])
  ;
})(angular, jQuery, _);