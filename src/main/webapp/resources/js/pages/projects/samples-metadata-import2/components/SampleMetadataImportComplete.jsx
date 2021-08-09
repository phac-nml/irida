import React from "react";
import { navigate } from "@reach/router"
import {
  Button,
  Result,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useSaveProjectSampleMetadataMutation } from "../../../../apis/metadata/metadata-import";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete({ projectId }) {
const [saveMetadata, {isLoading}] = useSaveProjectSampleMetadataMutation(projectId);

  React.useEffect(() => {
    saveMetadata({ projectId: projectId });
  }, []);

  return (
    <SampleMetadataImportWizard currentStep={3}>
      <Result
        status="success"
        title="Congratulations! The sample metadata imported successfully."
        extra={
          <Button type="primary" href={setBaseUrl(`projects/${projectId}/sample-metadata/upload2/file`)}>
            Upload another file
          </Button>
        }
      />
    </SampleMetadataImportWizard>
  );
}