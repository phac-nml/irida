import React, { useState } from "react";
import {
  Alert,
  Divider,
  notification,
  Space,
  Typography,
} from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";

export function SampleMetadataImportFileUploader() {
  const { Text, Title, Paragraph } = Typography
  const options = {
    multiple: false,
    showUploadList: false,
    accept: [".xls", ".xlsx", ".csv"],
    action: setBaseUrl(`/projects/${window.project.id}/sample-metadata/upload/file`),
    onChange(info) {
      const { status } = info.file;
      if (status !== 'uploading') {
        console.log(info.file);
      }
      if (status === 'done') {
        notification.success({
          message: i18n("SampleMetadataImportFileUploader.success", info.file.name),
        });
      } else if (status === 'error') {
        notification.error({
          message: i18n("SampleMetadataImportFileUploader.error", info.file.name),
        });
      }
    },
  }
  const [step, setStep] = useState(0);

  return (
    <>
      <SampleMetadataImportSteps currentStep={step} />
      <Divider />
      <Space direction="vertical">
        <Typography>
          <Title level={3}>{i18n("SampleMetadataImportFileUploader.title")}</Title>
          <Paragraph>{i18n("SampleMetadataImportFileUploader.intro")}</Paragraph>
          <Alert
            message={
              <Space direction="vertical">
                <Text>{i18n("SampleMetadataImportFileUploader.text")}</Text>
                <Text strong={true}>{i18n("SampleMetadataImportFileUploader.warning")}</Text>
              </Space>
            }
            type="info"
          />
        </Typography>
        <DragUpload
          className="t-sample-metadata-file-uploader"
          uploadText={i18n("SampleMetadataImportFileUploader.dropzone")}
          options={options}
        />
      </Space>
    </>
  );
}
