import React from "react";
import { connect } from "react-redux";
import isEqual from "lodash/isEqual";
import isArray from "lodash/isArray";
import PropTypes from "prop-types";
import { Icon } from "antd";
import { showUndoNotification } from "../../../../../modules/notifications";
import { AgGridReact } from "@ag-grid-community/react";
import { AllCommunityModules } from "@ag-grid-community/all-modules";
import "@ag-grid-community/all-modules/dist/styles/ag-grid.css";
import "@ag-grid-community/all-modules/dist/styles/ag-theme-material.css";
// Excel export support
import XLSX from "xlsx";

import { LoadingOverlay } from "./LoadingOverlay";
import {
  DateCellRenderer,
  IconCellRenderer,
  SampleNameRenderer
} from "./renderers";
import { FIELDS } from "../../constants";
import { actions as templateActions } from "../../reducers/templates";
import { actions as entryActions } from "../../reducers/entries";

const { i18n } = window.PAGE;

/**
 * React component to render the ag-grid to the page.
 */
export class TableComponent extends React.Component {
  state = {
    entries: null,
    filterCount: 0
  };

  /*
  Regular expression to clean the project and template names for export.
   */
  nameRegex = /([^\w]+)/gi;

  /*
  This is a flag for handling when a column is dragged and dropped on the table.
  This is required because the table can be modified externally through the columns
  panel.  If we don't flag this, it tries to update itself a second time throwing
  errors.
   */
  colDropped = false;

