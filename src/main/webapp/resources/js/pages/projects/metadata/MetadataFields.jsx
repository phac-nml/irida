import React from "react";
import { Button, PageHeader, Space, Table } from "antd";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useSelector } from "react-redux";

export function MetadataFields({}) {
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
    <PageHeader title={i18n("MetadataFields.title")}>
      <Space direction="vertical" style={{ display: "block" }}>
        <div>
          <MetadataTemplateCreate fields={selectedFields}>
            <Button>{i18n("MetadataTemplates.create")}</Button>
          </MetadataTemplateCreate>
        </div>
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
    </PageHeader>
  );
}
