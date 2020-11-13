import React from "react";
import { message } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { useLaunchDispatch } from "../launch-context";
import { DragUpload } from "../../../components/files/DragUpload.jsx";

export function UploadReferenceFile() {
  const { dispatchReferenceFileUploaded } = useLaunchDispatch();

  const options = {
    multiple: true,
    showUploadList: false,
    action: setBaseUrl(`/ajax/reference-files`),
    onChange(info) {
      const { status } = info.file;
      if (status === "done") {
        message.success(i18n("UploadReferenceFile.success", info.file.name));
        dispatchReferenceFileUploaded({
          name: info.file.name,
          id: info.file.response.id,
        });
      } else if (status === "error") {
        message.error(`${info.file.name} file upload failed.`);
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
