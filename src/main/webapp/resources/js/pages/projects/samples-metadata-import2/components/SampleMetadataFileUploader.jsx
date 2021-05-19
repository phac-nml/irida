import React from "react";
import {
  Alert,
  Space,
  Typography,
} from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";

export function SampleMetadataFileUploader() {
  const { Text, Title, Paragraph } = Typography
  const options = {
    multiple: true,
    showUploadList: false,
  }

  return (
    <>
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
