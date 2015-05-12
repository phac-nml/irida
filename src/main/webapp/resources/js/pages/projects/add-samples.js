(function(angular, $, TL, PAGE) {
  'use strict';
  var URL_BASE = TL.BASE_URL + 'projects/' + PAGE.project.id + '/samples';

  function SampleService($http) {
    var url = URL_BASE;
    return {
      createSample: createSample
    };

    function createSample(sample, successFn, errorFn) {
      return $http.post(url, sample)
        .success(function(response) {
          successFn(response);
        })
        .error(function(response) {
          errorFn(response);
        });
    }
  }

  function FileService(upload) {
    return {
      upload: uploadFiles
    };

    function uploadFiles(files, id) {
      console.log(files);
      if (files && files.length > 0) {
        upload.upload({
          // e.g. projects/4/samples/4/files
          url: URL_BASE + '/' + id + '/files',
          file: files
        });
      }
    }
  }

  function nameValidator() {
    var re = /[^A-Za-z0-9\-_]/;
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.nameValidator = function(value) {
          return !re.test(value);
        };
      }
    };
  }

  function sizeConverter() {
    return function(bytes) {
      var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
      if (bytes === 0) {
        return '0 Byte';
      }
      var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
      return (Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i]);
    };
  }

  function select2() {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, elem) {
        $(elem).select2({
          minimumInputLength: 3,
          ajax: {
            url: TL.BASE_URL + 'projects/ajax/taxonomy/search',
            dataType: 'json',
            data: function(term) {
              return {
                searchTerm: term
              };
            },
            results: function(data) {
              return {
                results: data
              };
            }
          }
        });
      }
    };
  }

  function SampleController(sampleService, fileService, $modal) {
    var vm = this;
    vm.files = [];
    vm.sample = {};
    vm.uploader = {
      inProgress: false
    };
    vm.nameOptions = {
      debounce: 300
    };

    vm.createSample = function createSample() {
      vm.sample.sequencerSampleId = vm.sample.sampleName;
      sampleService.createSample(vm.sample, sampleCreatedSuccess, sampleCreatedError);
    };

    //    vm.addFiles = function addFiles(f) {
    //      f.forEach(function(item) {
    //        vm.files.push(item);
    //      });
    //    };

    vm.uploadSingle = function(files) {
      fileService.upload(files, vm.sample.id);
    };

    vm.showPairedModal = function() {
      $modal.open({
        animation: true,
        templateUrl: '/paired-template.html',
        controllerAs: 'pairCtrl',
        controller: 'PairedUploadController'
      }).result.then(function(files) {
        console.log(files);
      });
    };

    function sampleCreatedSuccess(response) {
      vm.sample = response.sample;
      window.location = URL_BASE + '/' + response.sample.id + '/sequenceFiles';
    }

    function sampleCreatedError(response) {
      var errors = response.errors;
      for (var key in errors) {
        if (errors.hasOwnProperty(key) && key !== 'label' && key !== 'sequencerSampleId') {
          vm.sampleDetailForm[key].$dirty = true;
          vm.sampleDetailForm[key].$setValidity(errors[key], false);
          vm.sampleNameError = errors[key];
        }
      }
    }
  }

  function PairedUploadController($modalInstance) {
    var vm = this;
    vm.files = {};
    vm.selectForward = function selectForward(file) {
      vm.files.forward = file.pop();
    };
    vm.selectReverse = function selectReverse(file) {
      vm.files.reverse = file.pop();
    };
    vm.upload = function upload() {
      var files = [vm.files.forward, vm.files.reverse];
      $modalInstance.close(files);
    };
  }

  angular.module('samples.new', ['ngFileUpload'])
    .factory('SampleService', ['$http', SampleService])
    .factory('FileService', ['Upload', FileService])
    .directive('select2', [select2])
    .directive('nameValidator', [nameValidator])
    .filter('sizeConverter', [sizeConverter])
    .controller('SampleController', ['SampleService', 'FileService', '$modal', SampleController])
    .controller('PairedUploadController', ['$modalInstance', PairedUploadController]);
})(window.angular, window.$, window.TL, window.PAGE);