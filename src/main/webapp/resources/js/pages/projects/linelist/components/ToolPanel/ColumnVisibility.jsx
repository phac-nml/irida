import React from "react";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Switch, List } from "antd";

export default class ColumnVisibility extends React.Component {
  static propTypes = {
    columns: ImmutablePropTypes.list.isRequired,
    templates: ImmutablePropTypes.list.isRequired
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
        ) : null}
      </div>
    );
  }
}
