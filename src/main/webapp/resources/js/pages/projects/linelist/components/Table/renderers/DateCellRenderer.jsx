import React from "react";
import { formatDate, isDate } from "../../../../../../utilities/date-utilities";
import { Popover } from "antd";
import { ExclamationCircleTwoTone, InfoCircleTwoTone } from "@ant-design/icons";
import { SPACE_XS } from "../../../../../../styles/spacing";

/**
 * Component to properly display dates in the ag-grid
 */
export class DateCellRenderer extends React.Component {
  render() {
    const content = <div>{i18n("linelist.dateCell.tooltip")}</div>;
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
                <ExclamationCircleTwoTone style={{ marginRight: SPACE_XS }} />
                {i18n("linelist.dateCell.popover.title")}
              </span>
            }
          >
            <span>
              <InfoCircleTwoTone style={{ color: "white" }} />
            </span>
          </Popover>
        </div>
      );
    }
  }
}
