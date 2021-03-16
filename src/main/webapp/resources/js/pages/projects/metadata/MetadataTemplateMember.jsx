import React from "react";
import { useSelector } from "react-redux";
import { navigate } from "@reach/router";
import { List, PageHeader, Skeleton, Table, Typography } from "antd";
import { addKeysToList } from "../../../utilities/http-utilities";

const { Text } = Typography;

export function MetadataTemplateMember({ id }) {
  const { templates, loading } = useSelector((state) => state.templates);
  const [template, setTemplate] = React.useState({});

  React.useEffect(() => {
    if (templates !== undefined) {
      if (templates.length === 0) {
        navigate(`../templates`);
      } else {
        const found = templates.find((template) => template.identifier === id);
        if (found) {
          setTemplate(found);
        }
        // Need to go to templates listing if template not found
      }
    }
  }, [id, templates]);

  return (
    <PageHeader title={template.name} onBack={() => navigate("./")}>
      <Skeleton loading={loading}>
        <List itemLayout="vertical" size="small">
          <List.Item>
            <List.Item.Meta
              title={<Text strong>Name</Text>}
              description={template.name}
            />
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={<Text strong>Description</Text>}
              description={template.description || ""}
            />
          </List.Item>
          <List.Item>
            <List.Item.Meta
              title={<Text strong>Metadata Fields</Text>}
              description={
                <Table
                  pagination={false}
                  columns={[
                    { title: "Label", dataIndex: "label" },
                    { title: "Type", dataIndex: "type" },
                  ]}
                  dataSource={addKeysToList(template.fields || [], "field")}
                />
              }
            />
          </List.Item>
        </List>
      </Skeleton>
    </PageHeader>
  );
}
