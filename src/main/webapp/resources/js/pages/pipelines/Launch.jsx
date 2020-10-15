import React from "react";
import { Alert, Steps, Typography } from "antd";
import { LaunchProvider } from "./launch-context";
import { PipelineLaunchPage } from "../pipeline-launch/PipelineLaunchPage";

const { Title } = Typography;

export default function Launch() {
  const [current, setCurrent] = React.useState(0);

  const steps = [
    {
      title: "PARAMETERS",
      content: (
        <PipelineLaunchPage
          pipelineId={"f609c177-c268-4ad0-9d7f-9f9d5187fef7"}
        />
      ),
    },
    {
      title: "FILES",
      content: <div>I AM SOME FILES</div>,
    },
  ];
  return (
    <>
      <Steps>
        {steps.map((step) => (
          <Steps.Step title={step.title} key={step.title} />
        ))}
      </Steps>
      {steps[current].content}
    </>
  );
}
