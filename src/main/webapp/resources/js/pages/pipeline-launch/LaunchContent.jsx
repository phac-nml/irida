import React from "react";
import { useLaunchState } from "./launch-context";
import { Button, Card, Form, PageHeader, Space } from "antd";
import { navigate } from "@reach/router";
import { setBaseUrl } from "../../utilities/url-utilities";
import { PipelineDetails } from "./PipelineDetails";
import { IconRocket } from "../../components/icons/Icons";
import { PipelineResultsSharing } from "./PipelineResultsSharing";
import { PipelineParameters } from "./PipelineParameters";

export function LaunchContent() {
  const { pipelineName, api } = useLaunchState();
  return (
    <section>
      <PageHeader
        title={i18n("LaunchContent.title", pipelineName)}
        onBack={() => navigate(setBaseUrl(`/cart/pipelines`))}
      />
      <Form layout="vertical" onFinish={api.startPipeline}>
        <Space direction="vertical" size="middle" style={{ width: "100%" }}>
          <Card title={i18n("LaunchContent.details")}>
            <PipelineDetails />
          </Card>
          <Card title={"PARAMETERS"}>
            <PipelineParameters />
          </Card>
          <Card title={i18n("LaunchContent.resultSharing")}>
            <PipelineResultsSharing />
          </Card>
          <Form.Item>
            <Button type="primary" htmlType="submit" icon={<IconRocket />}>
              {i18n("LaunchContent.launch-btn")}
            </Button>
          </Form.Item>
        </Space>
      </Form>
    </section>
  );
}
