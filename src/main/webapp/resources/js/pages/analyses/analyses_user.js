(function () {
  "use strict";

  /**
   * Service to hold the current state of the analyses filter
   * @returns {{search: string}}
   * @constructor
   */
  function AnalysisFilterService() {
    return {}
  }

  /**
   * The filter function for the analyses.
   * @param filter
   * @returns {Function}
   * @constructor
   */
  function AnalysesFilter(filter) {
    function _filterAnalysis(analysis) {
      var result = true;
      _.forOwn(filter, function (value, key) {
        var item = analysis[key];
        if (key === 'minDate' || key == 'maxDate') {
          item = analysis['createdDate'];
        }

        if (item === null) return;
        if (key === 'analysisState') {
          if (value.length > 0 && value !== 'ALL' && item !== value) result = false;
        }
        else if (key === 'minDate' && item < value) {
          result = false;
        }
        else if (key === 'maxDate' && item > value) {
          result = false;
        }
        else if (angular.isString(value) && item.toLowerCase().indexOf(value.toLowerCase()) !== -1) {
          result = false;
        }
      });
      return result;
    }

    return function (analyses) {
      return _.filter(analyses, function (analysis) {
        return _filterAnalysis(analysis)
      });
    }
  }

  /**
   * Service for querying the server for analyses
   * @param $http
   * @returns {{load: _loadData}}
   * @constructor
   */
  function AnalysisService($http) {
    function _loadData() {
      return $http.get(TL.BASE_URL + "analysis/ajax/list");
    }

    return {
      load: _loadData
    }
  }

  /**
   * Handles events in the view for the filter.
   * @param filter
   * @constructor
   */
  function FilterController(filter) {
    var vm = this;
    vm.opened = {};

    function _setDefaults() {
      vm.search = "";
      vm.state = "ALL";
      vm.min = "";
      vm.max = new Date();
    }

    vm.clear = function () {
      _.forOwn(filter, function (value, key) {
        delete filter[key];
      });
      _setDefaults();
    };

    vm.doSearch = function () {
      filter.label = vm.search;
    };

    vm.doState = function () {
      filter.analysisState = vm.state;
    };

    vm.open = function (e, value) {
      e.preventDefault();
      e.stopPropagation();
      vm.opened[value] = true;
    };

    vm.doDateFilter = function (key) {
      var value = vm[key];
      if (value === null) {
        delete filter[key];
        return;
      }
      var date = new Date(value);
      if (key === 'maxDate') {
        date.setDate(date.getDate() + 1);
      }
      filter[key] = date.getTime();
    }

    _setDefaults();
  }

  /**
   * Controller for the actual analyses list.
   * @param svc
   * @constructor
   */
  function AnalysisController(svc) {
    var vm = this;
    vm.analyses = [];

    svc.load()
      .success(function (data) {
        vm.analyses = data.analyses;
      });
  }

  angular.module('irida.analysis.user', [])
    .filter('analysesFilter', ['analysisFilterService', AnalysesFilter])
    .service('analysisService', ['$http', AnalysisService])
    .service('analysisFilterService', [AnalysisFilterService])
    .controller('analysisController', ['analysisService', AnalysisController])
    .controller('filterController', ['analysisFilterService', FilterController])
  ;
})();