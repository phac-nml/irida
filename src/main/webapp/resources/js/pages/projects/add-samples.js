(function(angular, $, TL, PAGE) {
  'use strict';

  function SampleService($http, $log) {
    var url = TL.BASE_URL + 'projects/' + PAGE.project.id + '/samples';
    return {
      createSample: createSample
    };

    function createSample(sample) {
      $log.info(sample);
      return $http.post(url, {
          sample: sample
        })
        .success(function(response) {
          console.log(response);
        })
        .error(function(response) {
          console.log(response);
        });
    }
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
      sampleService.createSample(vm.sample);
    };
  }

  angular.module('samples.new', ['mgo-angular-wizard'])
    .factory('SampleService', ['$http', '$log', SampleService])
    .directive('select2', [select2])
    .controller('SampleController', ['SampleService', 'WizardHandler', SampleController]);
})(window.angular, window.$, window.TL, window.PAGE);