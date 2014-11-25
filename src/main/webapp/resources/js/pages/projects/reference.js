(function (angular, $, _) {

  function FileService($rootScope, R) {
    "use strict";
    var svc = this,
        id = $rootScope.projectId,
        base = R.all('projects/' + id + '/ajax/reference');
    svc.files = [];

    svc.getFiles = function getFiles() {
      return base.customGET("all").then(function (data) {
        angular.copy(data.files, svc.files);
      });
    }
  }

  function FilesCtrl(FileService) {
    "use strict";
    var vm = this;
    vm.files = FileService.files;

    vm.download = function(id) {
      console.log(id)
    };

    vm.deleteFile = function(id) {
      console.log(id)
    };

    FileService.getFiles();
  }

  angular.module('References', [])
    .service('FileService', ['$rootScope', 'Restangular', FileService])
    .controller('FilesCtrl', ['FileService', FilesCtrl])
  ;
})(angular, jQuery, _);