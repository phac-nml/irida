import { Table } from "antd";
import React from "react";
import { Link } from "react-router-dom";
import { getProjectNCBIExports } from "../../../apis/ncbi/ncbi";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import ExportUploadStateTag from "../ExportUploadStateTag";

/**
 * Render a list of all Project NCBI Exports.
 * @returns {JSX.Element|string}
 * @constructor
 */
export function NcbiExportTable() {
  const [exports, setExports] = React.useState(null);
  const [total, setTotal] = React.useState(0);

  React.useEffect(() => {
    getProjectNCBIExports().then((data) => {
      setExports(data);
      setTotal(data.length);
    });
  }, []);

  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id, item) {
        return (
          <Link className="t-biosample-id" to={`${id}`}>
            {item.bioProjectId}
          </Link>
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
    <Table
      columns={columns}
      dataSource={exports}
      rowKey={(item) => item.id}
      pagination={getPaginationOptions(total)}
    />
  );
}
