import React from "react";
import {
  Alert,
  Typography,
  Space
} from "antd";

const { Text, Title, Paragraph } = Typography

export function SampleMetadataFileUploader() {
  return (
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
  );
}
