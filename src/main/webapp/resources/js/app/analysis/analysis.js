/* global ANALYSIS_PAGE */

(function (angular, PhyloCanvas) {

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


  angular.module('irida.analysis', [])
    .service('AnalysisService', ['$http', '$interval', AnalysisService])
    .directive('phylocanvas', function phylocanvas() {
      return {
        restrict  : 'E',
        transclude: true,
        replace   : true,
        template  : '<div><div ng-transclude></div>{{shape}}</div>',
        scope     : {
          shape: "@"
        },
        controller: ['$scope', function($scope) {
          $scope.shape = shape;          
        }]
      };
    })
    .directive('phylocanvasControls', function () {
      return {
        require   : '^phylocanvas',
        replace   : true,
        transclude: true,
        template  : '<div><div class="btn-group" role="group"><button ng-repeat="control in controls" ng-click="select(control)" class="btn btn-default btn-sm" ng-class="{active: control.selected}">{{control.text}}</button></div><div ng-transclude></div></div>',
        restrict  : "E",
        controller: ['$scope', function ($scope) {
          $scope.controls = [];
          $scope.shape = 'circular';
          
          $scope.select = function(control) {
            angular.forEach($scope.controls, function (eachControl) {
              eachControl.selected = angular.equals(control, eachControl);
            });
            
          };
          
          this.addControl = function (control) {
            if($scope.controls.length === 0) {
              control.selected = true;
            }
            $scope.controls.push(control);
          };
        }]
      };
    })
    .directive('phylocanvasControl', function () {
      return {
        require : '^phylocanvasControls',
        restrict: 'E',
        replace: true,
        template: '',
        scope   : {
          shape: "@",
          text : "@"
        },
        link    : function (scope, element, attrs, phylocanvasControlsCtrl) {
          phylocanvasControlsCtrl.addControl(scope);
        }
      };
    })
    .directive('phylocanvasBody', function () {
      return {
        require: "^phylocanvas",
        scope  : {
          newick: "@",
          id    : '@'
        },
        link   : function (scope, elem) {
          angular
            .element(elem)
            .css({
              'height': '500px',
              'width' : '100%'
            });
          var phylo = new PhyloCanvas.Tree(scope.id, {});
          phylo.load(scope.newick);

          scope.$watch(function () {
            return scope.$parent.shape;
          }, function (n, o) {
            console.log(n, o);
            if (o !== n) {
              phylo.setTreeType(n);
            }
          });
        }
      };
    })
    .controller('FileDownloadController', [FileDownloadController])
    .controller('StateController', ['AnalysisService', StateController])
  ;
})(window.angular, window.PhyloCanvas);