import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import {
  PagedTable,
  PagedTableProvider,
} from "../../../components/ant.design/PagedTable";
import {
  dateColumnFormat
} from "../../../components/ant.design/table-renderers";
import { Button } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/sequencingRuns`);

/**
 * React component to display the sequencing run list page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunListPage() {
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

  return (
    <PageWrapper title={i18n("SequencingRunListPage.title")}>
      <PagedTableProvider url={`${URL}/list`}>
        <PagedTable
          search={false}
          scroll={{x: "max-content"}}
          rowKey={(record) => record.id}
          columns={columns}
        />
      </PagedTableProvider>
    </PageWrapper>
  );
}
