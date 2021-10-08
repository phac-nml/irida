import React from "react";
import { useHistory, useParams } from "react-router-dom";
import { Button, Result } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete() {
  const { projectId } = useParams();
  const history = useHistory();

  return (
    <SampleMetadataImportWizard currentStep={3}>
      <Result
        status="success"
        title={i18n("SampleMetadataImportComplete.result.title")}
        subTitle={
          ((history.location.state.updatedSampleCount > 0) ? i18n("SampleMetadataImportComplete.result.subTitle.multiple-updated", history.location.state.updatedSampleCount) : i18n("SampleMetadataImportComplete.result.subTitle.single-updated", history.location.state.updatedSampleCount))
          +
          ((history.location.state.newSampleCount > 0) ? i18n("SampleMetadataImportComplete.result.subTitle.multiple-created", history.location.state.newSampleCount) : i18n("SampleMetadataImportComplete.result.subTitle.single-created", history.location.state.newSampleCount))
        }
        extra={
          <Button
            type="primary"
            href={setBaseUrl(
              `projects/${projectId}/sample-metadata/upload2/file`
            )}
          >
            {i18n("SampleMetadataImportComplete.button.upload")}
          </Button>
        }
      />
    </SampleMetadataImportWizard>
  );
}
