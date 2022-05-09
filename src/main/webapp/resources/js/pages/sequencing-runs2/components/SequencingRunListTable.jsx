import React, { useContext } from "react";
import {
  PagedTable,
  PagedTableContext,
} from "../../../components/ant.design/PagedTable";
import {
  dateColumnFormat
} from "../../../components/ant.design/table-renderers";
import { Button, Popconfirm } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import {
  useDeleteSequencingRunMutation
} from "../../../apis/sequencing-runs/sequencing-runs";

/**
 * React component to display the sequencing run list table.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunListTable() {
  const {updateTable} = useContext(PagedTableContext);
  const [deleteSequencingRun] = useDeleteSequencingRunMutation();
  const columns = [
    {
      dataIndex: "id",
      title: i18n("SequencingRunListPage.table.id"),
      sorter: true,
      render: (text) => {
        return (
          <Button
            type="link"
            className="t-run-link"
            href={setBaseUrl(`sequencing-runs2/${text}`)}
          >
            {text}
          </Button>
        );
      },
    },
    {
      title: i18n("SequencingRunListPage.table.type"),
      dataIndex: "sequencerType",
    },
    {
      title: i18n("SequencingRunListPage.table.uploadStatus"),
      dataIndex: "uploadStatus",
    },
    {
      title: i18n("SequencingRunListPage.table.uploadUser"),
      dataIndex: "user",
      sorter: true,
      render: (text) => {
        if (!text) {
          return null;
        }
        return (
          <Button type="link" href={setBaseUrl(`users/${text.identifier}`)}>
            {text.label}
          </Button>
        );
      },
    },
    {
      ...dateColumnFormat(),
      title: i18n("SequencingRunListPage.table.createdDate"),
      dataIndex: "createdDate",
    },
  ];

  if (window.TL._USER.systemRole === "ROLE_ADMIN") {
    columns.push({
      dataIndex: "actions",
      align: "right",
      fixed: "right",
      width: 100,
      render(text, record) {
        return (
          <Popconfirm
            key="remove-btn"
            title={i18n("SequencingRunListPage.button.delete.confirmation")}
            onConfirm={() => deleteSequencingRun({runId: record.id}).then(updateTable)}
          >
            <Button className="t-remove-btn" type="link" size="small">
              {i18n("SequencingRunListPage.button.delete")}
            </Button>
          </Popconfirm>
        );
      }
    });
  }

  return (
    <PagedTable
      search={false}
      scroll={{x: "max-content"}}
      rowKey={(record) => record.id}
      columns={columns}
    />
  );
}
