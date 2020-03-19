import React from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { FileUploader } from "../files/FileUploader";

/**
 * React component to upload sequence files for a sample.
 * @returns {*}
 * @constructor
 */
export function SampleSequenceFileUploader() {
  const onSuccess = () => window.location.reload();
  return (
    <FileUploader
      label={i18n("samples.files.upload.btn")}
      allowedTypes=".fastq,.fastq.gz"
      url={setBaseUrl(`samples/${window.PAGE.id}/sequenceFiles/upload`)}
      onSuccess={onSuccess}
    />
  );
}
