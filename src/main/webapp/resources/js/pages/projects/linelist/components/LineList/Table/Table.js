import React from "react";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import { LoadingOverlay } from "./LoadingOverlay";
import { SampleNameRenderer } from "./renderers/SampleNameRenderer";

const localeText = window.PAGE.i18n.agGrid;

export class Table extends React.Component {
  containerStyle = {
    boxSizing: "border-box",
    height: 600,
    width: "100%"
  };
  frameworkComponents = { LoadingOverlay, SampleNameRenderer };

  constructor(props) {
    super(props);

    const { fields, entries } = props;
    this.state = {
      fields,
      entries
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
    /*
    Since entries is an immutable data source, this will only do a quick check!
    Therefore should be fast.
     */
    if (!nextProps.entries.equals(this.state.entries)) {
      this.setState({ entries: nextProps.entries });
    }
  }

  render() {
    return (
      <div style={this.containerStyle} className="ag-theme-balham">
        <AgGridReact
          enableSorting={true}
          localeText={localeText}
          columnDefs={this.state.fields.toJS()}
          rowData={this.state.entries.toJS()}
          deltaRowDataMode={true}
          getRowNodeId={data => data.code}
          frameworkComponents={this.frameworkComponents}
          loadingOverlayComponent="LoadingOverlay"
          onGridReady={this.onGridReady}
        />
      </div>
    );
  }
}

Table.propTypes = {
  fields: ImmutablePropTypes.list.isRequired,
  entries: ImmutablePropTypes.list
};
