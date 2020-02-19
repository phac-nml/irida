import React from "react";
import "../../vendor/datatables/datatables";
import { dateColumnFormat } from "../../components/ant.design/table-renderers";
import { Button } from "antd";
import { PagedTable } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";

export function SequencingRunsList() {
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
            href={setBaseUrl(`sequencingRuns/${text}`)}
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
          <Button type="link" href={setBaseUrl(`users/${text.identifier}`)}>
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
    <PagedTable
      search={false}
      scroll={{ x: "max-content" }}
      rowKey={record => record.id}
      columns={columns}
    />
  );
}
