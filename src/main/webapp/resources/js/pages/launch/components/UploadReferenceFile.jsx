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
        message.success(`${info.file.name} file uploaded successfully.`);
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
        Click or drag new reference file to this area to upload
      </p>
      <p className="ant-upload-hint">
        Support for a single or bulk upload. Strictly prohibit from uploading
        company data or other band files
      </p>
    </Dragger>
  );
}
