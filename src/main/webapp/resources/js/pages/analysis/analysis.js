import angular from "angular";
import $ from "jquery";
import {
  showErrorNotification,
  showNotification
} from "../../modules/notifications";
import { formatDate } from "../../utilities/date-utilities";
import { renderPlainTextPreview } from "./plaintext-preview";
import { renderTabularPreview } from "./tabular-preview";
import "../../../sass/pages/analysis.scss";
import "../../vendor/datatables/datatables";
import { BioHanselController } from "./controllers/bio_hansel";
import { renderJsonPreview } from "./json-preview";

const baseAjaxUrl = window.PAGE.URLS.base;
const analysisSubmissionId = window.PAGE.ID;

/**
 * Controller to download the analysis.
 * @constructor
 */
function FileDownloadController() {
  const vm = this;

  vm.getZipFile = function(id) {
    const iFrameId = "hiddenDownloader";
    let hiddenIFrame = document.getElementById(iFrameId);
    if (hiddenIFrame === null) {
      hiddenIFrame = document.createElement("iframe");
      hiddenIFrame.id = iFrameId;
      hiddenIFrame.style.display = "none";
      document.body.appendChild(hiddenIFrame);
    }
    hiddenIFrame.src = window.PAGE.URLS.download + id;
  };
}

/**
 * Service for Analysis
 * @param $http
 * @returns {AnalysisService}
 * @constructor
 */
function AnalysisService($http) {
  const svc = this;
  svc._tabularData = null;
  svc._outputsInfo = null;
  svc.baseAjaxUrl = baseAjaxUrl;
  /**
   * Call the server to get the status for the current analysis.
   * 'page.URLS.status' is on the `_base.html` page for the analysis.
   * @returns {*}
   * @private
   */
  function _getState() {
    return $http.get(window.PAGE.URLS.status).then(function(data) {
      return data.data;
    });
  }

  /**
   * Get the results from a SISTR analysis to preview in the browser.
   */
  svc.getSistrResults = function() {
    return $http.get(window.PAGE.URLS.sistr).then(function(result) {
      return result.data;
    });
  };
  /**
   * Get Galaxy JobError info from server
   * @param vm JobErrorsController object for reporting progress of getting JobError info
   * @returns {PromiseLike<T> | Promise<T> | *}
   */
  svc.getJobErrors = function(vm) {
    vm.isInProgress = true;
    return $http.get(window.PAGE.URLS.jobErrors).then(
      function successCallback(x) {
        vm.isInProgress = false;
        return x.data;
      },
      function errorCallback(x) {
        vm.isInProgress = false;
        console.error(
          "Could not GET job error(s) from '" + page.URLS.jobErrors + "'"
        );
        console.error(x);
      }
    );
  };

  /**
   * Get tabular data info from server
   * @returns {PromiseLike<T> | Promise<T> | *}
   */
  svc.getOutputsInfo = function() {
    if (svc._outputsInfo === null) {
      const url = `${baseAjaxUrl}${analysisSubmissionId}/outputs`;
      return $http.get(url).then(
        function successCallback(x) {
          svc._outputsInfo = x.data;
          return svc._outputsInfo;
        },
        function errorCallback(x) {
          //TODO: i18n
          const errMsg = `Could not GET outputs info from "${url}". ${x}`;
          console.error(errMsg);
          showErrorNotification({ text: errMsg });
        }
      );
    } else {
      return new Promise(function(resolve, reject) {
        resolve(svc._outputsInfo);
      });
    }
  };

  /**
   * Exported function to call the server for information about the current analysis.
   * @param fn Callback function with how to handle the results.
   */
  svc.getAnalysisState = function(fn) {
    _getState().then(function(data) {
      fn(data);
    });
  };

  /**
   * Call the server to update the shared status of the current analysis.
   */
  svc.updateProjectShare = function(project, shared) {
    const data = { projectId: Number(project), shareStatus: Boolean(shared) };
    return $http.post(window.PAGE.URLS.share, data).then(function(response) {
      return response.data;
    });
  };

  /**
   * Call the server to get the shared status of project
   */
  svc.getSharedProjects = function() {
    return $http.get(window.PAGE.URLS.share).then(function(response) {
      return response.data;
    });
  };

  /**
   * Call the server to save results of a pipeline to the samples
   */
  svc.saveResults = function() {
    angular.element("#save-to-samples").prop("disabled", true);
    return $http.post(window.PAGE.URLS.saveResults).then(function(response) {
      return response.data;
    });
  };

  return svc;
}

