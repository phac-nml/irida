import React, { useEffect, useState } from "react";
import { Table, Tag } from "antd";
import { getNCBIExports } from "../../../apis/ncbi/ncbi";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";

const ExportState = ({ state }) => {
  switch (state) {
    case "NEW":
      return <Tag color="blue">{state}</Tag>;
    case "UPLOADED":
      return <Tag color="green">{state}</Tag>;
    case "UPLOADED_ERROR":
      return <Tag color="red">{state}</Tag>;
    default:
      return <Tag>{state}</Tag>;
  }
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
      render: (state) => <ExportState state={state} />,
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
