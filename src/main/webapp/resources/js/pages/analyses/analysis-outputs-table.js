import $ from "jquery";
import { Grid } from "ag-grid/main";
import { formatDate } from "../../utilities/date-utilities";

import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

/**
 * Internationalized text from div#messages.hidden
 * @type {Object} map of data attribute key name to i18n text
 */
let MESSAGES = {
  sampleName: "SAMPLE NAME",
  file: "FILE",
  analysisType: "ANALYSIS TYPE",
  pipeline: "PIPELINE",
  analysisSubmissionName: "ANALYSIS SUBMISSION",
  createdDate: "CREATED",
  download: "DOWNLOAD",
  submitter: "SUBMITTER",
  statusCode: "STATUS CODE",
  requestUrl: "REQUEST URL",
  statusText: "STATUS TEXT",
  reqError: "REQUEST ERROR",
  error: "!!!ERROR!!!",
  preparing: "PREPARING DOWNLOAD",
  automatedAnalyses: "AUTOMATED ANALYSES",
  sharedAnalyses: "SHARED ANALYSES"
};
MESSAGES = Object.assign(MESSAGES, $("#js-messages").data());

/**
 * Analysis output file path regex to capture filename with extension
 * @type {RegExp}
 */
const FILENAME_REGEX = /.*\/(.+\.\w+)/;

/**
 * Base URL for AJAX requests.
 * @type {string}
 */
const BASE_URL = window.PAGE.URLS.base;

/**
 * Get the project id if on Project Analysis Outputs page; null if on User Analysis Outputs page
 * @return {?number} Project id if on Project Analysis Outputs page; null if on User Analysis Outputs page
 */
function getProjectId() {
  try {
    return window.project.id;
  } catch (e) {
    return null;
  }
}

/**
 * URL to get analysis output file info via AJAX for a project or user
 * @param {boolean} isShared If true get shared analysis output, else false get automated
 * @return {string} AJAX URL to get analysis output file info
 */
function getAjaxUrl(isShared = true) {
  const projectId = getProjectId();
  if (projectId) {
    return isShared
      ? window.PAGE.URLS.sharedAnalyses
      : window.PAGE.URLS.automatedAnalyses;
  } else {
    return `${BASE_URL}analysis/ajax/user/analysis-outputs`;
  }
}

/**
 * HTML container for dynamically generated table and other UI elements
 * @type {jQuery|HTMLElement}
 */
const $app = $("#app");

/**
 * ag-grid Grid instance object
 * @type {Grid}
 */
let grid;

/**
 * Get filename from path
 * @param path File path
 */
function getFilename(path) {
  return path.replace(FILENAME_REGEX, "$1");
}

/**
 * Download a file with a temporary hidden <a> element.
 *
 * @param {string} url URL for download
 * @param {string} downloadName Download filename
 */
function downloadFile(url, downloadName) {
  /**
   * Hidden <a> element for downloading each AOF
   * @type {HTMLAnchorElement}
   */
  const $a = document.createElement("a");
  $a.style.display = "none";
  document.body.appendChild($a);
  $a.setAttribute("href", url);
  $a.setAttribute("download", downloadName);
  $a.click();
  document.body.removeChild($a);
}

/**
 * Download analysis output files (AOFs) for selected rows in ag-grid table
 *
 * A single selected AOF will be downloaded unzipped.
 * Multiple files are downloaded in the same request as a Zipped response
 * stream.
 *
 * @param {Object} api ag-grid grid options API object
 * @param {jQuery|HTMLElement} $dlButton
 */
function downloadSelected($dlButton, api) {
  /**
   * Selected rows in the ag-grid Grid corresponding to AOFs
   * @type {*|RowNode[]}
   */
  const selectedNodes = api.getSelectedNodes();
  setDownloadButtonHtml($dlButton, selectedNodes.length, true, true);
  if (selectedNodes.length === 1) {
    const {
      analysisSubmissionId,
      analysisOutputFileId,
      sampleName,
      sampleId,
      filePath
    } = selectedNodes[0].data;
    let url = `${BASE_URL}analysis/ajax/download/${analysisSubmissionId}/file/${analysisOutputFileId}`;
    const downloadName = `${sampleName}-sampleId-${sampleId}-analysisSubmissionId-${analysisSubmissionId}-${getFilename(
      filePath
    )}`;
    url += "?" + $.param({ filename: downloadName });
    downloadFile(url, downloadName);
    setDownloadButtonHtml($dlButton, selectedNodes.length);
  } else if (selectedNodes.length > 1) {
    const outputs = selectedNodes.map(node => node.data);
    let url = `${BASE_URL}analysis/ajax/download/prepare`;
    let downloadUrl = `${BASE_URL}analysis/ajax/download/selection`;
    $.ajax({
      url,
      type: "POST",
      data: JSON.stringify(outputs),
      contentType: "application/json",
      dataType: "json",
      success: ({ selectionSize }) => {
        const projectId = getProjectId();
        const projectOrUser = projectId ? `projectId-${projectId}` : `user`;
        downloadUrl += `?filename=${projectOrUser}-batch-download-${selectionSize}-analysis-output-files`;
        downloadFile(downloadUrl, "batch-download.zip");
        setDownloadButtonHtml($dlButton, selectedNodes.length);
      },
      error: (jqXHR, textStatus, errorThrown) => {
        console.error(jqXHR, textStatus, errorThrown);
        setDownloadButtonHtml($dlButton, selectedNodes.length);
      }
    });
  } else {
    $dlButton.attr("disabled", null);
  }
}

