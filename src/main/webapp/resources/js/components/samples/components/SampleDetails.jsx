import React from "react";
import { SampleMetadata } from "./SampleMetadata";
import { Tabs, Typography } from "antd";
import { SampleFiles } from "./SampleFiles";
import { SampleInfo } from "./SampleInfo";
import { SampleAnalyses } from "./SampleAnalyses";

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
      <Paragraph ellipsis={{ rows: 3, expandable: true }}>
        {details.sample.description}
      </Paragraph>
      <Tabs defaultActiveKey="details">
        <Tabs.TabPane tab={i18n("SampleDetails.details")} key="details">
          <SampleInfo
            sample={details.sample}
            isModifiable={details.modifiable}
          />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.metadata")} key="metadata">
          <SampleMetadata
            sampleId={details.sample.identifier}
            isModifiable={details.modifiable}
          />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.files")} key="files">
          <SampleFiles
            id={details.sample.identifier}
            projectId={details.projectId}
          />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.analyses")} key="analyses">
          <SampleAnalyses />
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}
