import { Button, Empty, Space, Table } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import {
  useGetMetadataFieldsForProjectQuery
} from "../../../../../apis/metadata/field";
import {
  useGetProjectDetailsQuery
} from "../../../../../apis/projects/project";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export default function MetadataFields() {
  const { projectId } = useParams();

  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const { data: fields, isLoading } = useGetMetadataFieldsForProjectQuery(
    projectId
  );

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
      {project.canManage && (
        <Space>
          <MetadataTemplateCreate fields={selectedFields} projectId={projectId}>
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
        loading={isLoading}
        pagination={false}
        rowClassName={() => `t-m-field`}
        rowSelection={
          project.canManage
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
