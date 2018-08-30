import React from "react";
import { Checkbox } from "antd";
import { TemplatesPanel } from "../TemplatesPanel";

/**
 * Use to hold the column selection in a tool panel to the right of the
 * ag-grid.
 */
export class ToolPanel extends React.Component {
  render() {
    console.log("RENDERING");
    let fields = [];
    if (this.props.templates.size > 0) {
      const template =
        this.props.templates.size > 0
          ? this.props.templates.get(this.props.current).toJS()
          : [];
      fields =
        template.modified.length === 0 ? template.fields : template.modified;
    }
    return (
      <div className="ag-grid-tool-panel">
        <TemplatesPanel {...this.props} />
        <div className="ag-grid-tool-panel--inner">
          {fields.map((f, index) => (
            <div key={index} className="field-checkbox">
              <Checkbox
                value={index}
                checked={!f.hide}
                className="t-field-toggle"
                onChange={this.fieldUpdated}
              >
                {f.label}
              </Checkbox>
            </div>
          ))}
        </div>
      </div>
    );
  }
}
