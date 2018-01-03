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
      hiddenIFrame.src = page.URLS.download + id
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
    };

    svc.getJobErrors = function(vm) {
      vm.is_in_progress = true;
      return $http.get(page.URLS.job_errors).then(function successCallback(x) {
        vm.is_in_progress = false;
        return x.data;
      }, function errorCallback(x) {
        vm.is_in_progress = false;
        console.error("Could not GET job error(s) from '" + page.URLS.job_errors + "'");
        console.error(x);
      });

    };

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

  function ProjectShareController(AnalysisService) {
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
        notifications.show({text: response.message});
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
      if (result['parse_results_error']) {
        vm.parse_results_error = true;
      } else {
        var sample_information = {};
        sample_information['name'] = result['sample_name'];
        sample_information['qc_status'] = result['qc_status'];
        sample_information['qc_messages'] = result['qc_messages'].split("|");
        sample_information['qc_pass'] = (result['qc_status'] == 'PASS');
        sample_information['qc_warning'] = (result['qc_status'] == 'WARNING');
        sample_information['qc_fail'] = (result['qc_status'] == 'FAIL');

        var cgMLST_predictions = {};
        cgMLST_predictions['matching_alleles'] = result['cgmlst_matching_alleles']+'/330';
        cgMLST_predictions['percent_matching'] = parseFloat((1 - result['cgmlst_distance'])*100).toFixed(1)+"%";
  
        var mash_predictions = {};
        mash_predictions['distance'] = result['mash_distance'];
  
	vm.result = result;
        vm.sample_information = sample_information;
        vm.cgMLST_predictions = cgMLST_predictions;
        vm.mash_predictions = mash_predictions;
        vm.parse_results_error = result['parse_results_error'];
      }
    });
  }

  function JobErrorsController(analysisService) {
    const vm = this;
    vm.has_job_errors = false;
    vm.is_in_progress = true;
    vm.job_errors = [];
    vm.sortLines = function(obj, attr) {
      obj[attr] = obj[attr].split('\n').reverse().join('\n').trim();
    }
    vm.isLoading = function() {
      return !vm.has_job_errors && vm.is_in_progress;
    }
    vm.hasNoJobErrorInfoAvailable = function() {
      return !vm.has_job_errors && !vm.is_in_progress;
    }
    vm.jsonifyParameters = function(j) {
      try {
        if (j.hasOwnProperty("parameters")) {
          j.parameters = JSON.parse(j.parameters.replace(/=/g,':')
                                     .replace(/(:)\s*([\w\-]+)/g, "\$1\"\$2\"")
                                     .replace(/([\w\|]+):/g, '"\$1":'));
        }
      } catch (e) {
        console.error(e);
        console.error("Could not JSONify 'parameters' for job error object");
      }
    }
    vm.formatDate = function(j, attr) {
      try {
        if (j.hasOwnProperty(attr)) {
          const t = new Date(j[attr]);
          if (moment.isDate(t)) {
            j[attr] = moment(t).format("lll");
          }
        }
      } catch (e) {
        console.error(e);
        console.error("Could not format '" + attr + "' of job error object as Date");
      }
    }
    analysisService.getJobErrors(vm).then(function(x) {
      if (typeof x === "undefined" || x === null) {
        return;
      }
      if (typeof x.job_errors === "undefined") {
        return;
      }
      if (x.job_errors.length === 0) {
        return;
      }
      vm.has_job_errors = true;
      for (const j of x.job_errors) {
        vm.formatDate(j, "createdDate");
        vm.formatDate(j, "updatedDate");
        vm.jsonifyParameters(j);
      }
      vm.job_errors = x.job_errors;
      vm.is_in_progress = false;
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
        .state("joberrors", {
          url: "/joberrors",
          templateUrl: "joberrors.html",
          controllerAs: "jobErrorsCtrl",
          controller: ["AnalysisService", JobErrorsController]
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
    .controller('ProjectShareController', ['AnalysisService', ProjectShareController])
  ;
})(window.angular, window.PAGE, window.notifications);
