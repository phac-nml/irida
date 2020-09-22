import React from "react";
import { useLaunchState } from "./launch-context";
import { Card, Form, PageHeader, Space } from "antd";
import { navigate } from "@reach/router";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PipelineDetails } from "./PipelineDetails";

export function LaunchContent() {
  const { pipelineName } = useLaunchState();
  return (
    <section>
      <PageHeader
        title={`Launch ${pipelineName} Pipeline`}
        onBack={() => navigate(setBaseUrl(`/cart/pipelines`))}
      />
      <Form layout="vertical">
        <Space direction="vertical" size="large" style={{ width: "100%" }}>
          <Card title={"PIPELINE DETAILS"}>
            <PipelineDetails />
          </Card>
        </Space>
      </Form>
    </section>
  );
}
