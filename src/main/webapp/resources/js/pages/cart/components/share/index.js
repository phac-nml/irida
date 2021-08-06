import { Steps, Typography } from "antd";
import React from "react";

const { Step } = Steps;

export function ShareLayout() {
  const [current, setCurrent] = React.useState(0);
  return (
    <>
      <Typography.Title level={3}>Share Samples with Project</Typography.Title>
      <Steps current={current}>
        <Step title={i18n("ShareLayout.step.projects")} />
        <Step title={i18n("ShareLayout.step.samples")} />
        <Step title={i18n("ShareLayout.step.restrictions")} />
      </Steps>
    </>
  );
}
