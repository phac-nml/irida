import React from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { FileUploader } from "../files/FileUploader";

/**
 * React component to upload sequence files for a sample.
 * @returns {*}
 * @constructor
 */
export function SampleSequenceFileUploader() {
  /**
   * Need to reload the table after successful upload since the table
   * is static.
   * @returns {*}
   */
  const onSuccess = () => window.location.reload();

  return (
    <FileUploader
      label={i18n("samples.files.upload.btn")}
      allowedTypes=".fastq,.fastq.gz"
      url={setBaseUrl(`samples/${window.PAGE.id}/sequenceFiles/upload`)}
      onSuccess={onSuccess}
      onError={e => console.log(e)}
    />
  );
}
