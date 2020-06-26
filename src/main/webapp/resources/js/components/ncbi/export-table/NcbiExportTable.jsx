import React, { useEffect, useState } from "react";
import { Table } from "antd";
import { getNCBIExports } from "../../../apis/ncbi/ncbi";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import NcbiUploadStates from "../upload-states";

export function NcbiExportTable({ url }) {
  const [exports, setExports] = useState(null);

  useEffect(() => {
    getNCBIExports({ url }).then(setExports);
  }, [url]);

  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id, item) {
        return (
          <a className="t-biosample-id" href={`${window.location.href}/${id}`}>
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
