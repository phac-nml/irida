(function () {
  "use strict";

  function StateFilter() {
    return function (state) {
      console.log(STATE_MAP[state]);
      return STATE_MAP[state];
    }
  }

  function AnalysisFilterService() {
    return {
      search: ""
    }
  }

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

  function AnalysisService($http) {
    function _loadData() {
      return $http.get(TL.BASE_URL + "analysis/ajax/list");
    }

    return {
      load: _loadData
    }
  }

  function FilterController(filter) {
    var vm = this;
    vm.search = filter.search;

    vm.doSearch = function () {
      filter.search = vm.search;
    };
  }

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