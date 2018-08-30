import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Switch, List } from "antd";

export default class ColumnVisibility extends React.Component {
  static propTypes = {
    templates: ImmutablePropTypes.list.isRequired,
    current: PropTypes.number.isRequired,
    templateModified: PropTypes.func.isRequired,
    height: PropTypes.number.isRequired
  };

  /**
   * Handle togging of a check box.
   * @param {object} e click event.
   * @param {array} columns list of all the columns
   */
  fieldUpdated = (item, checked, columns) => {
    item.hide = !checked;
    this.setState({ columns });

    /*
    Update the global state with the modified template.
     */
    this.props.templateModified(columns);
  };

  render() {
    let columns = [];
    if (this.props.templates.size > 0) {
      const template = this.props.templates.get(this.props.current).toJS();
      columns =
        template.modified.length === 0 ? template.fields : template.modified;
    }

    return (
      <div className="ag-grid-tool-panel--inner">
        {typeof columns !== "undefined" ? (
          <div style={{ overflowY: "auto", height: this.props.height - 77 }}>
            <List
              dataSource={columns}
              renderItem={item => (
                <List.Item
                  actions={[
                    <Switch
                      size="small"
                      checked={!item.hide}
                      onChange={checked =>
                        this.fieldUpdated(item, checked, columns)
                      }
                    />
                  ]}
                >
                  <span style={{ marginLeft: 10 }}>{item.label}</span>
                </List.Item>
              )}
            />
          </div>
        ) : null}
      </div>
    );
  }
}
