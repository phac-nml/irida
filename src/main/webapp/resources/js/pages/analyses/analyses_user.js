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
  function AnalysesFilter(filter, $rootScope) {
    function _filterAnalysis(analysis) {
      var result = true;
      _.forOwn(filter, function (value, key) {
        var item = analysis[key];
        if(item === null) return;
        if(key === 'analysisState') {
          if(value.length > 0 && value !== 'ALL' && item !== value) result = false;
        }
        else if (angular.isString(value) && item.toLowerCase().indexOf(value.toLowerCase()) === -1) {
          result = false;
        }
      });
      return result;
    }

    return function (analyses) {
      var filtered = _.filter(analyses, function (analysis) {
        return _filterAnalysis(analysis)
      });

      if(filtered.length === 0) {
        $rootScope.$broadcast('NO_ANALYSIS');
      }
      else {
        $rootScope.$broadcast('ANALYSIS_AVAILABLE');
      }
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
    vm.showAnalyses = true;

    function _setDefaults() {
      vm.search = "";
      vm.state = "ALL";
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

    _setDefaults();
  }

  /**
   * Controller for the actual analyses list.
   * @param svc
   * @constructor
   */
  function AnalysisController(svc, $scope) {
    var vm = this;
    vm.analyses = [];

    svc.load()
      .success(function (data) {
        vm.analyses = data.analyses;
      });

    $scope.$on('NO_ANALYSIS', function () {
      vm.showAnalyses = false;
    });

    $scope.$on('ANALYSIS_AVAILABLE', function () {
      vm.showAnalyses = true;
    })
  }

  angular.module('irida.analysis.user', [])
    .filter('analysesFilter', ['analysisFilterService', '$rootScope', AnalysesFilter])
    .service('analysisService', ['$http', AnalysisService])
    .service('analysisFilterService', [AnalysisFilterService])
    .controller('analysisController', ['analysisService', '$scope', AnalysisController])
    .controller('filterController', ['analysisFilterService', FilterController])
  ;
})();