/**
 * Set analysis output file download button inner HTML
 * @param {jQuery|HTMLElement} $dlButton Download button element
 * @param {number} selectionLength Number of rows selected for download
 * @param {boolean} isDisabled Disable the download button?
 * @param {boolean} isPreparing Is the download being prepared? It's necessary to prepare downloads for large collections of analysis output files.
 */
function setDownloadButtonHtml(
  $dlButton,
  selectionLength,
  isDisabled = false,
  isPreparing = false
) {
  $dlButton.attr("disabled", isDisabled ? "disabled" : null);
  const badge = selectionLength
    ? `<span class="badge">${selectionLength}</span>`
    : "";
  $dlButton.html(
    `<i class="fa fa-download spaced-right__sm"></i> ${
      isPreparing ? MESSAGES.preparing : MESSAGES.download
    } ${badge}`
  );
}

/**
 * Initialize ag-grid Grid
 * @param {HTMLElement} $grid Element to create Grid in
 * @param {Array<Object<string>>} headers Table fields
 * @param {Array<Object>} rows Table data
 * @param {jQuery|HTMLElement} $dlButton Download button
 * @return {Grid} ag-grid object
 */
function initAgGrid($grid, headers, rows, $dlButton) {
  const gridOptions = {
    enableColResize: true,
    columnDefs: headers,
    rowData: rows,
    rowDeselection: true,
    enableSorting: true,
    enableFilter: true,
    rowSelection: "multiple",
    onSelectionChanged: e => {
      const selectedNodes = e.api.getSelectedNodes();
      const selectionLength = selectedNodes.length;
      setDownloadButtonHtml($dlButton, selectionLength, selectionLength === 0);
    }
  };
  const grid = new Grid($grid, gridOptions);
  gridOptions.api.sizeColumnsToFit();
  $dlButton.on("click", e => {
    e.preventDefault();
    downloadSelected($dlButton, gridOptions.api);
  });
  return grid;
}

/**
 * Filter for single sample analysis outputs.
 * @param data
 * @returns {Array<Object>}
 */
function filterSingleSampleOutputs(data) {
  // group analysis output file (AOF) info by AOF id
  const groupedDataByAofId = data.reduce((acc, x) => {
    const { analysisOutputFileId } = x;
    if (acc.hasOwnProperty(analysisOutputFileId)) {
      acc[analysisOutputFileId].push(x);
    } else {
      acc[analysisOutputFileId] = [x];
    }
    return acc;
  }, {});
  // return AOF info that only has one Sample assoc with the AOF id, i.e. single
  // sample AOFs
  return Object.keys(groupedDataByAofId)
    .filter(x => groupedDataByAofId[x].length === 1)
    .map(x => groupedDataByAofId[x][0]);
}

/**
 * Get workflow/pipeline info and save to `workflowIds` map.
 * @param {Array<Object>} singleSampleOutputs Single sample analysis output file infos
 * @returns {Object<Object>} Map of workflow id to workflow info map.
 */
function getWorkflowInfo(singleSampleOutputs) {
  const workflowIds = singleSampleOutputs.reduce(
    (acc, x) => Object.assign(acc, { [x.workflowId]: null }),
    {}
  );
  Object.keys(workflowIds).forEach(workflowId => {
    $.get(`${BASE_URL}pipelines/ajax/${workflowId}`).done(wfInfo => {
      workflowIds[workflowId] = wfInfo;
      if (grid) {
        grid.context.beans.gridApi.beanInstance.redrawRows();
      }
    });
  });
  return workflowIds;
}

/**
 * Create radio button toggle for showing
 *
 * @param {boolean} isShared Check shared outputs if true, show automated outputs if false
 */