  /*
  External custom components used by ag-grid.
   */
  frameworkComponents = {
    LoadingOverlay,
    SampleNameRenderer,
    IconCellRenderer,
    DateCellRenderer
  };

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (prevProps.globalFilter !== this.props.globalFilter) {
      this.api.setQuickFilter(this.props.globalFilter);
    }
  }

  shouldComponentUpdate(nextProps) {
    if (nextProps.globalFilter !== this.props.globalFilter) return true;
    /**
     * Check to see if the height of the table needs to be updated.
     * This will only happen  on initial load or if the window height has changed
     */
    if (nextProps.height !== this.props.height) {
      return true;
    }

    if (!isEqual(nextProps.fields, this.props.fields)) {
      /*
      This should only happen on the original loading of the table when the
      complete list of UIMetadataTemplateFields are passed.
       */
      return true;
    }

    if (
      isArray(nextProps.entries) &&
      !isEqual(nextProps.entries, this.props.entries)
    ) {
      /*
      This should only happen on the original loading of the table when the
      complete list of MetadataEntries are passed.
       */
      return true;
    }

    if (nextProps.current !== this.props.current) {
      /*
      The current template has changed.
      Force the table to update to the new view based on the template fields.
       */
      const template = nextProps.templates[nextProps.current];
      this.applyTemplate(template.fields);
      return false;
    }

    /*
    The field order for a template can change externally.  Check to see if that
    order has been updated, and adjust the columns accordingly.
     */
    const oldModified = this.props.templates[this.props.current].modified;
    const newModified = nextProps.templates[nextProps.current].modified;

    if (isEqual(oldModified, newModified)) {
      if (this.colDropped) {
        // Clear the dropped flag as the next update might come from an external source
        this.colDropped = false;
      } else {
        /*
        If the length of the modified fields === 0, then the modified template
        was saved ==> the table already reflected this state.  If not the
        template was modified from an external event and therefore needs to
        reflect the changes.
         */
        if (newModified.length > 0) {
          this.applyTemplate(newModified);
        }
      }
      return false;
    }

    // If these have not been changed then don't update the entries;
    return false;
  }

  /**
   * Apply a Metadata template to the table.  This will reorder the columns and
   * toggle column visibility.
   * @param {Array} templateFields
   */
  applyTemplate = templateFields => {
    /*
    Get the current state of the table.  This hold critical information so we
    only want to modify the order and visible of the columns.
     */
    const columnState = this.columnApi.getColumnState();

    // Keep the icons
    const defaults = columnState.splice(0, 2);

    /*
   From the new template (or modified template) determine the order and
   visibility of the columns.  All templates know all the available fields
   in the table, if there are not visible they are just added to the end of
   the template.
    */
    const final = templateFields
      .map(field => {
        const index = columnState.findIndex(c => c.colId === field.field);
        if (index > -1) {
          const col = columnState.splice(index, 1)[0];

          /*
          Determine the visibility of the column based on the template field.
          */
          col.hide = field.hide;
          return col;
        }
      })
      .filter(c => typeof c !== "undefined");

    /*
    Combine back the sample name plus the new ordered state for the table.
     */
    this.columnApi.setColumnState([...defaults, ...final]);
  };

  /*
  Allow access to the grids API
   */
  onGridReady = params => {
    this.api = params.api;
    this.columnApi = params.columnApi;
    /*
    Resize the icons since no extra space is needed.
     */
    this.columnApi.autoSizeColumns([FIELDS.icons]);
  };

  /**
   * Event handler for if a user drags a column to a new spot on the current
   * table.
   */
  onColumnDropped = () => {
    const colOrder = [...this.columnApi.getColumnState()];
    // Remove sample name
    colOrder.shift();

    /*
    Remove the hidden ones and just get the field identifiers
     */
    let list = colOrder.map(c => {
      // Get the header name
      const field = this.props.fields.find(f => f.field === c.colId);
      field.hide = c.hide;
      return { ...field };
    });

    // Don't let the table perform a modified update since it handles it on its own
    this.colDropped = true;
    this.props.tableModified(list);
  };

  /**
   * Generate the name for the type of file to be exported from the grid
   * @param {string} ext extension for the file.
   * @returns {string}
   */
  generateFileName = ext => {
    // YYYY-MM-dd-project-X-<metadata template name>.csv
    const fullDate = new Date();
    const date = `${fullDate.getFullYear()}-${fullDate.getMonth() +
      1}-${fullDate.getDate()}`;
    const project = window.PAGE.project.label.replace(this.nameRegex, "_");
    const template = this.props.templates[this.props.current].name.replace(
      this.nameRegex,
      "_"
    );
    return `${date}-${project}-${template}.${ext}`;
  };

  createFile = ext => {
    const colOrder = this.columnApi.getColumnState().filter(c => !c.hide);

    /*
    Set up the excel file
     */
    const fileName = this.generateFileName(ext);
    const workbook = {};
    workbook.Sheets = {};
    workbook.Props = {};
    workbook.SSF = {};
    workbook.SheetNames = [];
    /* create worksheet: */
    const ws = {};

    /* the range object is used to keep track of the range of the sheet */
    const range = { s: { c: 0, r: 0 }, e: { c: 0, r: 0 } };

    /*
    Add the headers
     */
    const cell = { v: "Sample Id", t: "s" };
    const cell_ref = XLSX.utils.encode_cell({ c: 0, r: 0 });
    ws[cell_ref] = cell;
    colOrder.forEach((col, i) => {
      const index = i + 1;
      const column = this.columnApi.getColumn(col.colId);
      const name = this.columnApi.getDisplayNameForColumn(column);
      if (range.e.c < index) range.e.c = index;
      const cell = { v: name, t: "s" };
      const cell_ref = XLSX.utils.encode_cell({ c: index, r: 0 });
      ws[cell_ref] = cell;
    });

    /*
    Add all the entries
     */
    this.api.forEachNodeAfterFilterAndSort((node, r) => {
      const entry = node.data;
      /*
      Offset to allow for the header row.
       */
      const row = r + 1;
      if (range.e.r < row) range.e.r = row;

      // Need to add the sample identifier
      const idCell = { v: entry[FIELDS.sampleId], t: "n", z: "0" };
      const idRef = XLSX.utils.encode_cell({ c: 0, r: row });
      ws[idRef] = idCell;

      for (let c = 0; c < colOrder.length; c++) {
        const column = colOrder[c];
        /*
        Offset to allow for the sample id column
         */
        const col = c + 1;
        /* create cell object: .v is the actual data */
        const cell = { v: entry[column.colId] };
        if (cell.v !== null) {
          /* create the correct cell reference */
          const cell_ref = XLSX.utils.encode_cell({ c: col, r: row });

          /* determine the cell type */
          if (typeof cell.v === "number") cell.t = "n";
          else if (typeof cell.v === "boolean") cell.t = "b";
          else cell.t = "s";

          /* add to structure */
          ws[cell_ref] = cell;
        }
      }
    });

    ws["!ref"] = XLSX.utils.encode_range(range);

    /* add worksheet to workbook using the template name */
    const template = this.props.templates[this.props.current].name.replace(
      this.nameRegex,
      "_"
    );
    workbook.SheetNames.push(template);
    workbook.Sheets[template] = ws;

    /* write file */
    XLSX.writeFile(workbook, fileName);
  };

  addSamplesToCart = () => {
    const nodes = this.api.getSelectedNodes().map(n => n.data);
    this.props.addSelectedToCart(nodes);
  };

  /**
   * Export the currently visible columns as a CSV file.
   */
  exportCSV = () => {
    this.createFile("csv");
  };

  /**
   * Export the currently visible columns as an XLSX file.
   */
  exportXLSX = () => {
    this.createFile("xlsx");
  };

  onSelectionChange = () => {
    this.props.selection(this.api.getSelectedNodes().map(n => n.data));
  };

  /**
   * When a cell is edited, store the value in case it needs to be reversed
   * @param {object} event - the cell edit event
   */
  onCellEditingStarted = event => {
    this.cellEditedValue = event.value || "";
  };

  /**
   * After the cell has been edited give the user a chance to undo the edit.
   * @param {object} event - cell edit event
   */
  onCellEditingStopped = event => {
    // Get the table header for the cell that was edited

    const { field, headerName } = event.column.colDef;
    // Get the previous value
    const previousValue = this.cellEditedValue;
    // Get the new value for the cell
    const data = event.data;

    // Make sure that the data for saving is valid.
    if (typeof event.value !== "undefined" && previousValue !== event.value) {
      /*
      Update the value on the server (this way, if the user closes the page the
      server already has the update.
       */
      this.props.entryEdited(data, field, headerName);
      /*
      Show a notification that allows the user to reverse the change to the value.
       */
      const text = Boolean(data[field])
        ? i18n.linelist.editing.undo.full
        : i18n.linelist.editing.undo.empty;
      showUndoNotification(
        {
          text: text
            .replace("[SAMPLE_NAME]", data[FIELDS.sampleName])
            .replace("[FIELD]", headerName)
            .replace("[NEW_VALUE]", data[field])
        },
        () => {
          /**
           * Callback to reverse the change.
           */
          data[field] = previousValue;
          this.props.entryEdited(data, field, headerName);
          event.node.setDataValue(field, previousValue);
        }
      );
    }
    // Remove the stored value for the cell
    delete this.cellEditedValue;
  };

  /**
   * Update parent components of the revised filter status.
   * @returns {*}
   */
  setFilterCount = () =>
    this.props.onFilter(
      this.api.getModel().rootNode.childrenAfterFilter.length
    );

  /**
   * Scroll table to the top left most position.
   */
  scrollToTop = () => {
    // Scroll to top
    this.api.ensureIndexVisible(0);
    // Ensure the column is scrolled all the way to the left.
    this.api.ensureColumnVisible(this.columnApi.getColumnState()[1].colId);
  };

  render() {
    console.log(this.props.fields);
    return (
      <div
        className="ag-grid-table-wrapper"
        style={{ height: this.props.height }}
      >
        <AgGridReact
          id="linelist-grid"
          rowSelection="multiple"
          modules={AllCommunityModules}
          // onFilterChanged={this.setFilterCount}
          // localeText={i18n.linelist.agGrid}
          columnDefs={this.props.fields}
          rowData={this.props.entries}
          frameworkComponents={this.frameworkComponents}
          // loadingOverlayComponent="LoadingOverlay"
          onGridReady={this.onGridReady}
          // onDragStopped={this.onColumnDropped}
          rowDeselection={true}
          // suppressRowClickSelection={true}
          onSelectionChanged={this.onSelectionChange}
          defaultColDef={{
            headerCheckboxSelectionFilteredOnly: true,
            sortable: true,
            filter: true
          }}
          icons={{
            menu: '<i aria-label="icon: menu" class="anticon anticon-menu"><svg viewBox="64 64 896 896" focusable="false" class="" data-icon="menu" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M904 160H120c-4.4 0-8 3.6-8 8v64c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-64c0-4.4-3.6-8-8-8zm0 624H120c-4.4 0-8 3.6-8 8v64c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-64c0-4.4-3.6-8-8-8zm0-312H120c-4.4 0-8 3.6-8 8v64c0 4.4 3.6 8 8 8h784c4.4 0 8-3.6 8-8v-64c0-4.4-3.6-8-8-8z"></path></svg></i>',
            filter: '<i aria-label="icon: filter" class="anticon anticon-filter"><svg viewBox="64 64 896 896" focusable="false" class="" data-icon="filter" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M880.1 154H143.9c-24.5 0-39.8 26.7-27.5 48L349 597.4V838c0 17.7 14.2 32 31.8 32h262.4c17.6 0 31.8-14.3 31.8-32V597.4L907.7 202c12.2-21.3-3.1-48-27.6-48zM603.4 798H420.6V642h182.9v156zm9.6-236.6l-9.5 16.6h-183l-9.5-16.6L212.7 226h598.6L613 561.4z"></path></svg></i>',
            sortAscending: '<i aria-label="icon: sort-ascending" class="anticon anticon-sort-ascending"><svg viewBox="64 64 896 896" focusable="false" class="" data-icon="sort-ascending" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M839.6 433.8L749 150.5a9.24 9.24 0 0 0-8.9-6.5h-77.4c-4.1 0-7.6 2.6-8.9 6.5l-91.3 283.3c-.3.9-.5 1.9-.5 2.9 0 5.1 4.2 9.3 9.3 9.3h56.4c4.2 0 7.8-2.8 9-6.8l17.5-61.6h89l17.3 61.5c1.1 4 4.8 6.8 9 6.8h61.2c1 0 1.9-.1 2.8-.4 2.4-.8 4.3-2.4 5.5-4.6 1.1-2.2 1.3-4.7.6-7.1zM663.3 325.5l32.8-116.9h6.3l32.1 116.9h-71.2zm143.5 492.9H677.2v-.4l132.6-188.9c1.1-1.6 1.7-3.4 1.7-5.4v-36.4c0-5.1-4.2-9.3-9.3-9.3h-204c-5.1 0-9.3 4.2-9.3 9.3v43c0 5.1 4.2 9.3 9.3 9.3h122.6v.4L587.7 828.9a9.35 9.35 0 0 0-1.7 5.4v36.4c0 5.1 4.2 9.3 9.3 9.3h211.4c5.1 0 9.3-4.2 9.3-9.3v-43a9.2 9.2 0 0 0-9.2-9.3zM416 702h-76V172c0-4.4-3.6-8-8-8h-56c-4.4 0-8 3.6-8 8v530h-76c-6.7 0-10.5 7.8-6.3 13l112 141.9a8 8 0 0 0 12.6 0l112-141.9c4.1-5.2.4-13-6.3-13z"></path></svg></i>',
            sortDescending: '<i aria-label="icon: sort-descending" class="anticon anticon-sort-descending"><svg viewBox="64 64 896 896" focusable="false" class="" data-icon="sort-descending" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M839.6 433.8L749 150.5a9.24 9.24 0 0 0-8.9-6.5h-77.4c-4.1 0-7.6 2.6-8.9 6.5l-91.3 283.3c-.3.9-.5 1.9-.5 2.9 0 5.1 4.2 9.3 9.3 9.3h56.4c4.2 0 7.8-2.8 9-6.8l17.5-61.6h89l17.3 61.5c1.1 4 4.8 6.8 9 6.8h61.2c1 0 1.9-.1 2.8-.4 2.4-.8 4.3-2.4 5.5-4.6 1.1-2.2 1.3-4.7.6-7.1zM663.3 325.5l32.8-116.9h6.3l32.1 116.9h-71.2zm143.5 492.9H677.2v-.4l132.6-188.9c1.1-1.6 1.7-3.4 1.7-5.4v-36.4c0-5.1-4.2-9.3-9.3-9.3h-204c-5.1 0-9.3 4.2-9.3 9.3v43c0 5.1 4.2 9.3 9.3 9.3h122.6v.4L587.7 828.9a9.35 9.35 0 0 0-1.7 5.4v36.4c0 5.1 4.2 9.3 9.3 9.3h211.4c5.1 0 9.3-4.2 9.3-9.3v-43a9.2 9.2 0 0 0-9.2-9.3zM310.3 167.1a8 8 0 0 0-12.6 0L185.7 309c-4.2 5.3-.4 13 6.3 13h76v530c0 4.4 3.6 8 8 8h56c4.4 0 8-3.6 8-8V322h76c6.7 0 10.5-7.8 6.3-13l-112-141.9z"></path></svg></i>',
            columnMovePin: '<i class="far fa-hand-rock"/>',
            columnMoveAdd: '<i class="fa fa-plus-square"/>',
            columnMoveHide: '<i class="fa fa-times"/>',
            columnMoveMove: '<i class="fa fa-link"/>',
            columnMoveLeft: '<i class="fa fa-arrow-left"/>',
            columnMoveRight: '<i class="fa fa-arrow-right"/>',
            checkboxChecked: '<i aria-label="icon: check-square" class="anticon anticon-check-square"><svg viewBox="64 64 896 896" focusable="false" class="" data-icon="check-square" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M433.1 657.7a31.8 31.8 0 0 0 51.7 0l210.6-292c3.8-5.3 0-12.7-6.5-12.7H642c-10.2 0-19.9 4.9-25.9 13.3L459 584.3l-71.2-98.8c-6-8.3-15.6-13.3-25.9-13.3H315c-6.5 0-10.3 7.4-6.5 12.7l124.6 172.8z"></path><path d="M880 112H144c-17.7 0-32 14.3-32 32v736c0 17.7 14.3 32 32 32h736c17.7 0 32-14.3 32-32V144c0-17.7-14.3-32-32-32zm-40 728H184V184h656v656z"></path></svg></i>',
            checkboxUnchecked: '<i aria-label="icon: border" class="anticon anticon-border"><svg viewBox="64 64 896 896" focusable="false" class="" data-icon="border" width="1em" height="1em" fill="currentColor" aria-hidden="true"><path d="M880 112H144c-17.7 0-32 14.3-32 32v736c0 17.7 14.3 32 32 32h736c17.7 0 32-14.3 32-32V144c0-17.7-14.3-32-32-32zm-40 728H184V184h656v656z"></path></svg></i>',
            checkboxIndeterminate: '<i class="far fa-circle"/>'
          }}
          // enableCellChangeFlash={true}
          // onCellEditingStarted={this.onCellEditingStarted}
          // onCellEditingStopped={this.onCellEditingStopped}
        />
      </div>
    );
  }
}

TableComponent.propTypes = {
  height: PropTypes.number.isRequired,
  tableModified: PropTypes.func.isRequired,
  fields: PropTypes.array.isRequired,
  entries: PropTypes.array,
  templates: PropTypes.array,
  current: PropTypes.number.isRequired,
  onFilter: PropTypes.func.isRequired,
  globalFilter: PropTypes.string.isRequired,
  selection: PropTypes.func.isRequired
};

const mapStateToProps = state => ({
  fields: state.fields.fields,
  templates: state.templates.templates,
  current: state.templates.current,
  entries: state.entries.entries,
  globalFilter: state.entries.globalFilter
});

const mapDispatchToProps = dispatch => ({
  tableModified: fields => dispatch(templateActions.tableModified(fields)),
  entryEdited: (entry, field, label) =>
    dispatch(entryActions.edited(entry, field, label)),
  selection: selected => dispatch(entryActions.selection(selected))
});

export const Table = connect(
  mapStateToProps,
  mapDispatchToProps,
  null,
  { forwardRef: true }
)(TableComponent);
