import React from "react";
import {
  Button,
  Result,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete({ projectId }) {
  const new_sample_count = 1;
  const updated_sample_count = 2;

  return (
    <SampleMetadataImportWizard currentStep={3}>
      <Result
        status="success"
        title={i18n("SampleMetadataImportComplete.result.title")}
        subTitle={
          ((updated_sample_count > 0) ? i18n("SampleMetadataImportComplete.result.subTitle.multiple-updated", updated_sample_count) : i18n("SampleMetadataImportComplete.result.subTitle.single-updated", updated_sample_count))
          +
          ((new_sample_count > 0) ? i18n("SampleMetadataImportComplete.result.subTitle.multiple-created", new_sample_count) : i18n("SampleMetadataImportComplete.result.subTitle.single-created", new_sample_count))
        }
        extra={
          <Button type="primary" href={setBaseUrl(`projects/${projectId}/sample-metadata/upload2/file`)}>
            {i18n("SampleMetadataImportComplete.button.upload")}
          </Button>
        }
      />
    </SampleMetadataImportWizard>
  );
}