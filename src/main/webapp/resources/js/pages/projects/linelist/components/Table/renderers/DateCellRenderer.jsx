import React from "react";
import { formatDate, isDate } from "../../../../../../utilities/date-utilities";
import { Icon, Popover } from "antd";

export class DateCellRenderer extends React.Component {
  render() {
    const content = (
      <div>Expected to be a proper date e.g. January 12, 2018</div>
    );

    if (!this.props.value) {
      return "";
    } else if (isDate(this.props.value)) {
      return formatDate({ date: this.props.value });
    } else {
      return (
        <div style={{ display: "flex" }}>
          <span style={{ flex: 1 }}>{this.props.value}</span>
          <Popover
            content={content}
            title={
              <span>
                <Icon type="exclamation-circle-o" /> Date format error
              </span>
            }
          >
            <span>
              <i
                style={{ color: "white" }}
                className="fas fa-info-circle fa-fw"
              />
            </span>
          </Popover>
        </div>
      );
    }
  }
}
