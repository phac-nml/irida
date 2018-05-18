import React from "react";
import { List } from "immutable";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AgGridReact } from "ag-grid-react";
import "ag-grid/dist/styles/ag-grid.css";
import "ag-grid/dist/styles/ag-theme-balham.css";

import { LoadingOverlay } from "./LoadingOverlay";
import { SampleNameRenderer } from "./renderers/SampleNameRenderer";
import { fields } from "../../../reducers";

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
    const sample = columnState.shift();
    const final = fields.map(field => {
      const index = columnState.findIndex(c => {
        return c.colId === field.label;
      });
      const col = columnState.splice(index, 1)[0];
      col.hide = field.hide;
      return col;
    });
    this.columnApi.setColumnState([sample, ...final]);
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
    const list = colOrder.map(c => ({ label: c.colId, hide: c.hide }));
    this.props.tableModified(list);
  };

  render() {
    return (
      <div style={this.containerStyle}>
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
  tableModified: PropTypes.func.isRequired,
  fields: ImmutablePropTypes.list.isRequired,
  entries: ImmutablePropTypes.list,
  templates: ImmutablePropTypes.list,
  current: PropTypes.number.isRequired
};
