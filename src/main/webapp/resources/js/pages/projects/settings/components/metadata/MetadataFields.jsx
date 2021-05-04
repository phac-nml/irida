import { Button, Empty, Space, Table } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchFieldsForProject } from "../../../redux/fieldsSlice";
import { fetchTemplatesForProject } from "../../../redux/templatesSlice";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export default function MetadataFields({ projectId }) {
  const { canManage } = useSelector((state) => state.project);
  const dispatch = useDispatch();
  const { fields, loading } = useSelector((state) => state.fields);

  const [selected, setSelected] = React.useState([]);
  const [selectedFields, setSelectedFields] = React.useState([]);

  React.useEffect(() => {
    dispatch(fetchFieldsForProject(projectId));
    dispatch(fetchTemplatesForProject(projectId));
  }, [dispatch, projectId]);

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
      {/*<Typography.Title level={2}>*/}
      {/*  {i18n("MetadataFields.title")}*/}
      {/*</Typography.Title>*/}
      {canManage && (
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
