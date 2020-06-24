import React, { useEffect, useState } from "react";
import { Table, Tag } from "antd";
import { getNCBIExports } from "../../../apis/ncbi/ncbi";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";

const NcbiExportUploadState = ({ state }) => {
  const states = {
    NEW: {
      color: "blue",
      text: i18n("NcbiExportUploadState.NEW"),
    },
    UPLOADING: {
      color: "blue",
      text: i18n("NcbiExportUploadState.UPLOADING"),
    },
    UPLOADED: {
      color: "green",
      text: i18n("NcbiExportUploadState.UPLOADED"),
    },
    UPLOAD_ERROR: {
      color: "red",
      text: i18n("NcbiExportUploadState.UPLOAD_ERROR"),
    },
    created: {
      text: i18n("NcbiExportUploadState.created"),
    },
    failed: {
      color: "red",
      text: i18n("NcbiExportUploadState.failed"),
    },
    queued: {
      text: i18n("NcbiExportUploadState.queued"),
    },
    processing: {
      text: i18n("NcbiExportUploadState.processing"),
    },
    "processed-ok": {
      text: i18n("NcbiExportUploadState.processed-ok"),
    },
    "processed-error": {
      color: "red",
      text: i18n("NcbiExportUploadState.processed-error"),
    },
    waiting: {
      text: i18n("NcbiExportUploadState.waiting"),
    },
    submitted: {
      text: i18n("NcbiExportUploadState.submitted"),
    },
  };
  return states[state] ? (
    <Tag color={states[state].color}>{states[state].text}</Tag>
  ) : (
    <Tag>{i18n("NcbiExportUploadState.unknown")}</Tag>
  );
};

export function NcbiExportTable({ url }) {
  const [exports, setExports] = useState(null);

  useEffect(() => {
    getNCBIExports({ url }).then(setExports);
  }, [url]);

  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id) {
        return <a href={`${window.location.href}/${id}`}>{id}</a>;
      },
    },
    {
      title: i18n("NcbiExportTable.exportedSamples"),
      dataIndex: "exportedSamples",
    },
    {
      title: i18n("NcbiExportTable.state"),
      dataIndex: "state",
      render: (state) => <NcbiExportUploadState state={state} />,
    },
    {
      title: i18n("NcbiExportTable.project"),
      dataIndex: "project",
      render(project) {
        return (
          <a href={setBaseUrl(`/projects/${project.id}`)}>{project.name}</a>
        );
      },
    },
    {
      title: i18n("NcbiExportTable.submitter"),
      dataIndex: "submitter",
      render(submitter) {
        return (
          <a href={setBaseUrl(`/users/${submitter.id}`)}>{submitter.name}</a>
        );
      },
    },
    {
      title: i18n("NcbiExportTable.createdDate"),
      dataIndex: "createdDate",
      render(date) {
        return formatInternationalizedDateTime(date);
      },
    },
  ];

  return (
    <Table columns={columns} dataSource={exports} rowKey={(item) => item.id} />
  );
}
