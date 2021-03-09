import React from "react";
import { Button, PageHeader, Space, Table } from "antd";
import { getMetadataFieldsForProject } from "../../../apis/metadata/field";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";

export function MetadataFields({ projectId }) {
  const [fields, setFields] = React.useState();
  const [selected, setSelected] = React.useState([]);
  const [selectedFields, setSelectedFields] = React.useState([]);

  React.useEffect(() => {
    getMetadataFieldsForProject(projectId).then((data) => {
      setFields(data.map((f) => ({ ...f, key: `field-${f.id}` })));
    });
  }, [projectId]);

  React.useEffect(() => {
    if (fields) {
      const set = new Set(selected);
      setSelectedFields(fields.filter((field) => set.has(field.key)));
    }
  }, [selected]);

  return (
    <PageHeader title={"METADATA FIELDS"}>
      <Space direction="vertical" style={{ display: "block" }}>
        <div>
          <MetadataTemplateCreate fields={selectedFields}>
            <Button>Create New Template</Button>
          </MetadataTemplateCreate>
        </div>
        <Table
          pagination={false}
          rowSelection={{ selectedRowKeys: selected, onChange: setSelected }}
          scroll={{ y: 800 }}
          dataSource={fields}
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
        />
      </Space>
    </PageHeader>
  );
}
