import React from "react";
import {
  List,
  Space,
  Typography,
} from "antd";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";
import { useSelector } from "react-redux";

const { Text, Title } = Typography

/**
 * React component that displays the steps for the Sample Metadata Uploader.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportHeaders({ projectId }) {

  const { headers, nameColumn } = useSelector((state) => state.reducer);
  console.log(nameColumn);

  return (
    <>
      <Space direction="vertical" size="large" style={{ width: `100%` }}>
        <Title level={3}>{i18n("SampleMetadataImportFileUploader.title")}</Title>
        <Text type="secondary">
          {i18n("SampleMetadataImportFileUploader.intro")}
        </Text>
        <SampleMetadataImportSteps currentStep={1} />
        <List
          dataSource={headers}
          renderItem={item => <List.Item>{item}</List.Item>} />
      </Space>
    </>
  );
}