function ProjectShareController(AnalysisService) {
  const vm = this;

  vm.projects = {};

  function initialize() {
    AnalysisService.getSharedProjects().then(function(response) {
      vm.projects = response;
    });
  }

  vm.updateShared = function(project) {
    AnalysisService.updateProjectShare(
      project.project.identifier,
      project.shared
    ).then(function(response) {
      showNotification({ text: response.message });
    });
  };

  vm.saveResults = function() {
    AnalysisService.saveResults().then(function(response) {
      if (response.result === "success") {
        showNotification({ text: response.message });
      } else {
        showErrorNotification({ text: response.message });
      }
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
  const vm = this;
  vm.percentage = 0;

  /**
   * Initializes the sate of the analysis.
   */
  function initialize() {
    return AnalysisService.getAnalysisState(function(data) {
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
    return "analysis__alert--" + state.toLowerCase();
  }

  initialize();
}

function PreviewController(analysisService) {
  this.newick = window.PAGE.NEWICK;
  const vm = this;
  const $tablesContainer = $("#js-file-preview-container");
  const tabExtSet = new Set(["tab", "tsv", "tabular", "csv"]);
  const jsonExtSet = new Set(["json"]);

  analysisService.getOutputsInfo(vm).then(outputInfos => {
    for (const outputInfo of outputInfos) {
      if (
        !outputInfo.hasOwnProperty("fileExt") ||
        !outputInfo.hasOwnProperty("id")
      ) {
        continue;
      }
      if (tabExtSet.has(outputInfo.fileExt)) {
        renderTabularPreview($tablesContainer, baseAjaxUrl, outputInfo);
      } else if (jsonExtSet.has(outputInfo.fileExt)) {
        renderJsonPreview($tablesContainer, baseAjaxUrl, outputInfo);
      } else {
        renderPlainTextPreview($tablesContainer, baseAjaxUrl, outputInfo);
      }
    }
  });
}

function SistrController(analysisService) {
  const vm = this;

  analysisService.getSistrResults().then(function(result) {
    if (result["parse_results_error"]) {
      vm.parse_results_error = true;
    } else {
      const sample_information = {};
      sample_information["name"] = result["sample_name"];
      sample_information["qc_status"] = result["qc_status"];
      sample_information["qc_messages"] = result["qc_messages"].split("|");
      sample_information["qc_pass"] = result["qc_status"] == "PASS";
      sample_information["qc_warning"] = result["qc_status"] == "WARNING";
      sample_information["qc_fail"] = result["qc_status"] == "FAIL";

      const cgMLST_predictions = {};
      cgMLST_predictions["matching_alleles"] =
        result["cgmlst_matching_alleles"] + "/330";
      cgMLST_predictions["percent_matching"] =
        parseFloat((1 - result["cgmlst_distance"]) * 100).toFixed(1) + "%";

      const mash_predictions = {};
      mash_predictions["distance"] = result["mash_distance"];

      vm.result = result;
      vm.sample_information = sample_information;
      vm.cgMLST_predictions = cgMLST_predictions;
      vm.mash_predictions = mash_predictions;
      vm.parse_results_error = result["parse_results_error"];
    }
  });
}

/**
 * Angular Controller for handling Galaxy job errors
 * @param analysisService Service for retrieving JobError info from server
 * @constructor
 */
function JobErrorsController(analysisService) {
  const vm = this;
  vm.hasJobErrors = false;
  vm.isInProgress = true;
  /**
   * Array of JobError objects
   * @type {Array}
   */
  vm.jobErrors = [];
  /**
   * Reverse order of lines for a JobError attribute and set whether the
   * attribute is reversed or not.
   * @param jobError JobError object
   * @param attr Attribute to reverse order of lines (e.g. "standardOutput")
   */
  vm.reverseLines = (jobError, attr) => {
    jobError[attr] = jobError[attr]
      .split("\n")
      .reverse()
      .join("\n")
      .trim();
    jobError.reversed[attr] = !jobError.reversed[attr];
  };
  /**
   * Is JobError info being retrieved from the server?
   * @returns {boolean}
   */
  vm.isLoading = () => !vm.hasJobErrors && vm.isInProgress;
  /**
   * Is there JobError info available?
   * @returns {boolean}
   */
  vm.hasNoJobErrorInfoAvailable = () => !vm.hasJobErrors && !vm.isInProgress;
  /**
   * Transform parameters string into valid JSON and parse into Object
   * @param jobError Object with JobError information
   */
  vm.jsonifyParameters = function(jobError) {
    try {
      if (jobError.hasOwnProperty("parameters")) {
        jobError.parameters = JSON.parse(
          jobError.parameters
            .replace(/=/g, ":")
            .replace(/(:)\s*([\w\-]+)/g, '$1"$2"')
            .replace(/([\w|]+):/g, '"$1":')
        );
      }
    } catch (e) {
      console.error(e);
      console.error("Could not JSONify 'parameters' for job error object");
    }
  };
  /**
   * Format JobError attribute as a human-readable date
   * @param jobError JobError object
   * @param attr Attribute containing date value (e.g. "createdDate")
   */
  vm.formatDate = function(jobError, attr) {
    try {
      if (jobError.hasOwnProperty(attr)) {
        jobError[attr] = formatDate({ date: jobError[attr] });
      }
    } catch (e) {
      console.error(e);
      console.error(
        "Could not format '" + attr + "' of job error object as Date"
      );
    }
  };
  analysisService.getJobErrors(vm).then(function(x) {
    // `x` is request data object
    if (typeof x === "undefined" || x === null) {
      return;
    }
    if (typeof x.jobErrors === "undefined") {
      return;
    }
    if (x.jobErrors.length === 0) {
      return;
    }
    vm.jobErrors = x.jobErrors;
    vm.hasJobErrors = true;
    for (const jobError of vm.jobErrors) {
      jobError.reversed = {};
      jobError.reversed.standardError = false;
      jobError.reversed.standardOutput = false;
      vm.formatDate(jobError, "createdDate");
      vm.formatDate(jobError, "updatedDate");
      vm.jsonifyParameters(jobError);
    }
    vm.isInProgress = false;
  });
}

const iridaAnalysis = angular
  .module("irida.analysis", ["ui.router", "subnav", "phylocanvas"])
  .config([
    "$stateProvider",
    function($stateProvider) {
      $stateProvider
        .state("preview", {
          url: "/preview",
          templateUrl: "preview.html"
        })
        .state("sistr", {
          url: "/sistr",
          templateUrl: "sistr.html",
          controllerAs: "sistrCtrl",
          controller: ["AnalysisService", SistrController]
        })
        .state("bio_hansel", {
          url: "/bio_hansel",
          templateUrl: "bio_hansel.html",
          controllerAs: "bioHanselCtrl",
          controller: ["AnalysisService", BioHanselController]
        })
        .state("joberrors", {
          url: "/joberrors",
          templateUrl: "joberrors.html",
          controllerAs: "jobErrorsCtrl",
          controller: ["AnalysisService", JobErrorsController]
        })
        .state("inputs", {
          url: "/inputs",
          templateUrl: "inputs.html"
        })
        .state("provenance", {
          url: "/provenance",
          templateUrl: "provenance.html"
        })
        .state("share", {
          url: "/share",
          templateUrl: "share.html"
        });
    }
  ])
  .service("AnalysisService", ["$http", AnalysisService])
  .controller("FileDownloadController", [FileDownloadController])
  .controller("StateController", ["AnalysisService", StateController])
  .controller("PreviewController", ["AnalysisService", PreviewController])
  .controller("ProjectShareController", [
    "AnalysisService",
    ProjectShareController
  ]).name;

angular.module("irida").requires.push(iridaAnalysis);
