import React from "react";
import { Typography } from "antd";
import { SPACE_LG } from "../../styles/spacing";

/**
 * React component to display the details of a pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchPageHeader({ pipeline }) {
  return (
    <div style={{ marginBottom: SPACE_LG }}>
      <Typography.Title>{pipeline.name}</Typography.Title>
      <Typography.Paragraph type="secondary">
        {pipeline.description}
      </Typography.Paragraph>
    </div>
  );
}
