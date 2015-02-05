(function () {
  "use strict";

  /**
   * Filter for transforming the galaxy generated state value into a readable value.
   * @returns {Function}
   * @constructor
   */
  function StateFilter() {
    return function (state) {
      return STATE_MAP[state];
    }
  }

  /**
   * Service to hold the current state of the analyses filter
   * @returns {{search: string}}
   * @constructor
   */
  function AnalysisFilterService() {
    return {
      search: ""
    }
  }

  /**
   * The filter function for the analyses.
   * @param filter
   * @returns {Function}
   * @constructor
   */
  function AnalysesFilter(filter) {
    return function (analyses) {
      var filtered = [];
      analyses.forEach(function (analysis) {
        if (filter.search.length === 0 || analysis['label'].toLowerCase().indexOf(filter.search.toLowerCase()) > 0) {
          filtered.push(analysis);
        }
      });
      return filtered;
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
    vm.search = filter.search;

    vm.doSearch = function () {
      filter.search = vm.search;
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
    .filter('stateFilter', [StateFilter])
    .filter('analysesFilter', ['analysisFilterService', AnalysesFilter])
    .service('analysisService', ['$http', AnalysisService])
    .service('analysisFilterService', [AnalysisFilterService])
    .controller('analysisController', ['analysisService', AnalysisController])
    .controller('filterController', ['analysisFilterService', FilterController])
  ;
})();