import $ from "jquery";
import { Grid } from "ag-grid/main";
import { analysisOutputFileApiUrl, panelHeading } from "./preview.utils";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

/**
 * Get basic ag-grid column definitions from a tab-delimited string
 * @param {string} firstLine Tab-delimited first line of an AnalysisOutputFile
 * @returns {Array<Object<string>>} Basic ag-grid column definitions
 */
function parseHeadersTabDelimited(firstLine) {
  let headers = [];
  const firstRow = firstLine.split("\t");
  for (let i = 0; i < firstRow.length; i++) {
    const row = firstRow[i];
    headers.push({
      headerName: row,
      field: i + ""
    });
  }
  return headers;
}

/**
 * Split each line on tab characters.
 * @param {Array<string>} lines Tab-delimited lines
 * @returns {Array<Object<string>>}
 */
function parseRowsTabDelimited(lines) {
  const rows = [];
  for (let i = 0; i < lines.length; i++) {
    const cells = lines[i].split("\t");
    const row = {};
    for (let j = 0; j < cells.length; j++) {
      row[j + ""] = cells[j];
    }
    rows.push(row);
  }
  return rows;
}

/**
 * Create Bootstrap Panel for ag-grid Grid.
 * @param {string} baseUrl Base AJAX URL
 * @param {number} analysisSubmissionId AnalysisSubmission id
 * @param {number} id AnalysisOutputFile id
 * @param {string} outputName AnalysisOutputFile output name
 * @param {string} filename AnalysisOutputFile filename
 * @param {number} height Panel body height
 * @returns {{$panel: jQuery|HTMLElement, gridId: string, $status: jQuery|HTMLElement}}
 */
function createGridPanel(
  baseUrl,
  analysisSubmissionId,
  id,
  outputName,
  filename,
  height = 300
) {
  const $panel = $(`<div id="js-panel-${id}" class="panel panel-default"/>`);
  const $panelHeading = $(
    panelHeading(baseUrl, analysisSubmissionId, id, outputName, filename)
  );
  const $panelBody = $(`<div class="panel-body"></div>`);
  const gridId = `grid-${id}`;
  const $grid = $(`<div/>`, {
    id: gridId,
    class: "display ag-theme-balham",
    height: "100%",
    width: "100%"
  });
  const $status = $(`<p class="small pull-right"/>`);
  const $div = $(`<div/>`);
  $div.css({
    height: `${height}px`,
    resize: "vertical",
    overflow: "hidden",
    "padding-bottom": "20px"
  });
  $div.append($grid);
  $div.append($status);
  $panelBody.append($div);
  $panel.append($panelHeading);
  $panel.append($panelBody);
  return { $panel, gridId, $status };
}

/**
 * Initialize ag-grid Grid
 * @param {HTMLElement} $grid Element to create Grid in
 * @param {jQuery|HTMLElement} $status Status
 * @param {Array<Object<string>>} headers
 * @param {string} baseUrl
 * @param {number} PAGE_SIZE
 */
function initAgGrid($grid, $status, headers, baseUrl, PAGE_SIZE = 100) {
  const dataSource = {
    rowCount: null,
    getRows: function({ startRow, endRow, successCallback }) {
      const params = {
        start: startRow,
        end: endRow
      };
      const url = `${baseUrl}?${$.param(params)}`;
      $.ajax({
        url: url,
        success: function onSuccess({ lines }) {
          const rows = parseRowsTabDelimited(lines);
          let last = -1;
          if (rows.length < PAGE_SIZE) {
            last = startRow + rows.length;
          }
          successCallback(rows, last);
        },
        error: function() {
          console.warn("error loading lines " + startRow + " to " + endRow);
        }
      });
    }
  };

  const gridOptions = {
    enableColResize: true,
    rowBuffer: 0,
    debug: true,
    columnDefs: headers,
    rowModelType: "infinite",
    paginationPageSize: PAGE_SIZE,
    cacheOverflowSize: 1,
    maxConcurrentDatasourceRequests: 2,
    infiniteInitialRowCount: 1,
    maxBlocksInCache: undefined,
    onGridReady: () => {
      gridOptions.api.setDatasource(dataSource);
    },
    onViewportChanged: ({ type, firstRow, lastRow, api, columnApi }) => {
      if (api.rowModel.infiniteCache) {
        //TODO: i18n
        const statusText = `Showing ${firstRow + 1}-${lastRow + 1} of ${
          api.rowModel.infiniteCache.virtualRowCount
        } rows`;
        $status.text(statusText);
      }
    }
  };

  new Grid($grid, gridOptions);
}

/**
 * Render a preview of a tabular analysis output file in a ag-grid Grid.
 *
 * Only `page_size` number of lines are initially loaded with more lines fetched
 * from the server if there are more lines to fetch when the user scrolls down
 * in the Grid.
 *
 * @param {jQuery|HTMLElement} $container Container element
 * @param {string} baseUrl Base analysis AJAX url (e.g. /analysis/ajax/)
 * @param {number} analysisSubmissionId AnalysisSubmission id
 * @param {string} outputName Workflow output file name
 * @param {number} id AnalysisOutputFile id
 * @param {string} filename AnalysisOutputFile filename
 * @param {number} fileSizeBytes AnalysisOutputFile file size in bytes
 * @param {string} firstLine AnalysisOutputFile first line
 * @param {number} height Grid container height
 * @param {number} page_size Number of lines to fetch from server at a time
 */
export function renderTabularPreview(
  $container,
  baseUrl,
  analysisSubmissionId,
  { outputName, id, filename, fileSizeBytes, firstLine },
  height = 300,
  page_size = 100
) {
  let headers = parseHeadersTabDelimited(firstLine);
  const { $panel, gridId, $status } = createGridPanel(
    baseUrl,
    analysisSubmissionId,
    id,
    outputName,
    filename,
    height
  );
  $container.append($panel);
  const apiUrl = analysisOutputFileApiUrl(baseUrl, analysisSubmissionId, id);
  initAgGrid(
    document.getElementById(gridId),
    $status,
    headers,
    apiUrl,
    page_size
  );
}