function createSharedOrAutomatedRadioButtons(isShared = true) {
  const $toggleSharedOrAutomated = $(`<div>
    <input type="radio" id="sharedAnalyses" value="shared" name="sharedOrAutomated" ${
      isShared ? "checked" : ""
    }>
    <label for="sharedAnalyses">${MESSAGES.sharedAnalyses}</label>
    <input type="radio" id="automatedAnalyses" value="automated" name="sharedOrAutomated" ${
      isShared ? "" : "checked"
    }>
    <label for="automatedAnalyses">${MESSAGES.automatedAnalyses}</label>
  </div>`);

  $app.prepend($toggleSharedOrAutomated);

  $("input[type=radio][name=sharedOrAutomated]").on("change", e => {
    getTableData(e.target.value === "shared");
  });
}

/**
 * Get analysis output file (AOF) table information and create table.
 * @param {boolean} isShared If project analyses to be shown, show outputs shared with project, otherwise show automated analyses.
 */
function getTableData(isShared = true) {
  $.get(getAjaxUrl(isShared))
    .done(data => {
      $app.html("");
      const singleSampleOutputs = filterSingleSampleOutputs(data);
      const workflowIds = getWorkflowInfo(singleSampleOutputs);
      /**
       * ag-grid Grid header definitions
       * @type {*[]}
       */
      const HEADERS = [
        {
          field: "sampleName",
          headerName: MESSAGES.sampleName,
          checkboxSelection: true,
          headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true,
          cellRenderer: p => {
            const { sampleId, sampleName } = p.data;
            const projectId = getProjectId();
            const projectUrlPrefix = projectId ? `projects/${projectId}/` : "";
            return `<a href="${BASE_URL}${projectUrlPrefix}samples/${sampleId}" target="_blank">${sampleName}</a>`;
          }
        },
        {
          field: "filePath",
          headerName: MESSAGES.file,
          cellRenderer: p => {
            const {
              filePath,
              analysisOutputFileKey,
              analysisOutputFileId
            } = p.data;
            const REGEX = /^\d+\/\d+\/(.+)$/;
            const groups = REGEX.exec(filePath);
            if (groups === null) return filePath;
            const filename = groups[1];

            return `${filename} <small>(${analysisOutputFileKey}, id=${analysisOutputFileId})</small>`;
          }
        },
        {
          field: "analysisType",
          headerName: MESSAGES.analysisType
        },
        {
          field: "workflowId",
          headerName: MESSAGES.pipeline,
          cellRenderer: p => {
            const wfInfo = workflowIds[p.data.workflowId];
            if (wfInfo === null) return p.data.workflowId;
            return `${wfInfo.name} (v${wfInfo.version})`;
          }
        },
        {
          field: "analysisSubmissionName",
          headerName: MESSAGES.analysisSubmissionName,
          cellRenderer: p =>
            `<a href="${BASE_URL}analysis/${
              p.data.analysisSubmissionId
            }" target="_blank">${p.data.analysisSubmissionName}</a>`
        },
        getProjectId()
          ? {
              field: "userId",
              headerName: MESSAGES.submitter,
              cellRenderer: p =>
                `${p.data.userFirstName} ${p.data.userLastName}`
            }
          : null,
        {
          field: "createdDate",
          headerName: MESSAGES.createdDate,
          cellRenderer: p => formatDate({ date: p.data.createdDate })
        }
      ].filter(header => header !== null);
      const gridId = `grid-outputs`;
      const $grid = $(
        `<div id="${gridId}" class="ag-theme-balham" style="height: 600px; width: 100%; resize: both;"/>`
      );
      const $dlButton = $(
        `<button type="button" class="btn" disabled="disabled"></button>`
      );
      setDownloadButtonHtml($dlButton, 0, true);
      $app.prepend($dlButton);
      $app.append($grid);

      /**
       * Set `grid` to initialized ag-grid Grid for access to Grid API.
       * @type {Grid}
       */
      grid = initAgGrid(
        document.getElementById(gridId),
        HEADERS,
        singleSampleOutputs,
        $dlButton
      );
      if (getProjectId()) {
        createSharedOrAutomatedRadioButtons(isShared);
      }
    })
    .fail((xhr, error, exception) => {
      const $alert = $(
        `<div class="alert alert-danger"><h4>${MESSAGES.reqError}</h4></div>`
      );
      if (xhr !== null) {
        $alert.append($(`<p>${MESSAGES.statusCode}: ${xhr.status}</p>`));
        $alert.append($(`<p>${MESSAGES.requestUrl}: ${AJAX_URL}</p>`));
      }
      if (exception !== null) {
        $alert.append($(`<p>${MESSAGES.statusText}: ${exception}</p>`));
      }
      if (error !== null) {
        $alert.append($(`<p>${MESSAGES.error}: "${error}"</p>`));
      }
      $app.append($alert);
    });
}

// init table on page load with outputs shared to project
getTableData();
