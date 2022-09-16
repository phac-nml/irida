import React from "react";
import { useParams } from "react-router-dom";
import { Button, Result } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { useSelector } from "react-redux";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete() {
  const { metadata } = useSelector((state) => state.importReducer);

  const samplesUpdatedCount = metadata.filter(
    (metadataItem) =>
      metadataItem.saved === true && metadataItem.foundSampleId !== null
  ).length;

  const samplesCreatedCount = metadata.filter(
    (metadataItem) =>
      metadataItem.saved === true && metadataItem.foundSampleId === null
  ).length;

  let stats =
    samplesUpdatedCount == 1
      ? i18n(
          "server.metadataimport.results.save.success.single-updated",
          samplesUpdatedCount
        )
      : i18n(
          "server.metadataimport.results.save.success.multiple-updated",
          samplesUpdatedCount
        );
  stats +=
    samplesCreatedCount == 1
      ? i18n(
          "server.metadataimport.results.save.success.single-created",
          samplesCreatedCount
        )
      : i18n(
          "server.metadataimport.results.save.success.multiple-created",
          samplesCreatedCount
        );

  const { projectId } = useParams();

  return (
    <SampleMetadataImportWizard currentStep={3}>
      <Result
        status="success"
        title={i18n("SampleMetadataImportComplete.result.title")}
        subTitle={stats}
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
