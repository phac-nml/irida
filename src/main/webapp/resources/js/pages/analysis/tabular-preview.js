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
  let headers = [{ headerName: "#", field: "index" }];
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
 * @param {number} offset Index offset
 * @returns {Array<Object<string>>}
 */
function parseRowsTabDelimited(lines, offset = 0) {
  const rows = [];
  for (let i = 0; i < lines.length; i++) {
    const cells = lines[i].split("\t");
    const row = { index: offset + i + 1 };
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
 * @param {number} height Panel body height
 * @param {Object} aof AnalysisOutputFile info
 * @returns {{$panel: jQuery|HTMLElement, gridId: string, $status: jQuery|HTMLElement}}
 */
function createGridPanel(baseUrl, aof, height = 300) {
  const { id } = aof;
  const $panel = $(`<div id="js-panel-${id}" class="panel panel-default"/>`);
  const $panelHeading = $(panelHeading(baseUrl, aof));
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

function autoSizeAll({ columnApi }) {
  const allColumnIds = [];
  columnApi.getAllColumns().forEach(column => allColumnIds.push(column.colId));
  columnApi.autoSizeColumns(allColumnIds);
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
          const rows = parseRowsTabDelimited(lines, startRow);
          let last = -1;
          if (rows.length < PAGE_SIZE) {
            last = startRow + rows.length;
          }
          successCallback(rows, last);
          autoSizeAll(gridOptions);
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
    columnDefs: headers,
    rowModelType: "infinite",
    paginationPageSize: PAGE_SIZE,
    cacheOverflowSize: 1,
    maxConcurrentDatasourceRequests: 2,
    infiniteInitialRowCount: 1,
    maxBlocksInCache: undefined,
    onGridReady: () => gridOptions.api.setDatasource(dataSource)
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
 * @param {number} height Grid container height
 * @param {number} page_size Number of lines to fetch from server at a time
 * @param {Object} aof AnalysisOutputFile info
 */
export function renderTabularPreview(
  $container,
  baseUrl,
  aof,
  height = 300,
  page_size = 100
) {
  const { firstLine } = aof;
  const headers = parseHeadersTabDelimited(firstLine);
  const { $panel, gridId, $status } = createGridPanel(baseUrl, aof, height);
  $container.append($panel);
  const apiUrl = analysisOutputFileApiUrl(baseUrl, aof);
  initAgGrid(
    document.getElementById(gridId),
    $status,
    headers,
    apiUrl,
    page_size
  );
}
