import React from "react";
import { navigate } from "@reach/router"
import {
  Badge,
  Button,
  Space,
  Tabs,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useGetProjectSampleMetadataQuery  } from "../../../../apis/metadata/metadata-import";

const { Text } = Typography

function Back() {
  navigate(-1);
}

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview({ projectId }) {

  const { data, isSuccess } = useGetProjectSampleMetadataQuery(projectId);
  const { TabPane } = Tabs;
  console.log(data);
  console.log(isSuccess);

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>
        {i18n("SampleMetadataImportReview.description")}
      </Text>
      <Tabs type="card">
        <TabPane tab={<Space>Rows matching samples<Badge count={data?.found.length} style={{ backgroundColor: 'green' }} /></Space>} key="1">
          Content of Tab Pane 1
        </TabPane>
        <TabPane tab={<Space>Rows not matching samples<Badge count={data?.missing.length} /></Space>} key="2">
          Content of Tab Pane 2
        </TabPane>
      </Tabs>
      <Button onClick={() => Back()}> {i18n("SampleMetadataImportReview.back")}</Button>
    </SampleMetadataImportWizard>
  );
}