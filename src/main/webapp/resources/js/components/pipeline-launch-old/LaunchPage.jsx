import React from "react";
import { LaunchProvider, useLaunchState } from "./launch-context";
import { PipelineDetails } from "../../pages/pipeline-launch/PipelineDetails";
import { ReferenceFiles } from "../reference/ReferenceFiles";
import { PipelineParameters } from "./PipelineParameters";
import { Button, Card, Col, Form, PageHeader, Result, Row, Space, } from "antd";
import { navigate } from "@reach/router";
import { setBaseUrl } from "../../utilities/url-utilities";
import { LaunchComplete } from "./LaunchComplete";
import { IconRocket } from "../icons/Icons";

function LaunchTabs() {
  const {
    fetching,
    name,
    original,
    complete,
    requiresReference,
    notFound,
    parametersWithOptions,
  } = useLaunchState();

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

  const onFinish = (values) => {
    console.log("Success:", values);
  };

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
        {fetching ? null : (
          <Form
            layout="vertical"
            initialValues={{
              name,
              description: "",
              parameters: 0,
              ...parametersWithOptions,
            }}
            onFinish={(values) => console.log(values)}
          >
            <Space direction="vertical" style={{ width: `100%` }} size="large">
              <Card title={"PIPELINE DETAILS"}>
                <PipelineDetails />
              </Card>
              <Card title={"PIPELINE PARAMETERS"}>
                <PipelineParameters />
              </Card>
              {requiresReference ? (
                <Card title={"Reference File"}>
                  <ReferenceFiles />
                </Card>
              ) : null}
              <div style={{ display: "flex", flexDirection: "row-reverse" }}>
                <Button type="primary" icon={<IconRocket />} htmlType="submit">
                  Launch Pipeline
                </Button>
              </div>
            </Space>
          </Form>
        )}
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
