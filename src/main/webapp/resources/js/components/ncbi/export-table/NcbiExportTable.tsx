import React from "react";
import { Table } from "antd";
import {getProjectNCBIExports, NcbiExportSubmissionTableModel} from "../../../apis/export/ncbi";
import {
  formatInternationalizedDateTime
} from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import NcbiUploadStates from "../upload-states";
import {
  getPaginationOptions
} from "../../../utilities/antdesign-table-utilities";
import {Link, useParams} from "react-router-dom";
import NcbiUploadStateTag from "../ExportUploadStateTag/NcbiUploadStateTag";
import {ExportUploadState, NcbiSubmission, User, UserMinimal} from "../../../types/irida";

/**
 * Render a list of all Project NCBI Exports.
 * @returns {JSX.Element|string}
 * @constructor
 */
export function NcbiExportTable(): JSX.Element {
  const {projectId} = useParams();

  const [exports, setExports] = React.useState<NcbiExportSubmissionTableModel[] | undefined>(undefined);
  const [total, setTotal] = React.useState<number>(0);

  React.useEffect(() => {
    if (projectId) {
      getProjectNCBIExports(parseInt(projectId)).then((data) => {
        setExports(data);
        setTotal(data.length);
      });
    }
  }, [projectId]);

  const columns = [
    {
      title: i18n("NcbiExportTable.id"),
      dataIndex: "id",
      render(id:number, item: NcbiExportSubmissionTableModel) {
        return (
          <Link
            className="t-biosample-id"
            to={`${id}`}
          >
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
      render: (state:ExportUploadState) => <NcbiUploadStateTag state={state} />,
    },
    {
      title: i18n("NcbiExportTable.submitter"),
      dataIndex: "submitter",
      render(submitter: UserMinimal) {
        return (
          <a href={setBaseUrl(`/users/${submitter.id}`)}>{submitter.name}</a>
        );
      },
    },
    {
      title: i18n("NcbiExportTable.createdDate"),
      dataIndex: "createdDate",
      render(date : Date) {
        return formatInternationalizedDateTime(date);
      },
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={exports}
      rowKey={(item) => item.bioProjectId}
      pagination={getPaginationOptions(total)}
    />
  );
}
