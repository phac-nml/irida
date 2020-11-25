import React from "react";
import { Typography } from "antd";

/**
 * React component to display the details of a pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchPageHeader({ pipeline }) {
  return (
    <>
      <Typography.Title>{pipeline.name}</Typography.Title>
      <Typography.Paragraph>{pipeline.description}</Typography.Paragraph>
    </>
  );
}
