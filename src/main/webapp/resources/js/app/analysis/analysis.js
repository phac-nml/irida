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
      hiddenIFrame.src = PAGE.URL.download + id;
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
     * 'PAGE.URLS.status' is on the `_base.html` page for the analysis.
     * @returns {*}
     * @private
     */
    function _getState() {
      return $http.get(PAGE.URLS.status).then(function (data) {
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

  function PreviewController() {
    var vm = this;
    vm.newick = PAGE.NEWICK;
  }

  angular.module('irida.analysis', ['ui.router', 'subnav', 'phylocanvas'])
    .config(['$stateProvider', function ($stateProvider) {
      var isLinux = navigator.platform.indexOf("Linux") !== -1;
      console.log(navigator.platform);
      
      $stateProvider
        .state("preview", {
          url        : "/preview",
          templateUrl: isLinux ? "preview-linux.html" : "preview.html"
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
    .service('AnalysisService', ['$http', '$interval', AnalysisService])
    .controller('FileDownloadController', [FileDownloadController])
    .controller('StateController', ['AnalysisService', StateController])
    .controller('PreviewController', [PreviewController])
  ;
})(window.angular);