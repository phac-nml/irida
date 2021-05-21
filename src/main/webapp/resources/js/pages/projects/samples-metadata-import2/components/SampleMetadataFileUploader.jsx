import React from "react";
import {
  Alert,
  Divider,
  message,
  Space,
  Typography,
} from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { SampleMetadataSteps } from "./SampleMetadataSteps";

export function SampleMetadataFileUploader() {
  const { Text, Title, Paragraph } = Typography
  const options = {
    multiple: false,
    showUploadList: true,
    accept: [".xls", ".xlsx", ".csv"],
    // action: setBaseUrl(`/file`),
    onChange(info) {
      const { status } = info.file;
      if (status !== 'uploading') {
        console.log(info.file);
      }
      if (status === 'done') {
        message.success(`${info.file.name} file uploaded successfully.`);
      } else if (status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
  }

  return (
    <>
      <SampleMetadataSteps />
      <Divider />
      <Space direction="vertical">
        <Typography>
          <Title level={3}>{i18n("metadata.upload.component.title")}</Title>
          <Paragraph>{i18n("metadata.upload.component.intro")}</Paragraph>
          <Alert
            message={
              <Space direction="vertical">
                <Text>{i18n("metadata.upload.component.text")}</Text>
                <Text strong={true}>{i18n("metadata.upload.component.warning")}</Text>
              </Space>
            }
            type="info"
          />
        </Typography>
        <DragUpload
          className="t-sample-metadata-file-uploader"
          uploadText={i18n("metadata.dropzone.message")}
          options={options}
        />
      </Space>
    </>
  );
}
