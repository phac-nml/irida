import angular from "angular";
import { showNotification } from "../../modules/notifications";
import { formatDate } from "../../utilities/date-utilities";
import "../../../sass/pages/analysis.scss";
import $ from "jquery";
import "slickgrid-6pac/slick.core";
import "slickgrid-6pac/slick.grid";
import "slickgrid-6pac/slick.dataview";

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

  vm.getZipFile = function(id) {
    var iFrameId = "hiddenDownloader";
    var hiddenIFrame = document.getElementById(iFrameId);
    if (hiddenIFrame === null) {
      hiddenIFrame = document.createElement("iframe");
      hiddenIFrame.id = iFrameId;
      hiddenIFrame.style.display = "none";
      document.body.appendChild(hiddenIFrame);
    }
    hiddenIFrame.src = page.URLS.download + id;
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
   * @param vm
   * @returns {PromiseLike<T> | Promise<T> | *}
   */
  svc.getTabularData = function(vm) {
    if (svc._tabularData === null) {
      const url = window.PAGE.URLS.base + window.PAGE.ID + "/tabular-data";
      return $http.get(url).then(
        function successCallback(x) {
          svc._tabularData = x.data;
          return svc._tabularData;
        },
        function errorCallback(x) {
          console.error(`Could not GET tabular data from "${url}"`);
          console.error(x);
        }
      );
    } else {
      console.log("already fetched:", svc._tabularData);
      const promise = new Promise(function(resolve, reject) {
        resolve(svc._tabularData);
      });
      return promise;
    }
  };

  /**
   * Get tabular data info from server
   * @param vm
   * @returns {PromiseLike<T> | Promise<T> | *}
   */
  svc.getOutputsInfo = function(vm) {
    if (svc._outputsInfo === null) {
      const url = `${window.PAGE.URLS.base}${window.PAGE.ID}/outputs`;
      return $http.get(url).then(
        function successCallback(x) {
          svc._outputsInfo = x.data;
          return svc._outputsInfo;
        },
        function errorCallback(x) {
          console.error(`Could not GET outputs info from "${url}"`);
          console.error(x);
        }
      );
    } else {
      console.log("already fetched:", svc._outputsInfo);
      const promise = new Promise(function(resolve, reject) {
        resolve(svc._outputsInfo);
      });
      return promise;
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
    const data = { project: project, shared: shared };
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

function PreviewController() {
  this.newick = window.PAGE.NEWICK;
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

function TablesController(analysisService) {
  const MAX_TABLE_HEIGHT = 300; //px
  const TEXT_CHUNK_SIZE = 5000; //bytes
  const vm = this;
  const $tablesContainer = $("#js-tables-container");

  analysisService.getOutputsInfo(vm).then(result => {
    console.log("OUTPUTS", result);
    let count = 0;
    let options = {
      rowHeight: 20,
      editable: false,
      enableAddRow: false,
      enableCellNavigation: false
    };
    const setTabularExt = new Set(["tab", "tsv", "tabular"]);
    for (const t of result) {
      console.log("count", count, "t", t);
      if (!t.hasOwnProperty("fileExt")) {
        continue;
      }
      console.log(
        "setTAB",
        setTabularExt,
        t.fileExt,
        setTabularExt.has(t.fileExt)
      );
      if (!setTabularExt.has(t.fileExt)) {
        const $panel = $(
          `<div id="js-panel-${count}" class="panel panel-default"/>`
        );
        const $panelHeading = $(
          `<div class="panel-heading"><h5>${t.outputName} - ${
            t.filename
          }</h5></div>`
        );
        $panel.append($panelHeading);
        const $panelBody = $(`<div class="panel-body"></div>`);
        const gridId = `js-text-${count}`;
        const $table = $(`<pre/>`, {
          id: gridId
        });
        $table.css({
          "white-space": "pre-wrap",
          resize: "both",
          height: `${MAX_TABLE_HEIGHT}px`,
          width: "100%"
        });
        const fileSizeBytes = t.fileSizeBytes;
        const baseUrl = `${window.PAGE.URLS.base}${window.PAGE.ID}/outputs/${
          t.id
        }`;
        const params = {
          seek: 0,
          chunk: Math.min(fileSizeBytes, TEXT_CHUNK_SIZE)
        };
        const url = `${baseUrl}?${$.param(params)}`;
        let $showMore = null;
        if (t.fileSizeBytes > TEXT_CHUNK_SIZE) {
          $showMore = $(
            `<button href="#" id="js-show-more-${
              t.id
            }" disabled="disabled" class="btn btn-default">SHOW MORE</button>`
          );
        }
        let showMoreUrl = `${baseUrl}?${$.param(params)}`;

        function onTextScroll() {
          if (params.chunk === 0) {
            console.log("no more to show!");
            return;
          }
          if (
            $(this).scrollTop() + $(this).innerHeight() >=
            $(this)[0].scrollHeight
          ) {
            console.log("SHOWING MORE", showMoreUrl);
            showMoreUrl = `${baseUrl}?${$.param(params)}`;
            $.ajax({
              url: showMoreUrl,
              success: function(resp, statusText, xOpts) {
                $table.text($table.text() + resp.text);
                params.seek = resp.filePointer;
                params.chunk = Math.min(
                  fileSizeBytes - params.seek,
                  TEXT_CHUNK_SIZE
                );
                if (params.chunk === 0) {
                  $showMore.prop("disabled", true);
                  $showMore.css({ display: "none" });
                } else {
                  $showMore.prop("disabled", false);
                  showMoreUrl = `${baseUrl}?${$.param(params)}`;
                }
                console.log("SCROLLED", params, showMoreUrl);
              }
            });
          }
        }

        $.ajax({
          url: url,
          success: (resp, statusText, xOpts) => {
            $table.text(resp.text);
            if ($showMore == null) return;
            const seek = resp.filePointer;
            params.seek = seek;
            params.chunk = Math.min(fileSizeBytes - seek, TEXT_CHUNK_SIZE);
            if (params.chunk === 0) {
              $showMore.css({ display: "none" });
            } else {
              $table.on("scroll", onTextScroll);
              $showMore.prop("disabled", false);
              $showMore.on("click", () => {
                $showMore.prop("disabled", true);
                $.ajax({
                  url: showMoreUrl,
                  success: (resp, statusText, xOpts) => {
                    $table.text($table.text() + resp.text);
                    params.seek = resp.filePointer;
                    params.chunk = Math.min(
                      fileSizeBytes - params.seek,
                      TEXT_CHUNK_SIZE
                    );
                    if (params.chunk === 0) {
                      $showMore.prop("disabled", true);
                      $showMore.css({ display: "none" });
                    } else {
                      $showMore.prop("disabled", false);
                      showMoreUrl = `${baseUrl}?${$.param(params)}`;
                    }
                    console.log(
                      "SHOW MORE BUTTON CLICKED",
                      params,
                      showMoreUrl
                    );
                  }
                });
              });
            }
          }
        });

        $panelBody.append($table);
        if ($showMore !== null) {
          $panelBody.append($showMore);
        }
        $panel.append($panelBody);
        $tablesContainer.append($panel);

        continue;
      }
      let headers = [];
      const firstRow = t.firstLine.split("\t");
      for (let i = 0; i < firstRow.length; i++) {
        const row = firstRow[i];
        // headers.push({ title: x });
        headers.push({ id: i + "", field: i + "", name: row, sortable: true });
      }
      const loader = Slick.Data.RemoteModel(t.id, t.filePointer);
      const $panel = $(
        `<div id="js-panel-${count}" class="panel panel-default"/>`
      );
      const $panelHeading = $(
        `<div class="panel-heading"><h5>${t.outputName} - ${
          t.filename
        }</h5></div>`
      );
      $panel.append($panelHeading);
      const $panelBody = $(`<div class="panel-body"></div>`);
      const gridId = `grid-${count}`;
      const $table = $(`<div/>`, {
        id: gridId,
        class: "display",
        height: `${MAX_TABLE_HEIGHT}px`,
        width: "100%"
      });
      $table.appendTo($panelBody);
      $panel.append($panelBody);
      $panel.appendTo($tablesContainer);
      // const dataView = new Slick.Data.DataView();
      const grid = new Slick.Grid(`#${gridId}`, loader.data, headers, options);
      grid.onViewportChanged.subscribe((e, args) => {
        const vp = grid.getViewport();
        loader.ensureData(vp.top, vp.bottom);
      });
      loader.onDataLoading.subscribe(() => {
        console.log("Loading!", t);
      });
      loader.onDataLoaded.subscribe((e, args) => {
        for (let i = args.from; i <= args.to; i++) {
          grid.invalidateRow(i);
        }
        grid.updateRowCount();
        grid.render();
        console.log("LOADED!", e, args);
      });
      grid.onViewportChanged.notify();
      count++;
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
        .state("tables", {
          url: "/tables",
          templateUrl: "tables.html"
        })
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
  .controller("TablesController", ["AnalysisService", TablesController])
  .controller("PreviewController", [PreviewController])
  .controller("ProjectShareController", [
    "AnalysisService",
    ProjectShareController
  ]).name;

angular.module("irida").requires.push(iridaAnalysis);
