import React, { useEffect, useState } from "react";
import { Table } from "antd";
import { getProjectNCBIExports } from "../../../apis/ncbi/ncbi";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import NcbiUploadStates from "../upload-states";

/**
 * Render a list of all Project NCBI Exports.
 * @returns {JSX.Element|string}
 * @constructor
 */
export function NcbiExportTable() {
  const [exports, setExports] = useState(null);

  useEffect(() => {
    getProjectNCBIExports().then(setExports);
  }, []);

  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id, item) {
        return (
          <a
            className="t-biosample-id"
            href={setBaseUrl(`/projects/${window.project.id}/export/${id}`)}
          >
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
      render: (state) => <NcbiUploadStates state={state} />,
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
