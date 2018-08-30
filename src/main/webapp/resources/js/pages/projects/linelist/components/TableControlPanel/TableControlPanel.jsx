import React from "react";
import PropTypes from "prop-types";
import ColumnVisibility from "./Columns/ColumnVisibility";
import { Button, Tooltip } from "antd";
import { TemplatesPanel } from "./TemplatesPanel";
import Columns from "./Columns/Columns";

export default class TableControlPanel extends React.Component {
  static propTypes = {
    togglePanel: PropTypes.func.isRequired,
    height: PropTypes.number.isRequired
  };

  render() {
    return (
      <div className="control-panel">
        <div className="control-panel--content">
          <Columns {...this.props} />
        </div>
        <div className="control-panel--buttons">
          <Tooltip title="COLUMN VISIBILITY" placement="left">
            <Button
              tour="tour-columns"
              shape="circle"
              onClick={this.props.togglePanel}
            >
              <i className="fas fa-columns" />
            </Button>
          </Tooltip>
        </div>
      </div>
    );
  }
}
