import $ from "jquery";
import { Grid } from "ag-grid/main";
import { formatDate } from "../../utilities/date-utilities";

import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

/**
 * Internationalized text from div#messages.hidden
 * @type {Object} map of data attribute key name to i18n text
 */
const MESSAGES = $("#js-messages").data();

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
 * URL to get analysis output file info via AJAX for a project
 * @type {string}
 */
const AJAX_URL = `${window.PAGE.URLS.base}projects/${
  window.project.id
}/ajax/analysis-outputs`;

/**
 * HTML container for dynamically generating UI for download of output files
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
 * Each selected AOF will be downloaded in a separate request. Multiple files
 * could be downloaded in the same request as a Zipped response stream, but that
 * would require coming up with a way to get around the limit on the query
 * string for an HTTP GET request:
 *
 * Revert "Merge branch 'fix-612-request-uri-too-long' into 'development'"
 * http://gitlab-irida.corefacility.ca/irida/irida/merge_requests/1297
 *
 * @param {Object} api ag-grid grid options API object
 */
function downloadSelected(api) {
  /**
   * Selected rows in the ag-grid Grid corresponding to AOFs
   * @type {*|RowNode[]}
   */
  const selectedNodes = api.getSelectedNodes();

  /**
   * Hidden <a> element for downloading each AOF
   * @type {HTMLAnchorElement}
   */
  const $a = document.createElement("a");
  $a.style.display = "none";
  document.body.appendChild($a);

  // trigger hidden <a> element download of each selected AOF
  for (const node of selectedNodes) {
    const {
      submission_id,
      aof_id,
      sample_name,
      sample_id,
      file_path
    } = node.data;
    let url = `${BASE_URL}analysis/ajax/download/${submission_id}/file/${aof_id}`;
    const downloadName = `${sample_name}-sample_id-${sample_id}-submission_id-${submission_id}-${getFilename(
      file_path
    )}`;
    url += "?" + $.param({ filename: downloadName });
    $a.setAttribute("href", url);
    $a.setAttribute("download", downloadName);
    $a.click();
  }
  document.body.removeChild($a);
}

/**
 * Initialize ag-grid Grid
 * @param {HTMLElement} $grid Element to create Grid in
 * @param {Array<Object<string>>} headers
 * @param {Array<Object>} rows
 * @param {jQuery|HTMLElement} $dlButton
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
      $dlButton.attr("disabled", selectionLength > 0 ? null : "disabled");
      const badge = selectionLength
        ? `<span class="badge">${selectionLength}</span>`
        : "";
      $dlButton.html(`DOWNLOAD ${badge}`);
    }
  };
  const grid = new Grid($grid, gridOptions);
  gridOptions.api.sizeColumnsToFit();
  $dlButton.on("click", e => {
    e.preventDefault();
    downloadSelected(gridOptions.api);
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
    const aofId = x.aof_id;
    if (acc.hasOwnProperty(aofId)) {
      acc[aofId].push(x);
    } else {
      acc[aofId] = [x];
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
    (acc, x) => Object.assign(acc, { [x.workflow_id]: null }),
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
 */
$.get(AJAX_URL)
  .done(data => {
    const singleSampleOutputs = filterSingleSampleOutputs(data);
    const workflowIds = getWorkflowInfo(singleSampleOutputs);
    /**
     * ag-grid Grid header definitions
     * @type {*[]}
     */
    const HEADERS = [
      {
        field: "sample_name",
        headerName: "Sample",
        checkboxSelection: true,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        cellRenderer: p => {
          return `<a href="${BASE_URL}projects/${
            window.PAGE.projectId
          }/samples/${p.data.sample_id}" target="_blank">${
            p.data.sample_name
          }</a>`;
        }
      },
      {
        field: "file_path",
        headerName: "File",
        cellRenderer: p => {
          const REGEX = /^\d+\/\d+\/(.+)$/;
          const groups = REGEX.exec(p.data.file_path);
          if (groups === null) return p.data.file_path;
          const filename = groups[1];

          return `${filename} <small>(${p.data.analysis_output_file_key}, id=${
            p.data.aof_id
          })</small>`;
        }
      },
      {
        field: "analysis_type",
        headerName: "Analysis Type"
      },
      {
        field: "workflow_id",
        headerName: "Pipeline",
        cellRenderer: p => {
          const wfInfo = workflowIds[p.data.workflow_id];
          if (wfInfo === null) return p.data.workflow_id;
          return `${wfInfo.name} (v${wfInfo.version})`;
        }
      },
      {
        field: "submission_name",
        headerName: "Analysis Submission",
        cellRenderer: p =>
          `<a href="${BASE_URL}analysis/${
            p.data.submission_id
          }" target="_blank">${p.data.submission_name}</a>`
      },
      {
        field: "user_id",
        headerName: "Submitter",
        cellRenderer: p => `${p.data.user_first_name} ${p.data.user_last_name}`
      },
      {
        field: "created_date",
        headerName: "Created",
        cellRenderer: p => formatDate({ date: p.data.created_date })
      }
    ];
    const gridId = `grid-outputs`;
    const $grid = $(
      `<div id="${gridId}" class="ag-theme-balham" style="height: 600px; width: 100%; resize: both;"/>`
    );
    const $dlButton = $(
      `<button type="button" class="btn" disabled="disabled" >DOWNLOAD</button>`
    );
    $app.prepend($grid);
    $app.prepend($dlButton);

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
  .fail(function(xhr, error, exception) {
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
