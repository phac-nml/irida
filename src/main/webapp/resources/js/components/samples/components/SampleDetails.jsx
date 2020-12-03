import React from "react";
import { SampleMetadata } from "./SampleMetadata";
import { Space, Tabs, Typography } from "antd";
import { SampleFiles } from "./SampleFiles";
import { CalendarDate } from "../../CalendarDate";

const { Paragraph } = Typography;

export function SampleDetails({ details }) {
  return (
    <>
      <Space direction="vertical">
        <CalendarDate date={details.sample.createdDate} />
        <Paragraph>{details.sample.description}</Paragraph>
      </Space>
      <Tabs defaultActiveKey="metadata">
        <Tabs.TabPane tab={"METADATA"} key="metadata">
          <SampleMetadata metadata={details.metadata} />
        </Tabs.TabPane>
        <Tabs.TabPane tab={"FILES"} key="files">
          <SampleFiles
            id={details.sample.identifier}
            projectId={details.projectId}
          />
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}
