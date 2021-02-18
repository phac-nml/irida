import React from "react";
import { List, notification, PageHeader, Typography } from "antd";
import { navigate } from "@reach/router";
import {
  getMetadataTemplate,
  updateMetadataTemplate,
} from "../../../apis/metadata/metadata-templates";
import DnDTable from "../../../components/ant.design/DnDTable";
import { MetadataFieldCreate } from "./MetadataFieldCreate";

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
      <List itemLayout="vertical">
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
                <MetadataFieldCreate />
              </div>
            }
          />
          <DnDTable
            data={data}
            columns={columns}
            onRowUpdate={(data) => setData(data)}
          />
        </List.Item>
      </List>
    </PageHeader>
  );
}
