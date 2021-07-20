import React from "react";
import {
  Space,
  Typography,
} from "antd";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";

const { Text, Title } = Typography

/**
 * React component that displays the Sample Metadata Uploader Wizard wrapper.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportWizard({ currentStep, currentStatus, children }) {
  return (
    <Space direction="vertical" size="large" style={{ width: `100%` }}>
      <Title level={3}>{i18n("SampleMetadataImportWizard.title")}</Title>
      <Text type="secondary">
        {i18n("SampleMetadataImportWizard.intro")}
      </Text>
      <SampleMetadataImportSteps currentStep={currentStep} currentStatus={currentStatus} />
      {children}
    </Space>
  );
}