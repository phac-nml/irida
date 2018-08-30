import React from "react";
import PropTypes from "prop-types";
import ColumnVisibility from "../ToolPanel/ColumnVisibility";
import { Button, Tooltip } from "antd";
import { TemplatesPanel } from "../TemplatesPanel";

export default class TableControlPanel extends React.Component {
  static propTypes = {
    togglePanel: PropTypes.func.isRequired,
    height: PropTypes.number.isRequired
  };

  render() {
    return (
      <div className="control-panel">
        <div className="control-panel--content">
          <TemplatesPanel {...this.props} />
          <ColumnVisibility
            height={this.props.height}
            templates={this.props.templates}
            current={this.props.current}
            templateModified={this.props.templateModified}
          />
        </div>
        <div className="control-panel--buttons">
          <Tooltip title="COLUMN VISIBILITY" placement="left">
            <Button shape="circle" onClick={this.props.togglePanel}>
              <i className="fas fa-columns" />
            </Button>
          </Tooltip>
        </div>
      </div>
    );
  }
}
