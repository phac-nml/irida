import React from "react";
import { notification } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { DragUpload } from "../../../components/files/DragUpload";
import { useLaunch } from "../launch-context";
import { referenceFileUploadComplete } from "../launch-dispatch";

/**
 * Component to upload a reference file to the launch pipeline page.
 * @returns {JSX.Element}
 * @constructor
 */
export function UploadReferenceFile({ form }) {
  const [, launchDispatch] = useLaunch();

  const options = {
    multiple: true,
    showUploadList: false,
    action: setBaseUrl(`/ajax/reference-files`),
    onChange(info) {
      const { status, response } = info.file;
      if (status === "done") {
        notification.success({
          message: i18n("UploadReferenceFile.success", info.file.name),
        });
        referenceFileUploadComplete(
          launchDispatch,
          info.file.name,
          info.file.response.files[0].id
        );
        form.setFieldsValue({ reference: info.file.response.files[0].id });
      } else if (status === "error") {
        notification.error({
          message: i18n("ReferenceFile.uploadFileError", info.file.name),
          description: response.error,
        });
      }
    },
  };

  return (
    <DragUpload
      uploadText={i18n("UploadReferenceFile.upload-text")}
      uploadHint={i18n("UploadReferenceFile.hint")}
      options={options}
      props={{ className: "t-upload-reference" }}
    />
  );
}
