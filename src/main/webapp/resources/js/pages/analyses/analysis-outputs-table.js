import { Grid } from "ag-grid-community/main";
import { formatDate } from "../../utilities/date-utilities";
import { escapeHtml, newElement } from "../../utilities/html-utilities";
import { download } from "../../utilities/file.utilities";
import {
  getPrincipalUserSingleSampleAnalysisOutputs,
  getProjectAutomatedSingleSampleAnalysisOutputs,
  getProjectSharedSingleSampleAnalysisOutputs,
  prepareAnalysisOutputsDownload
} from "../../apis/analysis/analysis";
import { getIridaWorkflowDescription } from "../../apis/pipelines/pipelines";

import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-balham.css";

/**
 * Internationalized messages
 * @type {Object} map of messages key name to i18n text
 */
let I18N = {
  "sample.sampleName": "SAMPLE NAME",
  "bc.file": "FILE",
  "analysis.table.type": "ANALYSIS TYPE",
  pipeline: "PIPELINE",
  "analysis-submission": "ANALYSIS SUBMISSION",
  "analysis.date-created": "CREATED",
  "form.download": "DOWNLOAD",
  "project.export.submitter": "SUBMITTER",
  "error.request.status-code": "STATUS CODE",
  "error.request.url": "REQUEST URL",
  "error.request.status-text": "STATUS TEXT",
  "analysis.batch-download.ajax.error": "REQUEST ERROR",
  "analysis.batch-download.preparing": "PREPARING DOWNLOAD",
  "analysis.automated-analyses": "AUTOMATED ANALYSES",
  "analysis.shared-analyses": "SHARED ANALYSES",
  "analysis.batch-download.help-info": "SEND HELP"
};
I18N = Object.assign(I18N, window.PAGE.i18n);

const helpInfoIcon = `<i class="fa fa-2x fa-question-circle spaced-left__sm text-info" title="${escapeHtml(
  I18N["analysis.batch-download.help-info"]
)}"></i>`;

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
 * Project id if on Project Analysis Outputs page; null if on User Analysis Outputs page
 * @type {?number}
 */
const PROJECT_ID = (() => {
  try {
    return window.project.id;
  } catch (e) {
    // No window.project.id? Then we must be on the User Analysis Outputs page!
    return null;
  }
})();

/**
 * HTML container for dynamically generated table and other UI elements
 * @type {HTMLElement}
 */
const $app = document.getElementById("app");

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
 * Download analysis output files (AOFs) for selected rows in ag-grid table
 *
 * A single selected AOF will be downloaded unzipped.
 * Multiple files are downloaded in the same request as a Zipped response
 * stream.
 *
 * @param {Object} api ag-grid grid options API object
 * @param {HTMLElement} $dlButton
 */
async function downloadSelected($dlButton, api) {
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
    download(url);
  } else if (selectedNodes.length > 1) {
    const outputs = selectedNodes.map(node => node.data);
    const { data, error } = await prepareAnalysisOutputsDownload(outputs);
    if (error) {
      console.error(I18N["analysis.batch-download.ajax.error"], error);
      return;
    }
    const { selectionSize } = data;
    const projectOrUser = PROJECT_ID ? `projectId-${PROJECT_ID}` : `user`;
    const downloadUrl = `${BASE_URL}analysis/ajax/download/selection?filename=${projectOrUser}-batch-download-${selectionSize}-analysis-output-files`;
    download(downloadUrl);
  }
  setDownloadButtonHtml(
    $dlButton,
    selectedNodes.length,
    selectedNodes.length === 0
  );
}

/**
 * Set analysis output file download button inner HTML
 * @param {HTMLElement} $dlButton Download button element
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
  if (isDisabled) {
    $dlButton.setAttribute("disabled", "disabled");
  } else {
    $dlButton.removeAttribute("disabled");
  }

  const badge = selectionLength
    ? `<span class="badge">${selectionLength}</span>`
    : "";
  $dlButton.innerHTML = `<i class="fa fa-download spaced-right__sm"></i> ${
    isPreparing
      ? I18N["analysis.batch-download.preparing"]
      : I18N["form.download"]
  } ${badge}`;
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
  $dlButton.addEventListener("click", e => {
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
  Object.keys(workflowIds).forEach(async function(workflowId) {
    const { data, error } = await getIridaWorkflowDescription(workflowId);
    if (!error) {
      workflowIds[workflowId] = data;
      if (grid) {
        grid.gridOptions.api.redrawRows();
      }
    }
  });
  return workflowIds;
}

function displayErrorAlert(error) {
  const { message, request } = error;
  const { responseURL, statusText, status } = request;
  $app.innerHTML = "";
  const $alert = newElement(
    `<div class="alert alert-danger">
         <h4>${I18N["analysis.batch-download.ajax.error"]}</h4>
         <p>${I18N["error.request.status-text"]}: ${message}; ${statusText}</p>
         <p>${I18N["error.request.status-code"]}: ${status}</p>
         <p>${
           I18N["error.request.url"]
         }: <a href="${responseURL}" target="_blank">${responseURL}</a></p>
       </div>`
  );
  $app.appendChild($alert);
}

/**
 * Get analysis output file (AOF) table information and create table.
 * @param {boolean} isShared If project analyses to be shown, show outputs shared with project, otherwise show automated analyses.
 */
