import React from "react";
import { SampleMetadata } from "./SampleMetadata";
import { Divider, Tabs, Typography } from "antd";
import { SampleFiles } from "./SampleFiles";
import { CalendarDate } from "../../CalendarDate";

const { Paragraph } = Typography;

/**
 * React component to render the details of a sample, including metadata
 * and files.
 *
 * @param details
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleDetails({ details }) {
  return (
    <>
      <CalendarDate date={details.sample.createdDate} />
      <Divider />
      <Paragraph ellipsis={{ rows: 3, expandable: true }}>
        {details.sample.description}
      </Paragraph>
      <Tabs defaultActiveKey="metadata">
        <Tabs.TabPane tab={i18n("SampleDetails.metadata")} key="metadata">
          <SampleMetadata metadata={details.metadata} />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.files")} key="files">
          <SampleFiles
            id={details.sample.identifier}
            projectId={details.projectId}
          />
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}
