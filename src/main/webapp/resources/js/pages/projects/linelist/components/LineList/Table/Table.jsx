import React from "react";
import { List } from "immutable";
import PropTypes from "prop-types";
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
    if (nextProps.current !== this.props.current) {
      const template = nextProps.templates.get(nextProps.current).toJS();
      this.applyTemplate(template.fields);
    }
    // If these have not been changed then don't update the entries;
    return false;
  }

  /**
   * Apply a Metadata template to the table.  This will reorder the columns and
   * toggle column visibility.
   * @param fields
   */
  applyTemplate = fields => {
    const columnState = this.columnApi.getColumnState();

    // If there are no fields, then there is no template :)
    // Therefore just show all the fields.
    if (fields.length === 0) {
      const state = columnState.map(c => {
        c.hide = false;
        return c;
      });
      this.columnApi.setColumnState(state);
      return;
    }

    // Need to keep sample name first since it is not actually metadata!
    let final = [columnState.shift()];
    fields.forEach(t => {
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
  };

  /**
   * Event handler for if a user drags a column to a new spot on the current
   * table.
   */
  onColumnDropped = () => {
    const colOrder = this.columnApi.getColumnState();
    // Remove the hidden ones and just get the field identifiers
    const list = colOrder.filter(c => !c.hide).map(c => ({ label: c.colId }));
    this.props.templateModified(list);
  };

  render() {
    return (
      <div style={this.containerStyle} className="ag-theme-balham table-wrapper">
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
          onDragStopped={this.onColumnDropped}
        />
      </div>
    );
  }
}

Table.propTypes = {
  fields: ImmutablePropTypes.list.isRequired,
  entries: ImmutablePropTypes.list,
  templates: ImmutablePropTypes.list,
  current: PropTypes.number.isRequired
};
