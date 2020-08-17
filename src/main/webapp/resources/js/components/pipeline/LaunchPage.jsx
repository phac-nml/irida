import React, { useState } from "react";
import { LaunchProvider, useLaunchState } from "./launch-context";
import { PipelineDetails } from "./PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";
import { PipelineParameters } from "./PipelineParameters";
import { Button, Form, PageHeader, Space, Steps } from "antd";
import { IconRocket } from "../icons/Icons";
import { navigate } from "@reach/router";
import { setBaseUrl } from "../../utilities/url-utilities";
import styled from "styled-components";
import { grey3 } from "../../styles/colors";

const StepsContent = styled("div")`
  min-height: 500px;
  padding: 5px;
  border-radius: 3px;
  border: 1px solid ${grey3};
`;

function LaunchTabs() {
  const { PIPELINE_NAME, requiresReference } = useLaunchState();
  const [current, setCurrent] = useState(0);

  const steps = [
    {
      title: "Pipeline Details",
      content: <PipelineDetails />,
    },
    requiresReference
      ? {
          title: "Reference Files",
          content: <ReferenceFiles />,
        }
      : null,
    {
      title: "Parameters",
      content: <PipelineParameters />,
    },
  ].filter(Boolean);

  const next = () => setCurrent(current + 1);

  const prev = () => setCurrent(current - 1);

  return (
    <>
      <PageHeader
        title={PIPELINE_NAME}
        onBack={() => navigate(setBaseUrl(`/cart/pipelines`))}
      />
      <Space direction="vertical" style={{ width: `100%` }} size="large">
        <Steps current={current}>
          {steps.map((item) => (
            <Steps.Step key={item.title} title={item.title} />
          ))}
        </Steps>
        <StepsContent>
          <Form layout={"vertical"}>{steps[current].content}</Form>
        </StepsContent>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            flexDirection: "row-reverse",
          }}
        >
          {current < steps.length - 1 && <Button onClick={next}>Next</Button>}
          {current === steps.length - 1 && (
            <Button type="primary" danger icon={<IconRocket />}>
              LAUNCH PIPELINE
            </Button>
          )}
          {current > 0 && <Button onClick={prev}>Previous</Button>}
        </div>
      </Space>
    </>
  );
}

export function LaunchPage({ pipelineId }) {
  return (
    <LaunchProvider pipelineId={pipelineId}>
      <LaunchTabs />
    </LaunchProvider>
  );
}
