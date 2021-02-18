import React from "react";
import { Button, notification, PageHeader, Space, Typography } from "antd";
import { navigate } from "@reach/router";
import {
  getMetadataTemplate,
  updateMetadataTemplate,
} from "../../../apis/metadata/metadata-templates";
import DnDTable from "../../../components/ant.design/DnDTable";

const { Paragraph, Text } = Typography;

const columns = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
  },
  {
    title: "Age",
    dataIndex: "age",
    key: "age",
  },
  {
    title: "Address",
    dataIndex: "address",
    key: "address",
  },
];

const dataSource = [
  {
    key: "1",
    name: "John Brown",
    age: 32,
    address: "New York No. 1 Lake Park",
    index: 0,
  },
  {
    key: "2",
    name: "Jim Green",
    age: 42,
    address: "London No. 1 Lake Park",
    index: 1,
  },
  {
    key: "3",
    name: "Joe Black",
    age: 32,
    address: "Sidney No. 1 Lake Park",
    index: 2,
  },
];

export function MetadataTemplate({ id }) {
  const [template, setTemplate] = React.useState({});

  const [data, setData] = React.useState(dataSource);

  React.useEffect(() => {
    getMetadataTemplate(id).then((data) => setTemplate(data));
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
      <Space direction="vertical" style={{ width: `100%` }}>
        <Text strong>Name</Text>
        <Paragraph editable={{ onChange: (text) => onChange("name", text) }}>
          {template.name}
        </Paragraph>
        <Text strong>Description</Text>
        <Paragraph
          editable={{ onChange: (text) => onChange("description", text) }}
        >
          {template.description || ""}
        </Paragraph>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <Text strong>Metadata Fields</Text>
          <Button>Add New Field</Button>
        </div>
        <DnDTable
          data={data}
          columns={columns}
          onRowUpdate={(data) => setData(data)}
        />
      </Space>
    </PageHeader>
  );
}
