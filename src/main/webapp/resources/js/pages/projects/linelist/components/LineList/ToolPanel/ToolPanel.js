import React from "react";
import { Checkbox } from "antd";

const templateColumnOpen = "template-column--open";
const templateColumnClosed = "template-column--closed";

export class ToolPanel extends React.Component {
  state = { panelClass: templateColumnClosed };
  constructor(props) {
    super(props);
  }

  updateClass = () => {
    this.setState({
      panelClass:
        this.state.panelClass === templateColumnOpen
          ? templateColumnClosed
          : templateColumnOpen
    });
  };

  render() {
    const classes = `template-column ${this.state.panelClass}`;
    console.log(classes);
    const fields = this.props.fields.toJS();
    return (
      <div className="template-column-wrapper">
        <div className={classes}>
          <div className="template-column--fields">
            <div>
              {fields.map(f => (
                <div>
                  <Checkbox key={f.field}>{f.field}</Checkbox>
                </div>
              ))}
            </div>
          </div>
          <div className="template-column--button--wrapper">
            <button
              className="template-column--button"
              onClick={this.updateClass}
            >
              Columns
            </button>
          </div>
        </div>
      </div>
    );
  }
}
