import "../../vendor/datatables/datatables";
import React from "react";
import { Button } from "antd";
import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig,
  wrapCellContents,
} from "./../../utilities/datatables-utilities";
import { formatDate } from "./../../utilities/date-utilities";
import { SampleDetailViewer } from "../../components/samples/SampleDetailViewer";
import * as ReactDOM from "react-dom";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
const PROJECT_COLUMNS = generateColumnOrderInfo("#projects");
const SAMPLE_COLUMNS = generateColumnOrderInfo("#samples");

const projectConfig = Object.assign({}, tableConfig, {
  ajax: window.PAGE.urls.projects,
  searching: false,
  order: [[PROJECT_COLUMNS.MODIFIED_DATE, "desc"]],
  initComplete: function (settings, json) {
    $("#project-count").text(json.recordsTotal);
  },
  columnDefs: [
    {
      targets: [PROJECT_COLUMNS.NAME],
      render(data, type, full) {
        // Render the name as a link to the actual project.
        return createItemLink({
          url: `${window.PAGE.urls.project}${full.id}`,
          label: `${
            full.remote
              ? `<div aria-hidden="true" data-toggle="tooltip" data-placement="top" title="${i18n(
                  "projects.table.remoteSynchronized"
                )}">${data}&nbsp;<i style="color: #000;" class="fa fa-exchange pull-right"></i></div>`
              : data
          }`,
          width: "200px",
        });
      },
    },
    {
      targets: PROJECT_COLUMNS.ORGANISM,
      render(data) {
        return wrapCellContents({ text: data });
      },
    },
    // Format all dates to standate date for the systme.
    {
      targets: [PROJECT_COLUMNS.CREATED_DATE, PROJECT_COLUMNS.MODIFIED_DATE],
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      },
    },
  ],
});

const sampleConfig = Object.assign({}, tableConfig, {
  ajax: window.PAGE.urls.samples,
  searching: false,
  order: [[SAMPLE_COLUMNS.MODIFIED_DATE, "desc"]],
  initComplete: function (settings, json) {
    $("#sample-count").text(json.recordsTotal);
  },
  columnDefs: [
    {
      targets: [SAMPLE_COLUMNS.SAMPLE_NAME],
      createdCell: (td, cellData, rowData) => {
        ReactDOM.render(
          <SampleDetailViewer
            sampleId={rowData.id}
            projectId={rowData.projectId}
          >
            <Button type="link" style={{ padding: 0 }}>
              {cellData}
            </Button>
          </SampleDetailViewer>,
          td
        );
      },
    },
    {
      targets: SAMPLE_COLUMNS.ORGANISM,
      render(data) {
        return wrapCellContents({ text: data });
      },
    },
    {
      targets: [SAMPLE_COLUMNS.PROJECT_NAME],
      render(data, type, full) {
        // Render the name as a link to the actual project.
        return createItemLink({
          url: `${window.PAGE.urls.project}${full.projectId}`,
          label: data,
        });
      },
    },
    // Format all dates to standate date for the systme.
    {
      targets: [SAMPLE_COLUMNS.CREATED_DATE, SAMPLE_COLUMNS.MODIFIED_DATE],
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      },
    },
  ],
});

// init the datatables
$("#projects").DataTable(projectConfig);
$("#samples").DataTable(sampleConfig);

// update the page hash on tab click
$('a[data-toggle="tab"]').on("shown.bs.tab", function (e) {
  var hash = $(this).attr("href");
  window.location.hash = hash;
});

$(document).ready(function () {
  var hash = window.location.hash;

  if (hash === "#project-tab") {
    $('.nav-tabs a[href="#project-tab"]').tab("show");
  } else if (hash === "#sample-tab") {
    $('.nav-tabs a[href="#sample-tab"]').tab("show");
  }
});
