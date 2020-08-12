import React from "react";
import { message, Upload } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconCloudUpload } from "../icons/Icons";

const { Dragger } = Upload;

export function UploadReferenceFile({ afterReferenceUpload }) {
  const options = {
    multiple: true,
    action: setBaseUrl(`referenceFiles/new`),
    onChange(info) {
      const { status } = info.file;
      if (status !== "uploading") {
        // console.log("NOT UPLOADING", info.file, info.fileList);
      }
      if (status === "done") {
        // console.log(`DONE`, info);
        message.success(`${info.file.name} file uploaded successfully.`);
        afterReferenceUpload({
          name: info.file.name,
          id: info.file.response["uploaded-file-id"],
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
        Click or drag file to this area to upload
      </p>
      <p className="ant-upload-hint">
        Support for a single or bulk upload. Strictly prohibit from uploading
        company data or other band files
      </p>
    </Dragger>
  );
}
