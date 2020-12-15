import React from "react";
import { Divider } from "antd";

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
    <Divider orientation="left" {...props}>
      {children}
    </Divider>
  );
}
