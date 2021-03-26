import React from "react";
import { Button, Empty, Select, Space, Table } from "antd";
import { useTableSelect } from "../../../hooks";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useSelector } from "react-redux";
import { SPACE_MD } from "../../../styles/spacing";
import { IconFolder } from "../../../components/icons/Icons";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export function MetadataFieldsListManager({ projectId }) {
  const { canManage } = useSelector((state) => state.project);
  const { fields, loading } = useSelector((state) => state.fields);

  const [{ selected, selectedItems }, { setSelected }] = useTableSelect(fields);

  const restrictions = [
    {
      label: "RESTRICTED",
      value: "RESTRICTED",
    },
    {
      label: "SECRET",
      value: "SECRET",
    },
    {
      label: "NONE",
      value: "NONE",
    },
  ];

  const columns = [
    {
      title: i18n("MetadataField.label"),
      dataIndex: "label",
      key: "label",
      className: "t-m-field-label",
    },
    {
      title: i18n("MetadataField.type"),
      dataIndex: "type",
      key: "type",
    },
    {
      title: "__Restriction [help]",
      dataIndex: "restriction",
      key: "restriction",
      render() {
        return (
          <Select
            defaultValue={"RESTRICTED"}
            style={{ width: `100%` }}
            options={restrictions}
          />
        );
      },
    },
  ];

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      {canManage && (
        <Space>
          <MetadataTemplateCreate fields={selectedItems} projectId={projectId}>
            <Button
              className="t-create-template"
              disabled={selected.length === 0}
            >
              {i18n("MetadataFieldsList.create")}
            </Button>
          </MetadataTemplateCreate>
        </Space>
      )}
      <Table
        loading={loading}
        pagination={false}
        rowClassName={() => `t-m-field`}
        rowSelection={
          canManage
            ? { selectedRowKeys: selected, onChange: setSelected }
            : false
        }
        locale={{
          emptyText: (
            <Empty
              description={i18n("MetadataFieldsList.empty")}
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
          ),
        }}
        scroll={{ y: 800 }}
        dataSource={fields}
        columns={columns}
      />
    </Space>
  );
}
