import React from "react";
import { Button, Space, Table } from "antd";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useSelector } from "react-redux";
import { SPACE_MD } from "../../../styles/spacing";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export function MetadataFieldsList({ projectId }) {
  const { canManage } = useSelector((state) => state.project);
  const { fields, loading } = useSelector((state) => state.fields);

  const [selected, setSelected] = React.useState([]);
  const [selectedFields, setSelectedFields] = React.useState([]);

  React.useEffect(() => {
    /*
    When fields are selected, Ant Table only has the key, here we are setting
    the selected fields as the entire field value.
     */
    if (fields && selected.length) {
      const set = new Set(selected);
      setSelectedFields(fields.filter((field) => set.has(field.key)));
    }
  }, [fields, selected]);

  const columns = [
    {
      title: i18n("MetadataField.label"),
      dataIndex: "label",
      key: "label",
    },
    {
      title: i18n("MetadataField.type"),
      dataIndex: "type",
      key: "text",
    },
  ];

  if (canManage) {
    columns.push({
      title: i18n("MetadataField.permissions"),
      dataIndex: "type",
      key: "permissions",
      render() {
        return "All";
      },
    });
  }

  return (
    <Space
      direction="vertical"
      style={{ display: "block", marginTop: SPACE_MD }}
    >
      {canManage && (
        <Space>
          <MetadataTemplateCreate fields={selectedFields} projectId={projectId}>
            <Button disabled={selected.length === 0}>
              {i18n("MetadataFieldsList.create")}
            </Button>
          </MetadataTemplateCreate>
        </Space>
      )}
      <Table
        loading={loading}
        pagination={false}
        rowSelection={
          canManage
            ? { selectedRowKeys: selected, onChange: setSelected }
            : false
        }
        scroll={{ y: 800 }}
        dataSource={fields}
        columns={columns}
      />
    </Space>
  );
}
