(function (angular, page, notifications) {
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
      hiddenIFrame.src = page.URLS.download + id + '?dandelionAssetFilterState=false';
    };
  }

  /**
   * Service for Analysis
   * @param $http
   * @param $interval
   * @returns {AnalysisService}
   * @constructor
   */
  function AnalysisService($http) {
    var svc = this;

    /**
     * Call the server to get the status for the current analysis.
     * 'page.URLS.status' is on the `_base.html` page for the analysis.
     * @returns {*}
     * @private
     */
    function _getState() {
      return $http.get(page.URLS.status).then(function (data) {
        return data.data;
      });
    }

    
    /**
     * Get the results from a SISTR analysis to preview in the browser.
     */
    svc.getSistrResults = function() {
      return $http.get(page.URLS.sistr).then(function(result) {
        return result.data;
      });
    }

    /**
     * Exported function to call the server for information about the current analysis.
     * @param fn Callback function with how to handle the results.
     */
    svc.getAnalysisState = function (fn) {
      _getState().then(function (data) {
        fn(data);
      });
    };

    /**
     * Call the server to update the shared status of the current analysis.
     */
    svc.updateProjectShare = function(project, shared) {
      var data = {project: project, shared: shared};
      return $http.post(page.URLS.share, data).then(function(response) {
        return response.data;
      });
    };

    /**
     * Call the server to get the shared status of project
     */
    svc.getSharedProjects = function() {
      return $http.get(page.URLS.share).then(function(response) {
        return response.data;
      });
    };

    return svc;
  }

  function ProjectShareController(AnalysisService, notifications) {
    var vm = this;

    vm.projects = {};

    function initialize() {
      AnalysisService.getSharedProjects().then(function(response){
        vm.projects = response;
        console.log(vm.projects);
      });
    }

    vm.updateShared = function(project) {
      AnalysisService.updateProjectShare(project.project.identifier, project.shared).then(function(response) {
        notifications.show({msg: response.message});
      });
    };

    initialize();
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
        vm.percentage = Math.round(parseFloat(data.percentComplete));
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
    vm.newick = page.NEWICK;
  }

  function SistrController(analysisService) {
    var vm = this;

    analysisService.getSistrResults().then(function(result) {
      vm.order = ["serovar_prediction",
                  "Serogroup_prediction",
                  "H1_prediction",
                  "H2_prediction",
                  "cgMLST_serovar_prediction",
                  "cgMLST_serovar_count_predictions",
                  "cgMLST_cluster_level",
                  "wgMLST_330"];
      var preds = result.serovar_prediction;
      var serotype_predictions_order = ['Subspecies', 'Serovar (overall)', 'Serovar (antigen)', 'Serovar (cgMLST)', 'Serogroup', 'H1', 'H2'];
      var serotype_predictions = {};
      serotype_predictions['Subspecies'] = preds['subspecies'];
      serotype_predictions['Serovar (overall)'] = preds['serovar_prediction'];
      serotype_predictions['Serovar (antigen)'] = preds['serovar_antigen_prediction'];
      serotype_predictions['Serovar (cgMLST)'] = preds['cgMLST_serovar_prediction'];
      serotype_predictions['Serogroup'] = preds['Serogroup_prediction'];
      serotype_predictions['H1'] = preds['H1_prediction'];
      serotype_predictions['H2'] = preds['H2_prediction'];

      var cgMLST_predictions = {};
      var cgMLST_predictions_order = ['Serovar count predictions', 'Cluster level', 'Allele stats'];
      cgMLST_predictions['Serovar count predictions'] = preds['cgMLST_serovar_count_predictions'];
      cgMLST_predictions['Cluster level'] = preds['cgMLST_cluster_level'];

      var cgMLST = result.in_silico_typing.marker_results.wgMLST_330;
      var cgMLST_complete_alleles = 0;
      var cgMLST_missing_alleles = 0;
      var cgMLST_partial_alleles = 0;
      var cgMLST_total_alleles = 0;
      for (var i in cgMLST) {
        if (cgMLST[i].is_missing) {
          cgMLST_missing_alleles++;
        } else if (cgMLST[i].is_contig_truncated) {
          cgMLST_partial_alleles++;
        } else {
          cgMLST_complete_alleles++;
        }

        cgMLST_total_alleles++;
      }

      cgMLST_predictions['Allele stats'] = 'complete ('+cgMLST_complete_alleles+'/'+cgMLST_total_alleles+'), partial ('+cgMLST_partial_alleles+'/'+cgMLST_total_alleles+'), missing ('+cgMLST_missing_alleles+'/'+cgMLST_total_alleles+')';

      vm.serotype_predictions = serotype_predictions;
      vm.serotype_predictions_order = serotype_predictions_order;
      vm.cgMLST_predictions_order = cgMLST_predictions_order;
      vm.cgMLST_predictions = cgMLST_predictions;
    });
  }

  angular.module('irida.analysis', ['ui.router', 'subnav', 'phylocanvas'])
    .config(['$stateProvider', function ($stateProvider) {
      $stateProvider
        .state("preview", {
          url        : "/preview",
          templateUrl: "preview.html"
        })
        .state("sistr", {
          url: "/sistr",
          templateUrl: "sistr.html",
          controllerAs: "sistrCtrl",
          controller: ["AnalysisService", SistrController]
        })
        .state("inputs", {
          url        : "/inputs",
          templateUrl: "inputs.html"
        })
        .state("provenance", {
          url        : "/provenance",
          templateUrl: "provenance.html"
        })
        .state("share", {
          url        : "/share",
          templateUrl: "share.html"
        })
      ;
    }])
    .service('AnalysisService', ['$http', AnalysisService])
    .controller('FileDownloadController', [FileDownloadController])
    .controller('StateController', ['AnalysisService', StateController])
    .controller('PreviewController', [PreviewController])
    .controller('ProjectShareController', ['AnalysisService', 'notifications', ProjectShareController])
  ;
})(window.angular, window.PAGE, window.notifications);
