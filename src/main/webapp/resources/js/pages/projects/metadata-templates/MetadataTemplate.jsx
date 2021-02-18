import React from "react";
import { PageHeader, Space, Typography } from "antd";
import { navigate } from "@reach/router";
import { getMetadataTemplate } from "../../../apis/metadata/metadata-templates";

const { Paragraph, Text } = Typography;

export function MetadataTemplate({ id }) {
  const [template, setTemplate] = React.useState({});

  React.useEffect(() => {
    getMetadataTemplate(id).then((data) => setTemplate(data));
  }, []);

  const onChange = (field, text) => {
    console.log(field, text);
  };

  return (
    <PageHeader title={template.name} onBack={() => navigate("./")}>
      <Space direction="vertical" style={{ width: `100%` }}>
        <Text strong>Description</Text>
        <Paragraph
          editable={{ onChange: (text) => onChange("description", text) }}
        >
          {template.description || ""}
        </Paragraph>
      </Space>
    </PageHeader>
  );
}
