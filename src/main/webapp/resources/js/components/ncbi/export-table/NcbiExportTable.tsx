import React from "react";
import { Table } from "antd";
import {
  getProjectNCBIExports,
  NcbiExportSubmissionTableModel,
} from "../../../apis/export/ncbi";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import { Link, useLoaderData } from "react-router-dom";
import NcbiUploadStateTag from "../ExportUploadStateTag/NcbiUploadStateTag";
import { UserMinimal } from "../../../types/irida";
import { ExportUploadState } from "../../../types/irida/export/ExportUploadState";
import { DataFunctionArgs } from "@remix-run/router/utils";

/**
 * React router data loader (https://beta.reactrouter.com/en/dev/route/loader)
 * Fetches a list of all NCBI SRA Exported submissions for this project.
 * @param params
 */
export async function loader({
  params,
}: DataFunctionArgs): Promise<NcbiExportSubmissionTableModel[]> {
  if (params.projectId) {
    return getProjectNCBIExports(parseInt(params.projectId));
  } else {
    return Promise.reject(i18n("NcbiExportTable.loader-error"));
  }
}

/**
 * Render a list of all Project NCBI Exports.
 * @constructor
 */
function NcbiExportTable(): JSX.Element {
  const exports = useLoaderData();

  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id: number, item: NcbiExportSubmissionTableModel) {
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
      render: (state: ExportUploadState) => (
        <NcbiUploadStateTag state={state} />
      ),
    },
    {
      title: i18n("NcbiExportTable.submitter"),
      dataIndex: "submitter",
      render(submitter: UserMinimal) {
        return submitter.name;
      },
    },
    {
      title: i18n("NcbiExportTable.createdDate"),
      dataIndex: "createdDate",
      render(date: Date) {
        return formatInternationalizedDateTime(date);
      },
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={exports}
      rowKey={(item) => item.bioProjectId}
      pagination={getPaginationOptions(exports.length)}
    />
  );
}

export default NcbiExportTable;
