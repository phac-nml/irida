import React, { useState } from "react";
import { LaunchProvider, useLaunchState } from "./launch-context";
import { PipelineDetails } from "./PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";
import { PipelineParameters } from "./PipelineParameters";
import { Button, Col, Form, PageHeader, Row, Space, Steps } from "antd";
import { navigate } from "@reach/router";
import { setBaseUrl } from "../../utilities/url-utilities";
import styled from "styled-components";
import { grey3 } from "../../styles/colors";
import { LaunchComplete } from "./LaunchComplete";
import { PipelineLaunchButton } from "./PipelineLaunchButton";

const StepsContent = styled("div")`
  min-height: 500px;
  padding: 5px;
  border-radius: 3px;
  border: 1px solid ${grey3};
`;

function LaunchTabs() {
  const { original, complete, requiresReference } = useLaunchState();
  const [current, setCurrent] = useState(0);

  const steps = [
    {
      title: (
        <Button type="text" onClick={() => setCurrent(0)}>
          Pipeline Details
        </Button>
      ),
      content: <PipelineDetails />,
    },
    {
      title: (
        <Button type="text" onClick={() => setCurrent(1)}>
          Parameters
        </Button>
      ),
      content: <PipelineParameters />,
    },
  ];

  if (requiresReference) {
    steps.push({
      title: (
        <Button type="text" onClick={() => setCurrent(2)}>
          Reference File
        </Button>
      ),
      content: <ReferenceFiles />,
    });
  }

  const next = () => setCurrent(current + 1);

  const prev = () => setCurrent(current - 1);

  return complete ? (
    <LaunchComplete />
  ) : (
    <>
      <PageHeader
        title={original.name}
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
          {current === steps.length - 1 && <PipelineLaunchButton />}
          {current > 0 && <Button onClick={prev}>Previous</Button>}
        </div>
      </Space>
    </>
  );
}

export function LaunchPage({ pipelineId }) {
  return (
    <LaunchProvider pipelineId={pipelineId}>
      <Row>
        <Col xl={{ span: 12, offset: 6 }} lg={{ span: 18, offset: 3 }}>
          <LaunchTabs />
        </Col>
      </Row>
    </LaunchProvider>
  );
}
