import React from "react";
import { notification } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { DragUpload } from "../../../components/files/DragUpload.jsx";
import { referenceFileUploadComplete, useLaunch } from "../launch-context";

/**
 * Component to upload a reference file to the launch pipeline page.
 * @returns {JSX.Element}
 * @constructor
 */
export function UploadReferenceFile() {
  const [, launchDispatch] = useLaunch();

  const options = {
    multiple: true,
    showUploadList: false,
    action: setBaseUrl(`/ajax/reference-files`),
    onChange(info) {
      const { status } = info.file;
      if (status === "done") {
        notification.success({
          message: i18n("UploadReferenceFile.success", info.file.name),
        });
        referenceFileUploadComplete(
          launchDispatch,
          info.file.name,
          info.file.response.id
        );
      } else if (status === "error") {
        notification.error({
          message: i18n("UploadReferenceFile.error", info.file.name),
        });
      }
    },
  };

  return (
    <DragUpload
      uploadText={i18n("UploadReferenceFile.upload-text")}
      uploadHint={i18n("UploadReferenceFile.hint")}
      options={options}
    />
  );
}
