import React from "react";
import { Button, List, notification, PageHeader, Typography } from "antd";
import { navigate } from "@reach/router";
import {
  getMetadataTemplate,
  updateMetadataTemplate,
} from "../../../apis/metadata/metadata-templates";
import DnDTable from "../../../components/ant.design/DnDTable";

const { Paragraph, Text } = Typography;

export function MetadataTemplate({ id }) {
  const [template, setTemplate] = React.useState({});
  const [fields, setFields] = React.useState([]);

  React.useEffect(() => {
    getMetadataTemplate(id).then(({ fields: newFields, ...newTemplate }) => {
      setFields(newFields);
      setTemplate(newTemplate);
    });
  }, []);

  const onChange = async (field, text) => {
    if (template[field] !== text) {
      const updated = { ...template, [field]: text };
      try {
        const message = await updateMetadataTemplate(updated);
        notification.success({ message });
        setTemplate(updated);
      } catch (e) {}
    }
  };

  return (
    <PageHeader title={template.name} onBack={() => navigate("./")}>
      <List itemLayout="vertical" size="small">
        <List.Item>
          <List.Item.Meta
            title={<Text strong>Name</Text>}
            description={
              <Paragraph
                editable={{ onChange: (text) => onChange("name", text) }}
              >
                {template.name}
              </Paragraph>
            }
          />
        </List.Item>
        <List.Item>
          <List.Item.Meta
            title={<Text strong>Description</Text>}
            description={
              <Paragraph
                editable={{
                  onChange: (text) => onChange("description", text),
                }}
              >
                {template.description || ""}
              </Paragraph>
            }
          />
        </List.Item>
        <List.Item>
          <List.Item.Meta
            title={
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <Text strong>Metadata Fields</Text>
                <Button>Add New Field</Button>
              </div>
            }
          />
          <DnDTable
            data={fields}
            columns={[
              { title: "Metadata Field", dataIndex: "label", key: "label" },
              { title: "Type", dataIndex: "type", key: "text" },
              window.project.canManage
                ? {
                    title: "Permissions",
                    dataIndex: "type",
                    key: "permissions",
                    render() {
                      return "All";
                    },
                  }
                : null,
            ]}
            onRowUpdate={setFields}
          />
        </List.Item>
      </List>
    </PageHeader>
  );
}
