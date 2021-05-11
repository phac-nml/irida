/**
 * @File component responsible for the layout of the associated projects page.
 */
import { Space, Typography } from "antd";
import React from "react";
import ViewAssociatedProjects from "./associated/ViewAssociatedProjects";

export default function AssociatedProjects() {
  return (
    <>
      <Typography.Title level={2}>
        {i18n("AssociatedProjects.title")}
      </Typography.Title>
      <Space size="small" direction="vertical" style={{ display: "block" }}>
        <Typography.Text type="secondary">
          {i18n("AssociatedProjects.subTitle")}
        </Typography.Text>
        <ViewAssociatedProjects />
      </Space>
    </>
  );
}
