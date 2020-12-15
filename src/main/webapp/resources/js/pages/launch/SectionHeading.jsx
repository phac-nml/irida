import React from "react";
import { Divider } from "antd";

export function SectionHeading({ children, ...props }) {
  return (
    <Divider orientation="left" {...props}>
      {children}
    </Divider>
  );
}
