import React from "react";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import { LoadingOverlay } from "./LoadingOverlay";
import { SampleNameRenderer } from "./renderers/SampleNameRenderer";

const localeText = window.PAGE.i18n.agGrid;

// Format the fields based on a template
const applyTemplate = (template, fields) => {
  if (template.length === 0) {
    return fields.map(f => {
      f.hide = false;
      return f;
    });
  }

  // Need to keep sample name first
  const sampleName = fields.shift();

  const final = [];
  template.forEach(t => {
    const index = fields.findIndex(f => t.label === f.field);
    if (index > -1) {
      const field = fields.splice(index, 1)[0];
      field.hide = false;
      final.push(field);
    }
  });
  return [sampleName, ...final];
};

export class Table extends React.Component {
  containerStyle = {
    boxSizing: "border-box",
    height: 600,
    width: "100%"
  };
  frameworkComponents = { LoadingOverlay, SampleNameRenderer };

  constructor(props) {
    super(props);
  }

  /*
  Allow access to the grids API
   */
  onGridReady = params => {
    this.api = params.api;
    this.columnApi = params.columnApi;
  };

  onColumnDropped = () => {
    // TODO: update UI to have modified template displayed with save btn.
    const colOrder = this.columnApi.getColumnState();
    console.log(colOrder);
    this.props.templateModified();
  };

  render() {
    return (
      <div style={this.containerStyle} className="ag-theme-balham">
        <AgGridReact
          enableSorting={true}
          localeText={localeText}
          columnDefs={applyTemplate(
            this.props.template.toJS(),
            this.props.fields.toJS()
          )}
          rowData={
            this.props.entries === null
              ? this.props.entries
              : this.props.entries.toJS()
          }
          deltaRowDataMode={true}
          getRowNodeId={data => data.code}
          frameworkComponents={this.frameworkComponents}
          loadingOverlayComponent="LoadingOverlay"
          animateRows={true}
          onGridReady={this.onGridReady}
          onDragStopped={this.onColumnDropped}
        />
      </div>
    );
  }
}

Table.propTypes = {
  fields: ImmutablePropTypes.list.isRequired,
  entries: ImmutablePropTypes.list
};
