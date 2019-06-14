import React from "react";
import PropTypes from "prop-types";
import { List, Switch } from "antd";
import { FIELDS } from "../../../constants";

/**
 * Component to display a list of switches which control the column
 * visibility within the metadata table.  Each switch represents
 * a Metadata Field.
 */
export default class ColumnVisibility extends React.Component {
  static propTypes = {
    /**
     * A list of Metadata templates related to the project.
     */
    templates: PropTypes.array.isRequired,
    /**
     * The index in the templates that is currently displayed.
     */
    current: PropTypes.number.isRequired,
    /**
     * A callback function for when there is a modification to the current template.
     * This allows the table to be updated.
     */
    templateModified: PropTypes.func.isRequired,
    /**
     * The current height of the table.  This is only used to update the height of the
     * panel when the user modifies the size of the browser window.
     */
    height: PropTypes.number.isRequired
  };

  /**
   * Handle togging of a check box.
   * @param {object} item - Metadata Field selected
   * @param {boolean} checked - whether the field should be visible or hidden.
   * @param {array} columns - list of all the columns
   */
  fieldUpdated = (item, checked, columns) => {
    /*
    Update the global state with the modified template.
     */
    this.props.templateModified(item);
  };

  render() {
    let columns = [];
    if (this.props.templates.length > 0) {
      const template = this.props.templates[this.props.current];

      columns = [
        ...(template.modified.length === 0
          ? template.fields
          : template.modified)
      ];
    }

    return (
      <div className="ag-grid-tool-panel--inner">
        {typeof columns !== "undefined" ? (
          <div style={{ overflowY: "auto", height: this.props.height - 77 }}>
            <List
              dataSource={columns.filter(
                f => f.field !== FIELDS.sampleName && f.field !== FIELDS.icons
              )}
              renderItem={item => (
                <List.Item
                  className="column-list-item"
                  actions={[
                    <Switch
                      size="small"
                      className="t-field-switch"
                      checked={!item.hide}
                      onChange={checked =>
                        this.fieldUpdated(item, checked, columns)
                      }
                    />
                  ]}
                >
                  <span className="column-list-item--text">
                    {item.headerName}
                  </span>
                </List.Item>
              )}
            />
          </div>
        ) : null}
      </div>
    );
  }
}
