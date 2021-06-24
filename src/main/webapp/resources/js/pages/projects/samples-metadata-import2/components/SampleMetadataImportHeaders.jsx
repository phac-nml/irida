import React from "react";
import {
  Space,
  Typography,
} from "antd";

import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";

/**
 * React component that displays the steps for the Sample Metadata Uploader.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportHeaders() {
  const { Text, Title } = Typography
  return (
    <>
      <Space direction="vertical" size="large" style={{ width: `100%` }}>
        <Title level={3}>{i18n("SampleMetadataImportFileUploader.title")}</Title>
        <Text type="secondary">
          {i18n("SampleMetadataImportFileUploader.intro")}
        </Text>
        <SampleMetadataImportSteps currentStep={1} />
      </Space>
    </>
  );
}