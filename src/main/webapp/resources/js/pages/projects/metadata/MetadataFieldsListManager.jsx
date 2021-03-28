import React from "react";
import { Button, Empty, Select, Space, Table } from "antd";
import { getMetadataRestrictions } from "../../../apis/metadata/field";
import { useTableSelect } from "../../../hooks";
import { fetchFieldsRestrictions } from "../redux/fieldsSlice";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";
import { useDispatch, useSelector } from "react-redux";
import { SPACE_MD } from "../../../styles/spacing";
import { IconFolder } from "../../../components/icons/Icons";

/**
 * Component for showing metadata fields associated with a project.
 *
 * @returns {JSX.Element|string}
 */
export function MetadataFieldsListManager({ projectId }) {
  const dispatch = useDispatch();
  const { canManage } = useSelector((state) => state.project);
  const { fields, restrictions, loading } = useSelector(
    (state) => state.fields
  );

  const [{ selected, selectedItems }, { setSelected }] = useTableSelect(fields);

  React.useEffect(() => {
    dispatch(fetchFieldsRestrictions());
  }, [dispatch]);

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
        return <Select style={{ width: `100%` }} options={restrictions} />;
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
