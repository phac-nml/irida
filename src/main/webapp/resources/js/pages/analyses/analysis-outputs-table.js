import $ from "jquery";
import { Grid } from "ag-grid/main";
import { formatDate } from "../../utilities/date-utilities";
import { escapeHtml } from "../../utilities/html-utilities";
import { download } from "../../utilities/file.utilities";

import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

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
  error: "!!!ERROR!!!",
  "analysis.batch-download.preparing": "PREPARING DOWNLOAD",
  "analysis.automated-analyses": "AUTOMATED ANALYSES",
  "analysis.shared-analyses": "SHARED ANALYSES",
  "analysis.batch-download.help-info": "SEND HELP"
};
I18N = Object.assign(I18N, window.PAGE.i18n);

const helpInfoIcon = `
<i class="fa fa-2x fa-question-circle spaced-left__sm text-info" 
   data-toggle="tooltip"
   data-placement="auto right"
   title="${escapeHtml(I18N["analysis.batch-download.help-info"])}">
</i>`;

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
 * URL to get analysis output file info via AJAX for a project or user
 * @param {boolean} isShared If true get shared analysis output, else false get automated
 * @return {string} AJAX URL to get analysis output file info
 */
function getAjaxUrl(isShared = true) {
  if (PROJECT_ID) {
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
    download(url);
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
        const projectOrUser = PROJECT_ID ? `projectId-${PROJECT_ID}` : `user`;
        downloadUrl += `?filename=${projectOrUser}-batch-download-${selectionSize}-analysis-output-files`;
        download(downloadUrl);
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
      isPreparing
        ? I18N["analysis.batch-download.preparing"]
        : I18N["form.download"]
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
          headerName: I18N["sample.sampleName"],
          checkboxSelection: true,
          headerCheckboxSelection: true,
          headerCheckboxSelectionFilteredOnly: true,
          cellRenderer: p => {
            const { sampleId, sampleName } = p.data;
            const projectUrlPrefix = PROJECT_ID
              ? `projects/${PROJECT_ID}/`
              : "";
            return `<a href="${BASE_URL}${projectUrlPrefix}samples/${sampleId}" target="_blank">${sampleName}</a>`;
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
          headerName: I18N["analysis.table.type"]
        },
        {
          field: "workflowId",
          headerName: I18N["pipeline"],
          cellRenderer: p => {
            const wfInfo = workflowIds[p.data.workflowId];
            if (wfInfo === null) return p.data.workflowId;
            return `${wfInfo.name} (v${wfInfo.version})`;
          }
        },
        {
          field: "analysisSubmissionName",
          headerName: I18N["analysis-submission"],
          cellRenderer: p =>
            `<a href="${BASE_URL}analysis/${
              p.data.analysisSubmissionId
            }" target="_blank">${p.data.analysisSubmissionName}</a>`
        },
        PROJECT_ID
          ? {
              field: "userId",
              headerName: I18N["project.export.submitter"],
              cellRenderer: p =>
                `${p.data.userFirstName} ${p.data.userLastName}`
            }
          : null,
        {
          field: "createdDate",
          headerName: I18N["analysis.date-created"],
          cellRenderer: p => formatDate({ date: p.data.createdDate })
        }
      ].filter(header => header !== null);
      const gridId = `grid-outputs`;
      const $grid = $(
        `<div id="${gridId}" class="ag-theme-balham" style="height: 600px; width: 100%; resize: both;"/>`
      );
      const $dlButton = $(
        `<button type="button" class="btn spaced-bottom" disabled="disabled"></button>`
      );
      setDownloadButtonHtml($dlButton, 0, true);
      $app.prepend($dlButton);
      const $helpIcon = $(helpInfoIcon);
      $app.append($helpIcon);
      $helpIcon.tooltip({ container: "body" });
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
    })
    .fail((xhr, error, exception) => {
      const $alert = $(
        `<div class="alert alert-danger"><h4>${
          I18N["analysis.batch-download.ajax.error"]
        }</h4></div>`
      );
      if (xhr !== null) {
        $alert.append(
          $(`<p>${I18N["error.request.status-code"]}: ${xhr.status}</p>`)
        );
        $alert.append($(`<p>${I18N["error.request.url"]}: ${AJAX_URL}</p>`));
      }
      if (exception !== null) {
        $alert.append(
          $(`<p>${I18N["error.request.status-text"]}: ${exception}</p>`)
        );
      }
      if (error !== null) {
        $alert.append($(`<p>${I18N["error"]}: "${error}"</p>`));
      }
      $app.append($alert);
    });
}

// init table on page load with outputs shared to project
getTableData(window.PAGE.page === "shared");
