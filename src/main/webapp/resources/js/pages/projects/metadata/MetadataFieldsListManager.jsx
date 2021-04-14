import { unwrapResult } from "@reduxjs/toolkit";
import { Button, Empty, notification, Select, Space, Table } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { HelpPopover } from "../../../components/popovers";
import { useTableSelect } from "../../../hooks";
import {
  fetchFieldsRestrictions,
  updateProjectFieldRestriction,
} from "../redux/fieldsSlice";
import { MetadataTemplateCreate } from "./MetadataTemplateCreate";

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

  const changeFieldRestriction = (field, restriction) => {
    dispatch(
      updateProjectFieldRestriction({
        projectId,
        fieldId: field.id,
        projectRole: restriction,
      })
    )
      .then(unwrapResult)
      .then(({ message }) => notification.success({ message }));
  };

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
      {canManage && (
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
