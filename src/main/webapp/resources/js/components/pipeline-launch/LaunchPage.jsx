import React from "react";
import { LaunchProvider, useLaunchState } from "./launch-context";
import { PipelineDetails } from "../pipeline/PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";
import { PipelineParameters } from "./PipelineParameters";
import { Button, Col, Form, PageHeader, Result, Row, Space, Tabs } from "antd";
import { navigate } from "@reach/router";
import { setBaseUrl } from "../../utilities/url-utilities";
import { LaunchComplete } from "./LaunchComplete";
import { PipelineLaunchButton } from "../pipeline/PipelineLaunchButton";

const { TabPane } = Tabs;

function LaunchTabs() {
  const { original, complete, requiresReference, notFound } = useLaunchState();

  const steps = [
    {
      key: "details",
      title: "Pipeline Details",
      content: <PipelineDetails />,
    },
    {
      key: "parameters",
      title: "Parameters",
      content: <PipelineParameters />,
    },
  ];

  if (requiresReference) {
    steps.push({
      key: "reference",
      title: "Reference File",
      content: <ReferenceFiles />,
    });
  }

  return complete ? (
    <LaunchComplete />
  ) : notFound ? (
    <Result
      status="404"
      title="The pipeline you are looking for cannot be found"
      subTitle="Try returning to the cart and selecting again"
      extra={
        <Button
          type="primary"
          onClick={() => navigate(setBaseUrl(`/cart/pipelines`))}
        >
          Back to Cart
        </Button>
      }
    />
  ) : (
    <Row>
      <Col xxl={{ span: 15, offset: 3 }} xl={{ span: 24 }}>
        <PageHeader
          title={original.name}
          onBack={() => navigate(setBaseUrl(`/cart/pipelines`))}
        />
        <Space direction="vertical" style={{ width: `100%` }} size="large">
          <Form layout="vertical">
            <Tabs tabPosition="left">
              {steps.map((step) => (
                <TabPane tab={step.title} key={step.key}>
                  {step.content}
                </TabPane>
              ))}
            </Tabs>
          </Form>
          <div style={{ display: "flex", flexDirection: "row-reverse" }}>
            <PipelineLaunchButton key="launch" />
          </div>
        </Space>
      </Col>
    </Row>
  );
}

export function LaunchPage({ pipelineId }) {
  return (
    <LaunchProvider pipelineId={pipelineId}>
      <LaunchTabs />
    </LaunchProvider>
  );
}
