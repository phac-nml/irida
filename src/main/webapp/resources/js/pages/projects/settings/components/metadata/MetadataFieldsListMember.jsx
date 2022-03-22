import { Empty, Space, Table } from "antd";
import React from "react";
import { useGetMetadataFieldsForProjectQuery } from "../../../../../apis/metadata/field";
import { useParams } from "react-router-dom";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @param {number} projectId - Identifier for the current project
 * @returns {JSX.Element|string}
 */
export default function MetadataFieldsListMember() {
  const { projectId } = useParams();

  const { data: fields, isLoading } = useGetMetadataFieldsForProjectQuery(
    projectId
  );

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
      className: "t-m-field-type",
    },
  ];

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Table
        loading={isLoading}
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
