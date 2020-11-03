import React from "react";
import { message, Upload } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconCloudUpload } from "../../../components/icons/Icons";
import { useLaunchDispatch } from "../launch-context";

const { Dragger } = Upload;

export function UploadReferenceFile() {
  const { dispatchReferenceFileUploaded } = useLaunchDispatch();

  const options = {
    multiple: true,
    showUploadList: false,
    action: setBaseUrl(`/ajax/references/add`),
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
    <Dragger {...options}>
      <p className="ant-upload-drag-icon">
        <IconCloudUpload />
      </p>
      <p className="ant-upload-text">
        {i18n("UploadReferenceFile.upload-text")}
      </p>
      <p className="ant-upload-hint">{i18n("UploadReferenceFile.hint")}</p>
    </Dragger>
  );
}
