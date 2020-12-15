import React from "react";
import { IconCalendarTwoTone } from "./icons/Icons";
import { formatInternationalizedDateTime } from "../utilities/date-utilities";
import { Space, Typography } from "antd";

/**
 * React component to display a calendar icon with a formatted date
 * @param date - Date to be displayed.
 * @returns {JSX.Element}
 * @constructor
 */
export function CalendarDate({ date }) {
  return (
    <Space size="small">
      <IconCalendarTwoTone />
      <Typography.Text type="secondary">
        <span className="t-date-text">
          {formatInternationalizedDateTime(date)}
        </span>
      </Typography.Text>
    </Space>
  );
}
