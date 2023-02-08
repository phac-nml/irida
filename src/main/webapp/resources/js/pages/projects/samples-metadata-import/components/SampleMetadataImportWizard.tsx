import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { PageHeader, Space } from "antd";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays the Sample Metadata Uploader Wizard wrapper.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportWizard(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();

  return (
    <PageHeader
      title={i18n("SampleMetadataImportWizard.title")}
      subTitle={i18n("SampleMetadataImportWizard.intro")}
      onBack={() =>
        (window.location.href = setBaseUrl(`projects/${projectId}/linelist`))
      }
    >
      <Space direction="vertical" size="large" style={{ width: `100%` }}>
        <SampleMetadataImportSteps />
        <Outlet />
      </Space>
    </PageHeader>
  );
}
