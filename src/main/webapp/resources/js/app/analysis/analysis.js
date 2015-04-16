/* global ANALYSIS_PAGE */

(function (angular) {

  /**
   * Controller to download the analysis.
   * @constructor
   */
  function FileDownloadController() {
    var vm = this;

    vm.getZipFile = function (id) {
      var iFrameId = 'hiddenDownloader';
      var hiddenIFrame = document.getElementById(iFrameId);
      if (hiddenIFrame === null) {
        hiddenIFrame = document.createElement('iframe');
        hiddenIFrame.id = iFrameId;
        hiddenIFrame.style.display = 'none';
        document.body.appendChild(hiddenIFrame);
      }
      hiddenIFrame.src = ANALYSIS_PAGE.URL.download + id;
    };
  }

  /**
   * Service for Analysis
   * @param $http
   * @param $interval
   * @returns {AnalysisService}
   * @constructor
   */
  function AnalysisService($http, $interval) {
    var svc = this;

    /**
     * Call the server to get the status for the current analysis.
     * 'ANALYSIS_PAGE.URLS.status' is on the `_base.html` page for the analysis.
     * @returns {*}
     * @private
     */
    function _getState() {
      return $http.get(ANALYSIS_PAGE.URLS.status).then(function (data) {
        return data.data;
      });
    }

    /**
     * Set up polling if the state is not 'completed' or 'error'
     * @param fn Callback function with how to handle the results.
     * @private
     */
    function _poll(fn) {
      var interval = $interval(function () {
        _getState().then(function (data) {
          // 100 set for complete and error states
          if (parseInt(data.percentComplete) === 100) {
            $interval.cancel(interval);
            window.location.reload();
          }
          fn(data);
        });
      }, 5000);
    }

    /**
     * Exported function to call the server for information about the current analysis.
     * @param fn Callback function with how to handle the results.
     */
    svc.getAnalysisState = function (fn) {
      _getState().then(function (data) {
        fn(data);
        if (parseInt(data.percentComplete) !== 100) {
          _poll(fn);
        }
      });
    };

    return svc;
  }

  /**
   * Controls the state for the current analysis.
   * @param AnalysisService
   * @constructor
   */
  function StateController(AnalysisService) {
    var vm = this;
    vm.percentage = 0;

    /**
     * Initializes the sate of the analysis.
     */
    function initialize() {
      return AnalysisService.getAnalysisState(function (data) {
        vm.state = data.state;
        vm.stateLang = data.stateLang;
        vm.percentage = parseFloat(data.percentComplete);
        vm.stateClass = _createClass(vm.state);
      });
    }

    /**
     * Dynamically creates the border color for the top of the sidebar depending on the state of the analysis.
     * @param state
     * @returns {string}
     * @private
     */
    function _createClass(state) {
      return 'analysis__alert--' + state.toLowerCase();
    }

    initialize();
  }


  angular.module('irida.analysis', ['ui.router', 'phylocanvas'])
    .config(['$stateProvider', function ($stateProvider) {

      $stateProvider
        .state("preview", {
          url        : "/preview",
          templateUrl: "preview.html"
        })
        .state("inputs", {
          url        : "/inputs",
          templateUrl: "inputs.html"
        })
        .state("provenance", {
          url        : "/provenance",
          templateUrl: "provenance.html"
        })
      ;
    }])
    .directive('subNav', function () {
      return {
        restrict  : 'E',
        replace   : true,
        transclude: true,
        priority  : -1,
        template  : '<div style="padding-bottom: 10px;"><ul class="nav nav-pills"><li ng-click="select(link)" ng-class="{active: link.selected}" ng-repeat="link in links"><a id="{{link.state}}" ui-sref="{{link.state}}">{{link.text}}</a></li></ul><div ng-transclude></div></div>',
        controller: ['$scope', '$location', function ($scope, $location) {
          $scope.links = [];

          $scope.select = function (link) {
            angular.forEach($scope.links, function (eachLink) {
              eachLink.selected = angular.equals(link, eachLink);
            });
          };

          var path = $location.path();
          this.addLink = function (link) {
            if (path === "" && $scope.links.length === 0) {
              link.selected = true;
              $location.path("/" + link.state);
            }
            else if (path.indexOf(link.state) > -1) {
              link.selected = true;
            }
            $scope.links.push(link);
          };
        }]
      };
    })
    .directive('subNavItem', function () {
      return {
        require : '^subNav',
        restrict: 'E',
        replace : true,
        template: '',
        scope   : {
          state: "@",
          text : "@"
        },
        link    : function (scope, element, attrs, SubNavCtrl) {
          SubNavCtrl.addLink(scope);
        }
      };
    })
    .service('AnalysisService', ['$http', '$interval', AnalysisService])
    .controller('FileDownloadController', [FileDownloadController])
    .controller('StateController', ['AnalysisService', StateController])
  ;
})(window.angular);