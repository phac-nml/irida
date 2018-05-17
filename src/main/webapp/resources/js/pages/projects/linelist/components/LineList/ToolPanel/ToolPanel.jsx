import React from "react";
import { Checkbox } from "antd";

export class ToolPanel extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    const fields = this.props.fields.toJS();
    return (
      <div className="ag-grid-tool-panel">
        {fields.map((f, index) => (
          <div key={index} style={{display:"block", width: 200}}>
            <Checkbox>{f.field}</Checkbox>
          </div>
        ))}
      </div>
    );
  }
}
