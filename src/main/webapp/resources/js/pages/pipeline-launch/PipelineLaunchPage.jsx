import React from "react";
import { LaunchContent } from "./LaunchContent";
import { Button, Card, Form, Space, Tabs, Typography } from "antd";
import { useLaunchState } from "./launch-context";
import { IconRocket } from "../../components/icons/Icons";
import { SPACE_MD } from "../../styles/spacing";
import { PipelineDetails } from "./PipelineDetails";
import { PipelineResultsSharing } from "./PipelineResultsSharing";

export function PipelineLaunchPage() {
  const { details, api } = useLaunchState();

  // Update the page title with the current pipeline.
  const title = i18n("LaunchContent.title", details.name);
  document.title = title;

  return (
    <Card>
      <Space direction="vertical" style={{ width: `100%` }}>
        <Typography.Title>
          <Space>
            <IconRocket />
            {title}
          </Space>
        </Typography.Title>
        <Typography.Paragraph>{details.description}</Typography.Paragraph>
        <Form layout="vertical" onFinish={api.startPipeline}>
          <Tabs>
            <Tabs.TabPane tab={i18n("LaunchContent.details")} key="details">
              <PipelineDetails />
              <PipelineResultsSharing />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={i18n("LaunchContent.parameters")}
              key="parameters"
            >
              <LaunchContent />
            </Tabs.TabPane>
            <Tabs.TabPane tab={i18n("LaunchContent.files")} key="files">
              <div>I AM A BUNCH OF FILES</div>
            </Tabs.TabPane>
          </Tabs>
          <Form.Item style={{ marginTop: SPACE_MD }}>
            <Button type="primary" htmlType="submit" icon={<IconRocket />}>
              {i18n("LaunchContent.launch-btn")}
            </Button>
          </Form.Item>
        </Form>
      </Space>
    </Card>
  );
}
