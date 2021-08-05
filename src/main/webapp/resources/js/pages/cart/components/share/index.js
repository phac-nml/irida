import { Space, Steps, Typography } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { ShareProject } from "./ShareProject";

const { Step } = Steps;

/**
 * React component for Sharing samples with other projects.
 */
export function ShareLayout() {
  const current = useSelector((state) => state.share.current);
  return (
    <Space direction="vertical" style={{ display: "block" }} size="large">
      <Typography.Title level={3}>{i18n("ShareLayout.title")}</Typography.Title>
      <Steps current={current}>
        <Step title={i18n("ShareLayout.step.projects")} />
        <Step title={i18n("ShareLayout.step.samples")} />
        <Step title={i18n("ShareLayout.step.restrictions")} />
      </Steps>
      <ShareProject />
    </Space>
  );
}
