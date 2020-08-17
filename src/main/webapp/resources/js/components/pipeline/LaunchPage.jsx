import React, { useState } from "react";
import { LaunchProvider, useLaunchState } from "./launch-context";
import { PipelineDetails } from "./PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";
import { PipelineParameters } from "./PipelineParameters";
import { Button, Space, Steps } from "antd";
import { IconRocket } from "../icons/Icons";

function LaunchTabs() {
  const { files, requiresReference, parameters } = useLaunchState();
  const [current, setCurrent] = useState(0);

  const steps = [
    {
      title: "Pipeline Details",
      content: <PipelineDetails />,
    },
    requiresReference
      ? {
          title: "Reference Files",
          content: <ReferenceFiles files={files} />,
        }
      : null,
    {
      title: "Parameters",
      content: <PipelineParameters parameters={parameters} />,
    },
  ].filter(Boolean);

  const next = () => setCurrent(current + 1);

  const prev = () => setCurrent(current - 1);

  return (
    <Space direction="vertical" style={{ width: `100%` }} size="large">
      <Steps current={current}>
        {steps.map((item) => (
          <Steps.Step key={item.title} title={item.title} />
        ))}
      </Steps>
      <div className="steps-content">{steps[current].content}</div>
      {/*<PipelineDetails />*/}
      {/*{requiresReference ? <ReferenceFiles files={files} /> : null}*/}
      {/*<PipelineParameters parameters={parameters} />*/}
      <Space>
        {current > 0 && <Button onClick={prev}>Previous</Button>}
        {current < steps.length - 1 && <Button onClick={next}>Next</Button>}
        {current === steps.length - 1 && (
          <Button danger icon={<IconRocket />}>
            LAUNCH PIPELINE
          </Button>
        )}
      </Space>
    </Space>
  );
}

export function LaunchPage({ pipelineId }) {
  return (
    <LaunchProvider pipelineId={pipelineId}>
      <LaunchTabs />
    </LaunchProvider>
  );
}
