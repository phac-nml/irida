import React from "react";
import { List } from "immutable";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import { LoadingOverlay } from "./LoadingOverlay";
import { SampleNameRenderer } from "./renderers/SampleNameRenderer";

const { i18n } = window.PAGE;

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

  shouldComponentUpdate(nextProps) {
    if (!nextProps.fields.equals(this.props.fields)) {
      return true;
    }
    if (
      List.isList(nextProps.entries) &&
      !nextProps.entries.equals(this.props.entries)
    ) {
      return true;
    }
    if (!nextProps.template.equals(this.props.template)) {
      // Update the table nothing else
      this.applyTemplate(nextProps.template);
    }
    // If these have not been changed then don't update the entries;
    return false;
  }

  applyTemplate = templateList => {
    const template = templateList.toJS();
    const columnState = this.columnApi.getColumnState();

    // If there are no fields, then there is no template :)
    // Therefore just show all the fields.
    if (template.length === 0) {
      const state = columnState.map(c => {
        c.hide = false;
        return c;
      });
      this.columnApi.setColumnState(state);
      return;
    }

    // Need to keep sample name first
    let final = [columnState.shift()];
    template.forEach(t => {
      const index = columnState.findIndex(f => t.label === f.colId);
      if (index > -1) {
        const field = columnState.splice(index, 1)[0];
        field.hide = false;
        final.push(field);
      }
    });
    const remainder = columnState.map(c => {
      c.hide = true;
      return c;
    });
    this.columnApi.setColumnState([...final, ...remainder]);
  };

  /*
  Allow access to the grids API
   */
  onGridReady = params => {
    this.api = params.api;
    this.columnApi = params.columnApi;

    this.api.sortChanged;
  };

  render() {
    return (
      <div style={this.containerStyle} className="ag-theme-balham">
        <AgGridReact
          enableSorting={true}
          enableColResize={true}
          localeText={i18n.linelist.agGrid}
          columnDefs={this.props.fields.toJS()}
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
        />
      </div>
    );
  }
}

Table.propTypes = {
  fields: ImmutablePropTypes.list.isRequired,
  entries: ImmutablePropTypes.list
};
