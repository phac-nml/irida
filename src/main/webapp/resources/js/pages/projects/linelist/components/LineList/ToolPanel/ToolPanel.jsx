import React from "react";
import { Checkbox } from "antd";

export class ToolPanel extends React.Component {
  state = { fields: [] };
  constructor(props) {
    super(props);
  }

  static getDerivedStateFromProps(nextProps) {
    if (nextProps.templates.size > 0) {
      const template = nextProps.templates.get(nextProps.current).toJS();
      const fields =
        template.modified.length === 0 ? template.fields : template.modified;
      return { fields };
    }
    return null;
  }

  fieldUpdated = e => {
    const { fields } = this.state;
    fields[e.target.value].hide = !e.target.checked;
    this.setState({ fields });
    this.props.templateModified(fields);
  };

  render() {
    return (
      <div className="ag-grid-tool-panel">
        {this.state.fields.map((f, index) => (
          <div key={f.id} style={{ display: "block", width: 200 }}>
            <Checkbox
              value={index}
              checked={!f.hide}
              onChange={this.fieldUpdated}
            >
              {f.label}
            </Checkbox>
          </div>
        ))}
      </div>
    );
  }
}
