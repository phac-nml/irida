import React from "react";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";
import SampleNameRenderer from "./renderers/SampleNameRenderer";

import LoadingOverlay from "../../../../../modules/agGrid/LoadingOverlay";

const localeText = window.PAGE.i18n.agGrid;

/*
Special handler for formatting the sample Name Column;
 */
const sampleNameColumn = {
  sort: "asc",
  pinned: "left",
  lockPosition: true,
  cellRenderer: "SampleNameRenderer"
};

/**
 * Format the column definitions.
 * @param {array} cols
 * @returns {*}
 */
const formatColumns = cols =>
  cols.map((f, i) => ({
    field: f.label,
    headerName: f.label.toUpperCase(),
    ...(i === 0 ? sampleNameColumn : {})
  }));

const formatRows = rows => {
  if (rows !== null) {
    return rows.map(r => {
      const row = {};
      Object.keys(r).forEach(item => {
        row[item] = r[item].value;
      });
      return row;
    });
  }
};

export const Table = props => {
  const { fields, entries } = props;

  const containerStyle = {
    boxSizing: "border-box",
    height: 600,
    width: "100%"
  };

  const frameworkComponents = { LoadingOverlay, SampleNameRenderer };

  return (
    <div style={containerStyle} className="ag-theme-balham">
      <AgGridReact
        localeText={localeText}
        enableSorting={true}
        columnDefs={formatColumns(fields)}
        rowData={formatRows(entries)}
        deltaRowDataMode={true}
        getRowNodeId={data => data.code}
        frameworkComponents={frameworkComponents}
        loadingOverlayComponent="LoadingOverlay"
      />
    </div>
  );
};

Table.propTypes = {
  fields: PropTypes.array.isRequired,
  entries: PropTypes.array
};
