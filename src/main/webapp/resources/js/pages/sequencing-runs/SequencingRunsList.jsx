import React, { useContext } from "react";
import "../../vendor/datatables/datatables";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { Button, Table } from "antd";
import { PagedTableContext } from "../../contexts/PagedTableContext";
import { getI18N } from "../../utilities/i18n-utilities";

export function SequencingRunsList() {
  const {
    loading,
    total,
    pageSize,
    dataSource,
    handleTableChange
  } = useContext(PagedTableContext);

  const columns = [
    {
      dataIndex: "id",
      title: getI18N("sequencingruns.id"),
      sorter: true,
      render: text => {
        return (
          <Button
            type="link"
            className="t-run-link"
            href={`${window.TL.BASE_URL}sequencingRuns/${text}`}
          >
            {text}
          </Button>
        );
      }
    },
    {
      title: getI18N("sequencingruns.type"),
      dataIndex: "sequencerType"
    },
    {
      title: getI18N("sequencingruns.uploadStatus"),
      dataIndex: "uploadStatus"
    },
    {
      title: getI18N("sequencingruns.uploadUser"),
      dataIndex: "user",
      sorter: true,
      render: text => {
        if (!text) {
          return null;
        }
        return (
          <Button
            type="link"
            href={`${window.TL.BASE_URL}users/${text.identifier}`}
          >
            {text.label}
          </Button>
        );
      }
    },
    {
      ...dateColumnFormat(),
      title: getI18N("sequencingruns.createdDate"),
      dataIndex: "createdDate"
    }
  ];

  return (
    <Table
      scroll={{ x: "max-content" }}
      rowKey={record => record.id}
      pagination={{ total, pageSize }}
      loading={loading}
      columns={columns}
      dataSource={dataSource}
      onChange={handleTableChange}
    />
  );
}
