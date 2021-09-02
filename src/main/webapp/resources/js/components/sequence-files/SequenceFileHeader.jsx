import React from "react";
import { CalendarDate } from "../CalendarDate";
import { Typography } from "antd";

export function SequenceFileHeader({ file }) {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        width: `100%`,
      }}
    >
      <CalendarDate date={file.createdDate} />
    </div>
  );
}
