import React from "react";
import { Button, Space, Table } from "antd";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useSelector } from "react-redux";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export function MetadataFields() {
  const { fields, loading } = useSelector((state) => state.fields);
  const [selected, setSelected] = React.useState([]);
  const [selectedFields, setSelectedFields] = React.useState([]);

  React.useEffect(() => {
    if (fields) {
      const set = new Set(selected);
      setSelectedFields(fields.filter((field) => set.has(field.key)));
    }
  }, [fields, selected]);

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Space>
        <Button>Add New Field</Button>
        <MetadataTemplateCreate fields={selectedFields}>
          <Button disabled={selected.length === 0}>
            {i18n("MetadataTemplates.create")}
          </Button>
        </MetadataTemplateCreate>
      </Space>
      <Table
        loading={loading}
        pagination={false}
        rowSelection={{ selectedRowKeys: selected, onChange: setSelected }}
        scroll={{ y: 800 }}
        dataSource={fields}
        columns={[
          {
            title: i18n("MetadataTemplate.table.field"),
            dataIndex: "label",
            key: "label",
          },
          {
            title: i18n("MetadataTemplate.table.type"),
            dataIndex: "type",
            key: "text",
          },
          window.project.canManage
            ? {
                title: i18n("MetadataTemplate.table.permissions"),
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
  );
}
