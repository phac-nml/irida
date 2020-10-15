import React from "react";
import { useLaunchState } from "./launch-context";
import { Button, Card, Form, Space } from "antd";
import { PipelineDetails } from "./PipelineDetails";
import { IconRocket } from "../../components/icons/Icons";
import { PipelineResultsSharing } from "./PipelineResultsSharing";
import { PipelineRequiredParameters } from "./PipelineRequiredParameters";
import { PipelineParametersWithOptions } from "./PipelineParametersWithOptions";
import { PipelineDynamicParameters } from "./PipelineDynamicParameters";

export function LaunchContent() {
  const { api } = useLaunchState();

  return (
    <section>
      <Form layout="vertical" onFinish={api.startPipeline}>
        <Space direction="vertical" size="middle" style={{ width: "100%" }}>
          <Card title={i18n("LaunchContent.details")}>
            <PipelineDetails />
          </Card>
          <Card title={i18n("LaunchContent.parameters")}>
            <PipelineRequiredParameters />
            <PipelineParametersWithOptions />
            <PipelineDynamicParameters />
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
