import React from "react";
import { useParams } from "react-router-dom";
import { PageHeader, Space, StepsProps } from "antd";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";
import { setBaseUrl } from "../../../../utilities/url-utilities";

interface SampleMetadataImportWizardProps {
  current: StepsProps["current"];
  status?: StepsProps["status"];
  percent?: StepsProps["percent"];
  children?: React.ReactNode;
}

/**
 * React component that displays the Sample Metadata Uploader Wizard wrapper.
 * @prop current - the current step, starting with zero
 * @prop status - the status of the current step
 * @prop percent - the progress percentage of the current step
 * @prop children - the status of the current step
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportWizard({
  current,
  status,
  percent,
  children,
}: SampleMetadataImportWizardProps): JSX.Element {
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
        current={current}
        status={status}
        percent={percent}
      />
      {children}
    </Space>
  );
}
