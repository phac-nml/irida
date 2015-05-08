(function(angular, $, TL) {
  'use strict';

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
              return {searchTerm: term};
            },
            results: function(data) {
              return {results: data};
            }
          }
        });
      }
    };
  }

  function SampleController() {
    var vm = this;
  }

  angular.module('samples.new', ['mgo-angular-wizard'])
    .directive('select2', [select2])
    .controller('SampleController', ['$scope', SampleController]);
})(window.angular, window.$, window.TL);
