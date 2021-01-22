import React from "react";
import { PageHeader, Typography } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * React component to display the details of a pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchPageHeader({ pipeline }) {
  return (
    <div style={{ marginBottom: SPACE_LG }}>
      <PageHeader
        title={pipeline.name}
        onBack={() => (window.location.href = setBaseUrl(`cart/pipelines`))}
      />
      <Typography.Paragraph type="secondary">
        {pipeline.description}
      </Typography.Paragraph>
    </div>
  );
}
