import React, { Component } from "react";
import PropTypes from "prop-types";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import SampleNameRenderer from "./renderers/SampleNameRenderer";
import LoadingOverlay from "./LoadingOverlay";

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

const formatTemplateColumns = (cols, template) => {
  const columns = [...cols];
  const ordered = new Array(template.length);
  const remainder = [];

  // Always need to keep the sampleName first
  const name = columns.shift();

  columns.forEach(column => {
    const i = template.findIndex(item => column.id === item.id);
    if (-1 < i) {
      column.hide = false;
      ordered[i] = column;
    } else {
      column.hide = true;
      remainder.push(column);
    }
  });

  return [name, ...ordered, ...remainder];
};

/**
 * Format the column definitions.
 * @param {array} cols
 * @param {array} template
 * @returns {*}
 */
const formatColumns = (cols, template) => {
  const columns =
    template.length === 0 ? [...cols] : formatTemplateColumns(cols, template);

  return columns.map((column, i) => ({
    hide: template.length === 0 ? false : column.hide,
    field: column.label,
    headerName: column.label.toUpperCase(),
    ...(i === 0 ? sampleNameColumn : {})
  }));
};

/**
 * Format the row data.
 * Row should be {key: value}
 * @param {array} rows
 */
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

export class Table extends Component {
  frameworkComponents = { LoadingOverlay, SampleNameRenderer };
  constructor(props) {
    super(props);

    this.onGridReady = this.onGridReady.bind(this);
  }

  /*
  Allow access to the grids API
   */
  onGridReady(params) {
    this.api = params.api;
    this.columnApi = params.columnApi;
  }

  render() {
    const { fields, entries, template } = this.props;
    const containerStyle = {
      boxSizing: "border-box",
      height: 600,
      width: "100%"
    };
    return (
      <div style={containerStyle} className="ag-theme-balham">
        <AgGridReact
          localeText={localeText}
          enableSorting={true}
          columnDefs={formatColumns(fields, template)}
          rowData={formatRows(entries)}
          deltaRowDataMode={true}
          getRowNodeId={data => data.code}
          frameworkComponents={this.frameworkComponents}
          loadingOverlayComponent="LoadingOverlay"
          animateRows={true}
          onGridReady={this.onGridReady}
        />
      </div>
    );
  }
}

Table.propTypes = {
  fields: PropTypes.array.isRequired,
  entries: PropTypes.array
};
