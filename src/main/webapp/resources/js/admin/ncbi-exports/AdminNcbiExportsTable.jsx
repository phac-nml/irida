import React from "react";
import { PagedTable } from "../../components/ant.design/PagedTable";
import NcbiUploadStates from "../../components/ncbi/upload-states";
import { setBaseUrl } from "../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";

export default function AdminNcbiExportsTable() {
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
      render: (state) => <NcbiUploadStates state={state} />,
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

  return <PagedTable columns={columns} search={false} />;
}
