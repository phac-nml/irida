(function(angular, $, TL, PAGE) {
  'use strict';

  function SampleService($http) {
    var url = TL.BASE_URL + 'projects/' + PAGE.project.id + '/samples';
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

  function select2() {
    return {
      restrict: 'A',
      require: 'ngModel',
      link: function(scope, elem) {
        $(elem).select2({
          minimumInputLength: 2,
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

  function SampleController(sampleService, wizardHandler) {
    var vm = this;
    vm.sample = {};
    vm.nameOptions = {
      debounce: 300
    };

    vm.createSample = function createSample() {
      vm.sample.sequencerSampleId = vm.sample.sampleName;
      console.log(vm.sample);
      sampleService.createSample(vm.sample, sampleCreatedSuccess, sampleCreatedError);
    };

    function sampleCreatedSuccess(response) {
      var sample = response.sample;
      wizardHandler.wizard().next();
    }

    function sampleCreatedError(response) {
      console.log(response);
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

  angular.module('samples.new', ['mgo-angular-wizard', 'ng-file-upload'])
    .factory('SampleService', ['$http', SampleService])
    .directive('select2', [select2])
    .directive('nameValidator', [nameValidator])
    .controller('SampleController', ['SampleService', 'WizardHandler', SampleController]);
})(window.angular, window.$, window.TL, window.PAGE);