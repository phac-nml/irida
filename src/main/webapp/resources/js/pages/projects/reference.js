(function (angular, $, _) {

  function ProjectFileService($rootScope, R) {
    "use strict";
    var svc = this,
        base = R.all('projects/' + project.id + '/ajax/reference');
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

    $rootScope.$on('NEW_FILE', function (e, args) {
      args.file.newFile = true;
      svc.files.push(args.file);
    });
  }

  function ReferenceFileService($rootScope, $modal, R, notifications) {
    "use strict";
    var svc = this,
        api = R.all('referenceFiles');

    svc.download = function (id) {
      var iframe = document.createElement("iframe");
      iframe.src = TL.BASE_URL + "referenceFiles/download/" + id;
      iframe.style.display = "none";
      document.body.appendChild(iframe);
    };

    svc.deleteFile = function (file) {
      var modalInstance = $modal.open({
        templateUrl: TL.BASE_URL + 'projects/templates/referenceFiles/delete',
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
          projectId: project.id
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

  function FileUploadCtrl(fileService) {
    var vm = this,
      url = TL.BASE_URL + 'referenceFiles/project/' + project.id + '/new';

    vm.onFileSelect = function ($files) {
      if ($files && $files.length) {
        fileService.upload(url, $files).success(function () {
          window.location.href = window.location.href;
        });
      }
    };
  }

  angular.module('References', ['file.utils'])
    .service('ProjectFileService', ['$rootScope', 'Restangular', ProjectFileService])
    .service('ReferenceFileService', ['$rootScope', '$modal', 'Restangular', 'notifications', ReferenceFileService])
    .controller('FilesCtrl', ['ProjectFileService', 'ReferenceFileService', FilesCtrl])
    .controller('DeleteCtrl', ['$modalInstance', 'file', DeleteCtrl])
    .controller('FileUploadCtrl', ['FileService', FileUploadCtrl])
  ;
})(window.angular, window.jQuery, window._, window.TL, window.project);