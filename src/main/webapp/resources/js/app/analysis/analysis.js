/* global angular, ANALYSIS_PAGE */

(function () {

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
      hiddenIFrame.src = ANALYSIS_PAGE.URLS.download + id;
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
    .controller('FileDownloadController', [FileDownloadController])
    .controller('StateController', ['AnalysisService', StateController])
  ;
})();