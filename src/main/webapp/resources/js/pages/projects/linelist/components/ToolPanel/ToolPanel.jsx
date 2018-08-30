import React from "react";
import { TemplatesPanel } from "../TemplatesPanel";
import ColumnVisibility from "./ColumnVisibility";

/**
 * Use to hold the column selection in a tool panel to the right of the
 * ag-grid.
 */
export class ToolPanel extends React.Component {
  render() {
    return (
      <div className="ag-grid-tool-panel">
        <TemplatesPanel {...this.props} />
        <ColumnVisibility
          columns={this.props.fields}
          templates={this.props.templates}
          current={this.props.current}
          templateModified={this.props.templateModified}
        />
      </div>
    );
  }
}
