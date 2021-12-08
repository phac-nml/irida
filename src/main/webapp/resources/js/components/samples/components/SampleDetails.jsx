import React from "react";
import { SampleMetadata } from "./SampleMetadata";
import { Tabs, Typography } from "antd";
import { SampleFiles } from "./SampleFiles";
import { SampleInfo } from "./SampleInfo";
import { SampleAnalyses } from "./SampleAnalyses";
import { useDispatch } from "react-redux";
import { setProject, setSample } from "../sampleSlice";

const { Paragraph } = Typography;

/**
 * React component to render the details of a sample, including metadata
 * and files.
 *
 * @param details - The sample details
 * @param projectId - identifier for a project if provided
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleDetails({ details, projectId }) {
  const dispatch = useDispatch();

  React.useEffect(() => {
    dispatch(
      setSample({ sample: details.sample, modifiable: details.modifiable })
    );
    dispatch(setProject(details.projectId ? details.projectId : projectId));
  }, [dispatch]);

  return (
    <>
      <Paragraph ellipsis={{ rows: 3, expandable: true }}>
        {details.sample.description}
      </Paragraph>
      <Tabs defaultActiveKey="details">
        <Tabs.TabPane tab={i18n("SampleDetails.details")} key="details">
          <SampleInfo />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.metadata")} key="metadata">
          <SampleMetadata />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.files")} key="files">
          <SampleFiles />
        </Tabs.TabPane>
        <Tabs.TabPane tab={i18n("SampleDetails.analyses")} key="analyses">
          <SampleAnalyses />
        </Tabs.TabPane>
      </Tabs>
    </>
  );
}
