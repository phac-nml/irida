import React from "react";

import { Progress, Typography } from "antd";
import { LineListLayoutComponent } from "./LineListLayoutComponent";
import { ErrorAlert } from "../../../../../components/alerts/ErrorAlert";

const { project } = window.PAGE;

/**
 * Container class for the higher level states of the page:
 * 1. Loading
 * 2. Table
 * 3. Loading error.
 */
export function LineList({ error, loading, ...props }) {
  if (!!loading) {
    return (
      <div
        style={{
          width: `100%`,
          height: 300,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <Typography.Title level={3}>
          {i18n("linelist.loading", loading.total)}
        </Typography.Title>
        <Progress
          percent={Math.ceil((loading.count / loading.total) * 100)}
          type="circle"
        />
      </div>
    );
  } else if (error) {
    return (
      <ErrorAlert message={i18n("linelist.error.message", project.name)} />
    );
  }

  return <LineListLayoutComponent {...props} />;
}
