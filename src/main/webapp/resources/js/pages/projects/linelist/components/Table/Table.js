import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import { LoadingOverlay } from "./LoadingOverlay";
import { SampleNameRenderer } from "./renderers/SampleNameRenderer";

const localeText = window.PAGE.i18n.agGrid;

export class TableComponent extends React.Component {
  frameworkComponents = { LoadingOverlay, SampleNameRenderer };

  constructor(props) {
    super(props);

    this.state = {
      entries: props.entries,
      fields: props.fields
    };
  }

  /*
  Allow access to the grids API
   */
  onGridReady = params => {
    this.api = params.api;
    this.columnApi = params.columnApi;
  };

  componentWillReceiveProps(nextProps) {
    if (nextProps.entries !== null) {
      this.setState({ entries: nextProps.entries });
    }

    if (nextProps.fields) {
      this.setState({ fields: nextProps.fields });
    }
  }

  render() {
    const containerStyle = {
      boxSizing: "border-box",
      height: 600,
      width: "100%"
    };

    return (
      <div style={containerStyle} className="ag-theme-balham">
        <AgGridReact
          enableSorting={true}
          localeText={localeText}
          columnDefs={this.state.fields}
          rowData={this.state.entries}
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

TableComponent.propTypes = {
  fields: PropTypes.array.isRequired,
  entries: PropTypes.array
};

const mapStateToProps = state => ({
  fields: state.fields.fields,
  entries: state.entries.entries
});
const mapDispatchToProps = dispatch => ({});

export const Table = connect(mapStateToProps, mapDispatchToProps)(
  TableComponent
);
