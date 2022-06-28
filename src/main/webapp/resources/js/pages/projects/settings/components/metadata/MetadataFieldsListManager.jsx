import { Button, Empty, notification, Select, Space, Table } from "antd";
import React from "react";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
  useUpdateProjectMetadataFieldRestrictionMutation,
} from "../../../../../apis/metadata/field";
import { HelpPopover } from "../../../../../components/popovers";
import { useTableSelect } from "../../../../../hooks";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useParams } from "react-router-dom";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @param {number} projectId - Identifier for the current project
 * @returns {JSX.Element|string}
 */
export default function MetadataFieldsListManager() {
  const { projectId } = useParams();
  const [restrictions, setRestrictions] = React.useState([]);
  const {
    data: fields,
    isLoading,
    refetch: refetchFields,
  } = useGetMetadataFieldsForProjectQuery(projectId);
  const [
    updateProjectMetadataFieldRestriction,
  ] = useUpdateProjectMetadataFieldRestrictionMutation();

  const [{ selected, selectedItems }, { setSelected }] = useTableSelect(fields);

  React.useEffect(() => {
    getMetadataRestrictions()
      .then(setRestrictions)
      .catch((message) => notification.error(message));
  }, []);

  const changeFieldRestriction = (field, restriction) => {
    updateProjectMetadataFieldRestriction({
      projectId,
      fieldId: field.id,
      projectRole: restriction,
    }).then(({ data }) => {
      notification.success({ message: data.message });
      refetchFields();
    });
  };

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
      className: "t-m-field-type",
    },
    {
      title: (
        <span>
          {i18n("MetadataFieldsListManager.restrictions")}
          <HelpPopover
            content={
              <div>{i18n("MetadataFieldsListManager.restrictions-help")}</div>
            }
          />
        </span>
      ),
      dataIndex: "restriction",
      key: "restriction",
      render(restriction, field) {
        return (
          <Select
            className="t-field-restriction"
            onChange={(value) => changeFieldRestriction(field, value)}
            style={{ width: `100%` }}
            options={restrictions}
            value={restriction}
          />
        );
      },
    },
  ];

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Space>
        <MetadataTemplateCreate fields={selectedItems} projectId={projectId}>
          <Button
            className="t-create-template"
            disabled={selected.length === 0}
          >
            {i18n("CreateMetadataTemplate.title")}
          </Button>
        </MetadataTemplateCreate>
      </Space>
      <Table
        loading={isLoading}
        pagination={false}
        rowClassName={() => `t-m-field`}
        rowSelection={{ selectedRowKeys: selected, onChange: setSelected }}
        locale={{
          emptyText: (
            <Empty
              description={i18n("MetadataFieldsListMember.empty")}
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
