(function (angular, $, _, page, project) {

  function ProjectFileService($rootScope, $http) {
    "use strict";
    var svc = this;
      svc.files = [];

    svc.getFiles = function getFiles() {
      return $http.get(page.urls.get)
        .success(function(data){
          angular.copy(data.files, svc.files);
        });
    };

    $rootScope.$on('FILE_DELETED', function (e, args) {
      angular.copy(_.filter(svc.files, function (f) {
        return args.id !== f.id;
      }), svc.files);
    });

    $rootScope.$on('NEW_FILE', function (e, args) {
      args.file.newFile = true;
      svc.files.push(args.file);
    });
  }

  function ReferenceFileService($rootScope, $uibModal, $http, notifications) {
    "use strict";
    var svc = this;

    svc.download = function (id) {
      var iframe = document.createElement("iframe");
      iframe.src = page.urls.download + id;
      iframe.style.display = "none";
      document.body.appendChild(iframe);
    };

    svc.deleteFile = function (file) {
      var modalInstance = $uibModal.open({
        templateUrl: '/delete-modal.html',
        controller : 'DeleteCtrl as dCtrl',
        resolve    : {
          file: function () {
            return file;
          }
        }
      });

      modalInstance.result.then(function () {
        $http.post(page.urls.remove, {
          fileId: file.id,
          projectId: project.id
        }).success(function(data) {
          notifications.show({msg: data.msg, type: data.result});
          $rootScope.$broadcast('FILE_DELETED', {id: file.id});
        });
      });
    };
  }

  function DeleteCtrl($uibModalInstance, file) {
    "use strict";
    var vm = this;
    vm.file = file;

    vm.delete = function () {
      $uibModalInstance.close();
    };

    vm.close = function () {
      $uibModalInstance.dismiss();
    };
  }

  function FilesCtrl(ProjectFileService, projectFilesService) {
    "use strict";
    var vm = this;

    vm.getRowClass = function (file) {
      var rowClass = "";
      if (!angular.isNumber(file.size)) {
        rowClass = 'danger';
      }
      else if (file.newFile) {
        rowClass = 'success';
      }
      return rowClass;
    };

    vm.download = function (id) {
      projectFilesService.download(id);
    };

    vm.deleteFile = function (file) {
      projectFilesService.deleteFile(file);
    };

    ProjectFileService.getFiles().then(function () {
      vm.files = ProjectFileService.files;
    });
  }

  function FileUploadCtrl($timeout, fileService) {
    var vm = this;

    vm.onFileSelect = function ($files) {
      if ($files && $files.length) {
        fileService.upload(page.urls.upload, $files).then(function () {
          $timeout(function () {
            window.location.href = window.location.href;
          }, 500);
        });
      }
    };
  }

  angular.module('References', ['file.utils'])
    .service('ProjectFileService', ['$rootScope', '$http', ProjectFileService])
    .service('ReferenceFileService', ['$rootScope', '$uibModal', '$http', 'notifications', ReferenceFileService])
    .controller('FilesCtrl', ['ProjectFileService', 'ReferenceFileService', FilesCtrl])
    .controller('DeleteCtrl', ['$uibModalInstance', 'file', DeleteCtrl])
    .controller('FileUploadCtrl', ['$timeout', 'FileService', FileUploadCtrl])
  ;
})(window.angular, window.jQuery, window._, window.PAGE, window.project);
