import React from "react";
import {
  Radio,
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

  const { headers, sampleNameColumn } = useSelector((state) => state.reducer);

  return (
    <>
      <Space direction="vertical" size="large" style={{ width: `100%` }}>
        <Title level={3}>{i18n("SampleMetadataImportFileUploader.title")}</Title>
        <Text type="secondary">
          {i18n("SampleMetadataImportFileUploader.intro")}
        </Text>
        <SampleMetadataImportSteps currentStep={1} />
        <Radio.Group buttonStyle="solid" size="large" defaultValue={sampleNameColumn}>
          {headers.map((header) => (
            <Radio.Button key={header} value={header}>
              {header}
            </Radio.Button>
          ))}
        </Radio.Group>
      </Space>
    </>
  );
}