async function getTableData(isShared = true) {
  const { data, error } = await (PROJECT_ID === null
    ? getPrincipalUserSingleSampleAnalysisOutputs()
    : isShared
    ? getProjectSharedSingleSampleAnalysisOutputs(PROJECT_ID)
    : getProjectAutomatedSingleSampleAnalysisOutputs(PROJECT_ID));
  if (error) {
    displayErrorAlert(error);
    return;
  }

  $app.innerHTML = "";
  const singleSampleOutputs = filterSingleSampleOutputs(data);
  const workflowIds = getWorkflowInfo(singleSampleOutputs);
  /**
   * ag-grid Grid header definitions
   * @type {*[]}
   */
  const HEADERS = [
    {
      field: "sampleName",
      headerName: I18N["sample.sampleName"],
      checkboxSelection: true,
      headerCheckboxSelection: true,
      headerCheckboxSelectionFilteredOnly: true,
      cellRenderer: p => {
        const { sampleId, sampleName } = p.data;
        const projectUrlPrefix = PROJECT_ID ? `projects/${PROJECT_ID}/` : "";
        return `<a href="${BASE_URL}${projectUrlPrefix}samples/${sampleId}/details" target="_blank">${sampleName}</a>`;
      }
    },
    {
      field: "filePath",
      headerName: I18N["bc.file"],
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
      headerName: I18N["analysis.table.type"],
      valueGetter: function(p) {
        return p.data.analysisType.type;
      }
    },
    {
      field: "workflowId",
      headerName: I18N["pipeline"],
      valueGetter: function(p) {
        const wfInfo = workflowIds[p.data.workflowId];
        if (wfInfo === null) return p.data.workflowId;
        const version =
          wfInfo.version === "unknown"
            ? I18N["analysis.table.version.unknown"]
            : "v" + wfInfo.version;
        const name =
          wfInfo.name === "unknown"
            ? I18N["analysis.table.name.unknown"]
            : wfInfo.name;
        return `${name} (${version})`;
      }
    },
    {
      field: "analysisSubmissionName",
      headerName: I18N["analysis-submission"],
      cellRenderer: p =>
        `<a href="${BASE_URL}analysis/${p.data.analysisSubmissionId}" target="_blank">${p.data.analysisSubmissionName}</a>`
    },
    PROJECT_ID
      ? {
          field: "userId",
          headerName: I18N["project.export.submitter"],
          cellRenderer: p => `${p.data.userFirstName} ${p.data.userLastName}`
        }
      : null,
    {
      field: "createdDate",
      headerName: I18N["analysis.date-created"],
      cellRenderer: p => formatDate({ date: p.data.createdDate })
    }
  ].filter(header => header !== null);
  const $grid = newElement(
    `<div id="grid-outputs" class="ag-theme-balham" style="height: 600px; width: 100%; resize: both;"/>`
  );
  const $dlButton = newElement(
    `<button type="button" class="btn spaced-bottom" disabled="disabled"></button>`
  );
  const $helpIcon = newElement(helpInfoIcon);
  setDownloadButtonHtml($dlButton, 0, true);
  $app.appendChild($dlButton);
  $app.appendChild($helpIcon);
  $app.appendChild($grid);

  /**
   * Update the ag-grid container element height on window resize to fill out
   * the available vertical space
   */
  const updateHeight = () => {
    const BOTTOM_PADDING = 20;
    const top = $grid.children[0].getBoundingClientRect().top;
    let height = window.innerHeight - top - BOTTOM_PADDING;
    // prevent table from getting too small
    if (height < 300) {
      height = 300;
    }
    $grid.style.height = `${height}px`;
  };
  window.addEventListener("resize", updateHeight);
  /**
   * Set `grid` to initialized ag-grid Grid for access to Grid API.
   * @type {Grid}
   */
  grid = initAgGrid($grid, HEADERS, singleSampleOutputs, $dlButton);
  // initialize height of ag-grid table
  updateHeight();
}

// init table on page load with outputs shared to project
getTableData(window.PAGE.page === "shared");
