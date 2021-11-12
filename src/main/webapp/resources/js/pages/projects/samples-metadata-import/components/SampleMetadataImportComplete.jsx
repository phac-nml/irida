import React from "react";
import { useParams, useLocation } from "react-router-dom";
import { Button, Result } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructore
 */
export function SampleMetadataImportComplete() {
  const location = useLocation();

  const { projectId } = useParams();

  return (
    <SampleMetadataImportWizard currentStep={3}>
      <Result
        status="success"
        title={i18n("SampleMetadataImportComplete.result.title")}
        subTitle={location.state.statusMessage}
        extra={
          <Button
            type="primary"
            href={setBaseUrl(
              `projects/${projectId}/sample-metadata/upload/file`
            )}
          >
            {i18n("SampleMetadataImportComplete.button.upload")}
          </Button>
        }
      />
    </SampleMetadataImportWizard>
  );
}
