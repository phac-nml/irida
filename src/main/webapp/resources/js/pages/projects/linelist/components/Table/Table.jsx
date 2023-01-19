import React from "react";
import { connect } from "react-redux";
import { Button, notification, Spin } from "antd";
import isEqual from "lodash/isEqual";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-material.css";
import { CsvExportModule } from "@ag-grid-community/csv-export";
import LoadingOverlay from "./LoadingOverlay";
import {
  DateCellRenderer,
  IconCellRenderer,
  SampleNameRenderer,
} from "./renderers";
import { FIELDS } from "../../constants";
import { actions as templateActions } from "../../reducers/templates";
import { actions as entryActions } from "../../reducers/entries";
import { IconCheck } from "../../../../../components/icons/Icons";
import { green6 } from "../../../../../styles/colors";

/**
 * React component to render the ag-grid to the page.
 */
export class TableComponent extends React.Component {
  state = {
    entries: null,
    filterCount: 0,
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
  components = {
    CsvExportModule,
    LoadingOverlay,
    SampleNameRenderer,
    IconCellRenderer,
    DateCellRenderer,
  };

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (prevProps.globalFilter !== this.props.globalFilter) {
      this.api.setQuickFilter(this.props.globalFilter);
    }
  }

  shouldComponentUpdate(nextProps) {
    if (nextProps.globalFilter !== this.props.globalFilter) return true;

    if (!isEqual(nextProps.fields, this.props.fields)) {
      /*
      This should only happen on the original loading of the table when the
      complete list of UIMetadataTemplateFields are passed.
       */
      return true;
    }

    if (
      Array.isArray(nextProps.entries) &&
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
  applyTemplate = (templateFields) => {
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
      .map((field) => {
        const index = columnState.findIndex((c) => c.colId === field.field);
        if (index > -1) {
          const col = columnState.splice(index, 1)[0];

          /*
          Determine the visibility of the column based on the template field.
          */
          col.hide = field.hide;
          return col;
        }
      })
      .filter((c) => typeof c !== "undefined");

    /*
    Combine back the sample name plus the new ordered state for the table.
     */
    this.columnApi.applyColumnState({ state: [...defaults, ...final] });
  };

  /*
  Allow access to the grids API
   */
  onGridReady = (params) => {
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
    let list = colOrder.map((c) => {
      // Get the header name
      const field = this.props.fields.find((f) => f.field === c.colId);
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
  generateFileName = (ext) => {
    // yyyy-MM-dd-project-X-<metadata template name>.csv
    const fullDate = new Date();
    const date = `${fullDate.getFullYear()}-${
      fullDate.getMonth() + 1
    }-${fullDate.getDate()}`;
    const project = window.PAGE.project.label.replace(this.nameRegex, "_");
    const template = this.props.templates[this.props.current].name.replace(
      this.nameRegex,
      "_"
    );
    return `${date}-${project}-${template}.${ext}`;
  };

  createFile = (ext) => {
    /*
     * Lazy load xlsx utilities since exporting is not a function used on every page.
     */
    import(
      /* webpackChunkName: "exportUtilities" */ "../../../../../utilities/export-utilities"
    ).then((module) => {
      const createXLSX = module.default;
      notification.open({
        key: "export-notification",
        message: i18n("linelist.table.export.message"),
        icon: <Spin />,
        closeIcon: <div></div>,
        description: i18n("linelist.table.export.description"),
        duration: null,
      });

      /*
       * Wait 1 second before generating file, this allows the preparing
       * notification to display for at least 1 second before the file has
       * completed generation.
       */
      setTimeout(() => {
        /*
         * Get the visible columns.  Need to ignore the icon columns since
         * it does not contain any data that we want.
         */
        const filteredColIds = this.columnApi
          .getColumnState()
          .filter((c) => !c.hide && c.colId !== "icons")
          .map((c) => c.colId);

        const filename = this.generateFileName(ext);
        if (ext === "csv") {
          this.api.exportDataAsCsv({
            columnKeys: filteredColIds,
            fileName: filename,
          });
        } else {
          const data = this.api.getDataAsCsv({ columnKeys: filteredColIds });
          createXLSX({ filename, data });
        }
        notification.open({
          key: "export-notification",
          icon: <IconCheck style={{ color: green6 }} />,
          message: "File created",
        });
      }, 1000);
    });
  };

  addSamplesToCart = () => {
    const nodes = this.api.getSelectedNodes().map((n) => n.data);
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
    this.props.selection(this.api.getSelectedNodes().map((n) => n.data));
  };

  /**
   * When a cell is edited, store the value in case it needs to be reversed
   * @param {object} event - the cell edit event
   */
  onCellEditingStarted = (event) => {
    this.cellEditedValue = event.value || "";
  };

  /**
   * After the cell has been edited give the user a chance to undo the edit.
   * @param {object} event - cell edit event
   */
  onCellEditingStopped = (event) => {
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
        ? i18n(
            "linelist.editing.undo.full",
            `${data[FIELDS.sampleName]}`,
            `${headerName}`,
            `${data[field]}`
          )
        : i18n(
            "linelist.editing.undo.empty",
            `${headerName}`,
            `${data[FIELDS.sampleName]}`
          );
      const notKey = `open${Date.now()}`;
      notification.success({
        message: text,
        key: notKey,
        btn: (
          <Button
            type={"primary"}
            size={"small"}
            onClick={() => {
              /**
               * Callback to reverse the change.
               */
              data[field] = previousValue;
              this.props.entryEdited(data, field, headerName);
              event.node.setDataValue(field, previousValue);
              notification.close(notKey);
            }}
          >
            {i18n("generic.undo")}
          </Button>
        ),
      });
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
    return (
      <AgGridReact
        id="linelist-grid"
        rowSelection="multiple"
        onFilterChanged={this.setFilterCount}
        localeText={{
          loading: i18n("linelist.agGrid.loading"),
          sampleName: i18n("linelist.agGrid.sampleName"),
        }}
        columnDefs={this.props.fields}
        rowData={this.props.entries}
        components={this.components}
        loadingOverlayComponent={
          window.PAGE.totalSamples > 0 ? "LoadingOverlay" : null
        }
        onGridReady={this.onGridReady}
        onDragStopped={this.onColumnDropped}
        rowDeselection={true}
        suppressRowClickSelection={true}
        onSelectionChanged={this.onSelectionChange}
        defaultColDef={{
          headerCheckboxSelectionFilteredOnly: true,
          sortable: true,
          filter: true,
        }}
        enableCellChangeFlash={true}
        onCellEditingStarted={this.onCellEditingStarted}
        onCellEditingStopped={this.onCellEditingStopped}
      />
    );
  }
}

const mapStateToProps = (state) => ({
  loading: state.entries.loading,
  fields: state.fields.fields,
  templates: state.templates.templates,
  current: state.templates.current,
  entries: state.entries.entries?.entries,
  globalFilter: state.entries.globalFilter,
});

const mapDispatchToProps = (dispatch) => ({
  tableModified: (fields) => dispatch(templateActions.tableModified(fields)),
  entryEdited: (entry, field, label) =>
    dispatch(entryActions.edited(entry, field, label)),
  selection: (selected) => dispatch(entryActions.selection(selected)),
});

export const Table = connect(mapStateToProps, mapDispatchToProps, null, {
  forwardRef: true,
})(TableComponent);
