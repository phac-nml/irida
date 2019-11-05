import React, { useContext } from "react";
import "../../vendor/datatables/datatables";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { Button, Table } from "antd";
import { PagedTableContext } from "../../contexts/PagedTableContext";

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
      title: i18n("sequencingruns.id"),
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
      title: i18n("sequencingruns.type"),
      dataIndex: "sequencerType"
    },
    {
      title: i18n("sequencingruns.uploadStatus"),
      dataIndex: "uploadStatus"
    },
    {
      title: i18n("sequencingruns.uploadUser"),
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
      title: i18n("sequencingruns.createdDate"),
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
