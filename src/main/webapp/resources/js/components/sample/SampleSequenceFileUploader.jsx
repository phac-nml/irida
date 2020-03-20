import React from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { FileUploader } from "../files/FileUploader";
import {
  showErrorNotification,
  showNotification
} from "../../modules/notifications";

/**
 * React component to upload sequence files for a sample.
 *
 * @returns {*}
 * @constructor
 */
export function SampleSequenceFileUploader() {
  /**
   * Display successful upload then show notification that page will refresh
   * Need to reload the table after successful upload since the table
   * is static.
   * @param text
   * @returns {*}
   */
  const onSuccess = text => {
    showNotification({ text });
    setTimeout(() => {
      window.location.reload();
    }, 3000);
  };

  const onBadFiles = files => {
    const names = (
      <ul>
        {files.map(f => (
          <li>{f.name()}</li>
        ))}
      </ul>
    );
    showErrorNotification({ text: names });
  };

  return (
    <FileUploader
      label={i18n("SampleSequenceFileUploader.button")}
      allowedTypes=".fastq,.fastq.gz"
      url={setBaseUrl(`ajax/samples/${window.PAGE.id}/sequenceFiles/upload`)}
      onSuccess={onSuccess}
      onError={text => showErrorNotification({ text })}
      onBadFiles={onBadFiles}
    />
  );
}
