import React, { useState } from "react";
import { navigate } from "@reach/router"
import {
  notification,
  Space,
  Typography,
} from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { SampleMetadataImportSteps } from "./SampleMetadataImportSteps";

const { Text, Title } = Typography

/**
 * React component that displays Step #1 of the Sample Metadata Uploader.
 * This page is where the user selects the file to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportFileUploader({ projectId }) {

  const options = {
    multiple: false,
    showUploadList: false,
    accept: [".xls", ".xlsx", ".csv"],
    action: setBaseUrl(`/projects/${projectId}/sample-metadata/upload/file`),
    onChange(info) {
      const { status } = info.file;
      if (status === 'done') {
        notification.success({
          message: i18n("SampleMetadataImportFileUploader.success", info.file.name),
        });
        navigate('headers')
      } else if (status === 'error') {
        notification.error({
          message: i18n("SampleMetadataImportFileUploader.error", info.file.name),
        });
      }
    },
  }

  return (
    <>
      <Space direction="vertical" size="large" style={{ width: `100%` }}>
        <Title level={3}>{i18n("SampleMetadataImportFileUploader.title")}</Title>
        <Text type="secondary">
          {i18n("SampleMetadataImportFileUploader.intro")}
        </Text>
        <SampleMetadataImportSteps currentStep={0} />
        <DragUpload
          className="t-sample-metadata-file-uploader"
          uploadText={i18n("SampleMetadataImportFileUploader.dropzone")}
          uploadHint={<Text strong>{i18n("SampleMetadataImportFileUploader.warning")}</Text>}
          options={options}
        />
      </Space>
    </>
  );
}
