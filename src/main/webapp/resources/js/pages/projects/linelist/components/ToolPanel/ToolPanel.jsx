import React from "react";
import { Checkbox } from "antd";
import { TemplatesPanel } from "../TemplatesPanel";

/**
 * Use to hold the column selection in a tool panel to the right of the
 * ag-grid.
 */
export class ToolPanel extends React.Component {
  state = { fields: [] };
  constructor(props) {
    super(props);
  }

  static getDerivedStateFromProps(nextProps) {
    /*
    Make sure there are templates.  IF there are determine if you need to use
    the actual template or its modified sate.
     */
    if (nextProps.templates.size > 0) {
      const template = nextProps.templates.get(nextProps.current).toJS();
      const fields =
        template.modified.length === 0 ? template.fields : template.modified;
      return { fields };
    }
    return null;
  }

  /**
   * Handle togging of a check box.
   * @param {object} e click event.
   */
  fieldUpdated = e => {
    const { fields } = this.state;
    fields[e.target.value].hide = !e.target.checked;
    this.setState({ fields });

    /*
    Update the global state with the modified template.
     */
    this.props.templateModified(fields);
  };

  render() {
    return (
      <div className="ag-grid-tool-panel">
        <TemplatesPanel {...this.props} />
        <div className="ag-grid-tool-panel--inner">
          {this.state.fields.map((f, index) => (
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
