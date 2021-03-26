import React from "react";
import { Button, Empty, Space, Table } from "antd";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useSelector } from "react-redux";
import { SPACE_MD } from "../../../styles/spacing";
import { IconFolder } from "../../../components/icons/Icons";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export function MetadataFieldsListMember({ projectId }) {
  const { fields, loading } = useSelector((state) => state.fields);
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
      key: "text",
    },
  ];

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Table
        loading={loading}
        pagination={false}
        rowClassName={() => `t-m-field`}
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
