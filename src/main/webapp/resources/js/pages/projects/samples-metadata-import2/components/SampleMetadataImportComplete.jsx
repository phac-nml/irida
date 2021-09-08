import React from "react";
import { useSelector } from "react-redux";
import {
  Button,
  Result,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useSaveProjectSampleMetadataMutation } from '../../../../apis/metadata/metadata-import'
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * React component that displays Step #4 of the Sample Metadata Uploader.
 * This page is where the user receives confirmation that the metadata was uploaded successfully.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportComplete({ projectId }) {
  const { sampleNames } = useSelector((state) => state.reducer);
  const [saveMetadata] = useSaveProjectSampleMetadataMutation();
  const [status, setStatus] = React.useState();
  const [statusMessage, setStatusMessage] = React.useState();
  const [errorList, setErrorList] = React.useState();

  React.useEffect(() => {
    saveMetadata({ projectId, sampleNames })
      .unwrap()
      .then((payload) => {
        setStatus(payload.messageKey);
        setStatusMessage(payload.message);
        setErrorList(payload.errorList);
      });
  }, []);

  return (
    <SampleMetadataImportWizard currentStep={3}>
      <Result
        status={status == "success" ? "success" : "warning"}
        title={i18n("SampleMetadataImportComplete.result.title")}
        subTitle={statusMessage}
        extra={
          <Button type="primary" href={setBaseUrl(`projects/${projectId}/sample-metadata/upload2/file`)}>
            {i18n("SampleMetadataImportComplete.button.upload")}
          </Button>
        }
      />
    </SampleMetadataImportWizard>
  );
}