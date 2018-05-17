import React from "react";
import { TableContainer } from "./Table";
import { ToolPanelContainer } from "./ToolPanel";

export class TableLayoutComponent extends React.Component {
  state = {
    className: "tool-panel__open"
  };
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className="linelist-wrapper">
        <div className={this.state.className}>
          <TableContainer />
          <ToolPanelContainer />
        </div>
      </div>
    );
  }
}
