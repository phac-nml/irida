import React from "react";
import { IconCalendarTwoTone } from "./icons/Icons";
import { formatInternationalizedDateTime } from "../utilities/date-utilities";
import { Space, Typography } from "antd";

export function CalendarDate({ date }) {
  return (
    <Space size="small">
      <IconCalendarTwoTone />
      <Typography.Text type="secondary">
        {formatInternationalizedDateTime(date)}
      </Typography.Text>
    </Space>
  );
}
