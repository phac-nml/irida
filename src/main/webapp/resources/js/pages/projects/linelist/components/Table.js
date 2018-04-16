import React from "react";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import LoadingOverlay from "../../../../modules/agGrid/LoadingOverlay";

const localeText = window.PAGE.i18n.agGrid;

class SampleCellRenderer {
  constructor(props) {
    this.props = props;
  }
}

const formatColumns = cols =>
  cols.map((f, i) => {
    const column = {
      field: f.label,
      headerName: f.label.toUpperCase()
    };

    // Special handling for the sample name
    if (i === 0) {
      column.sort = "asc";
      column.pinned = "left";
      column.lockPosition = true;
      column.cellRenderer = params =>
        `<a href="${window.TL.BASE_URL}/samples/${Number(
          params.data.sampleId
        )}">${params.value}</a>`;
    }

    return column;
  });

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

  const frameworkComponents = { LoadingOverlay };

  return (
    <div style={containerStyle} className="ag-theme-balham">
      <AgGridReact
        localeText={localeText}
        enableSorting={true}
        columnDefs={formatColumns(fields)}
        rowData={formatRows(entries)}
        // deltaRowDataMode={true}
        // getRowNodeId={data => data.id}
        frameworkComponents={frameworkComponents}
        loadingOverlayComponent="LoadingOverlay"
        // onGridReady={params => params.api.sizeColumnsToFit()}
      />
    </div>
  );
};

Table.propTypes = {
  fields: PropTypes.array.isRequired,
  entries: PropTypes.array
};
