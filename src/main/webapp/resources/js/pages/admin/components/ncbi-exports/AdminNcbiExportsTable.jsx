import React from "react";
import { PagedTable } from "../../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import ExportUploadStateTag from "../../../../components/ncbi/ExportUploadStateTag";

/**
 * React component for rendering a table to display NCBI Exports in the
 * IRIDA system.
 * @returns {JSX.Element|string}
 * @constructor
 */
export default function AdminNcbiExportsTable() {
  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id, item) {
        return (
          <a href={setBaseUrl(`/projects/${item.project.id}/export/${id}`)}>
            {item.bioProjectId}
          </a>
        );
      },
    },
    {
      title: i18n("NcbiExportTable.exportedSamples"),
      dataIndex: "exportedSamples",
    },
    {
      title: i18n("NcbiExportTable.state"),
      dataIndex: "state",
      render: (state) => <ExportUploadStateTag state={state} />,
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
