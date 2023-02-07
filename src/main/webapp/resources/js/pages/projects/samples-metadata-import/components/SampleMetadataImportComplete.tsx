import React, { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Result } from "antd";
import { MetadataItem } from "../../../../apis/projects/samples";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../redux/store";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import { updateStep } from "../redux/importReducer";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete(): JSX.Element {
  const {
    sampleNameColumn,
    metadata,
    metadataValidateDetails,
    metadataSaveDetails,
  } = useImportSelector((state: ImportState) => state.importReducer);
  const dispatch: ImportDispatch = useImportDispatch();

  React.useEffect(() => {
    dispatch(updateStep(3, "process"));
  }, [dispatch]);

  const filteredSamples = React.useCallback(
    (metadataItem: MetadataItem, isSampleFound: boolean) => {
      return (
        metadataSaveDetails[metadataItem[sampleNameColumn]]?.saved === true &&
        !metadataValidateDetails[metadataItem[sampleNameColumn]].locked &&
        (isSampleFound
          ? metadataValidateDetails[metadataItem[sampleNameColumn]]
              .foundSampleId
          : !metadataValidateDetails[metadataItem[sampleNameColumn]]
              .foundSampleId)
      );
    },
    [metadataSaveDetails, metadataValidateDetails, sampleNameColumn]
  );

  const samplesUpdatedCount = useMemo(
    () =>
      metadata.filter((metadataItem: MetadataItem) =>
        filteredSamples(metadataItem, true)
      ).length,
    [filteredSamples, metadata]
  );

  const samplesCreatedCount = useMemo(
    () =>
      metadata.filter((metadataItem: MetadataItem) =>
        filteredSamples(metadataItem, false)
      ).length,
    [filteredSamples, metadata]
  );

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

  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();

  return (
    <Result
      status="success"
      title={i18n("SampleMetadataImportComplete.result.title")}
      subTitle={stats}
      extra={
        <Button
          type="primary"
          onClick={() => navigate(`/${projectId}/sample-metadata/upload/file`)}
        >
          {i18n("SampleMetadataImportComplete.button.upload")}
        </Button>
      }
    />
  );
}
