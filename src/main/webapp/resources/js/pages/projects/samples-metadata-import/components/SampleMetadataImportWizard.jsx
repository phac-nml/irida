import React from "react";
import { useParams } from "react-router-dom";
import { PageHeader, Space } from "antd";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays the Sample Metadata Uploader Wizard wrapper.
 * @prop {number} currentStep - the current step, starting with zero
 * @prop {string} currentStatus - the status of the current step
 * @prop {string} currentPercent - the progress percentage of the current step
 * @prop {any} children - the status of the current step
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportWizard({
  currentStep,
  currentStatus,
  currentPercent,
  children,
}) {
  const { projectId } = useParams();

  return (
    <Space direction="vertical" size="large" style={{ width: `100%` }}>
      <PageHeader
        title={i18n("SampleMetadataImportWizard.title")}
        subTitle={i18n("SampleMetadataImportWizard.intro")}
        onBack={() =>
          (window.location.href = setBaseUrl(`projects/${projectId}/linelist`))
        }
      />
      <SampleMetadataImportSteps
        currentStep={currentStep}
        currentStatus={currentStatus}
        currentPercent={currentPercent}
      />
      {children}
    </Space>
  );
}
