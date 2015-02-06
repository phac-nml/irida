(function () {
  "use strict";

  /**
   * Service to hold the current state of the analyses filter
   * @returns {{search: string}}
   * @constructor
   */
  function AnalysisFilterService() {
    return {
      label: "",
      analysisState : ""
    }
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
        if(item === null) return;
        if(key === 'analysisState' && value.length > 0 && item !== value) {
          result = false;
        }
        else if (item.toLowerCase().indexOf(value.toLowerCase()) < 0) {
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
    vm.search = "";

    vm.clear = function () {
      _.forOwn(filter, function (value, key) {
        filter[key] = "";
      });
      vm.search = "";
    };

    vm.doSearch = function () {
      filter.label = vm.search;
    };

    vm.doState = function () {
      filter.analysisState = vm.state;
    };
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