import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Result } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { MetadataItem } from "../../../../apis/projects/samples";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../redux/store";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import { resetImport } from "../redux/importReducer";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete(): JSX.Element {
  const { metadata, metadataValidateDetails, metadataSaveDetails } =
    useImportSelector((state: ImportState) => state.importReducer);
  const dispatch: ImportDispatch = useImportDispatch();

  const samplesUpdatedCount = metadata.filter(
    (metadataItem: MetadataItem) =>
      metadataSaveDetails[metadataItem.rowKey]?.saved === true &&
      metadataValidateDetails[metadataItem.rowKey].foundSampleId
  ).length;

  const samplesCreatedCount = metadata.filter(
    (metadataItem: MetadataItem) =>
      metadataSaveDetails[metadataItem.rowKey]?.saved === true &&
      !metadataValidateDetails[metadataItem.rowKey].foundSampleId
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

  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const onClick = React.useCallback(async () => {
    await dispatch(resetImport());
    navigate(`/${projectId}/sample-metadata/upload/file`);
  }, [dispatch, projectId, navigate]);

  React.useEffect(() => {
    setTimeout(onClick, 10000);
  }, [onClick]);

  return (
    <SampleMetadataImportWizard current={3}>
      <Result
        status="success"
        title={i18n("SampleMetadataImportComplete.result.title")}
        subTitle={stats}
        extra={
          <Button type="primary" onClick={onClick}>
            {i18n("SampleMetadataImportComplete.button.upload")}
          </Button>
        }
      />
    </SampleMetadataImportWizard>
  );
}
