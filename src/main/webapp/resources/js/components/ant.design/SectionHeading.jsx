import React from "react";
import { Typography } from "antd";
import { grey7 } from "../../styles/colors";

/**
 * React component to render section headings for forms
 *
 * @param children - label for the section.
 * @param props - any remaining props (e.g. id)
 * @returns {JSX.Element}
 * @constructor
 */
export function SectionHeading({ children, ...props }) {
  return (
    <Typography.Title level={3} style={{ color: grey7 }} {...props}>
      {children}
    </Typography.Title>
  );
}
