import React from "react";
import { Button, Table } from "antd";
import type { TableColumnProps } from "antd/es";
import { SampleDetailViewer } from "../../samples/SampleDetailViewer";
import useSamplesTableState from "./hooks/useSamplesTableState";
import { ProjectSample } from "../../../redux/endpoints/project-samples";
import SampleQuality from "./components/SampleQuality";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import ProjectTag from "../../../pages/search/ProjectTag";
import { useGetAssociatedProjectsQuery } from "../../../redux/endpoints/project";
import { useParams } from "react-router-dom";

/**
 * React component to render the project samples table
 * @constructor
 */
export default function SamplesTable(): JSX.Element {
  const { projectId } = useParams();
  const [samples, pagination, api] = useSamplesTableState();
  const { data: associatedProjects } = useGetAssociatedProjectsQuery(
    Number(projectId)
  );

  const columns: TableColumnProps<ProjectSample>[] = [
    {
      title: i18n("SamplesTable.Column.sampleName"),
      className: "t-td-name",
      dataIndex: ["sample", "sampleName"],
      sorter: true,
      render: (name, row) => (
        <SampleDetailViewer sampleId={row.sample.id} projectId={row.project.id}>
          <Button type="link" className="t-sample-name" style={{ padding: 0 }}>
            {name}
          </Button>
        </SampleDetailViewer>
      ),
      ...api.getColumnSearchProps(
        ["sample", "sampleName"],
        "t-name-select",
        i18n("Filter.sampleName.placeholder")
      ),
    },
    {
      title: i18n("SamplesTable.Column.quality"),
      width: 100,
      dataIndex: "qcStatus",
      render: (qcStatus: string, row) => (
        <SampleQuality qcStatus={qcStatus} qualities={row.quality} />
      ),
    },
    {
      title: i18n("SamplesTable.Column.coverage"),
      className: "t-td-coverage",
      width: 100,
      dataIndex: "coverage",
    },
    {
      title: i18n("SamplesTable.Column.organism"),
      className: "t-td-organism",
      dataIndex: ["sample", "organism"],
      sorter: true,
      ...api.getColumnSearchProps(["sample", "organism"], "t-organism-select"),
    },
    {
      title: i18n("SamplesTable.Column.project"),
      className: "t-td-project",
      dataIndex: ["project", "name"],
      sorter: true,
      key: "associated",
      render: (name, row) => {
        return <ProjectTag project={row.project} />;
      },
      filters: associatedProjects,
    },
    {
      title: i18n("SamplesTable.Column.collectedBy"),
      dataIndex: ["sample", "collectedBy"],
      sorter: true,
      ...api.getColumnSearchProps(["sample", "collectedBy"]),
    },
    {
      title: i18n("SamplesTable.Column.created"),
      className: "t-td-created",
      dataIndex: ["sample", "createdDate"],
      sorter: true,
      width: 230,
      render: (createdDate) => {
        return formatInternationalizedDateTime(createdDate);
      },
      ...api.getDateColumnSearchProps("t-created-filter"),
    },
    {
      title: i18n("SamplesTable.Column.modified"),
      className: "t-td-modified",
      dataIndex: ["sample", "modifiedDate"],
      defaultSortOrder: "descend",
      sorter: true,
      width: 230,
      render: (modifiedDate) => {
        return formatInternationalizedDateTime(modifiedDate);
      },
      ...api.getDateColumnSearchProps("t-modified-filter"),
    },
  ];

  return (
    <Table
      bordered
      dataSource={samples}
      columns={columns}
      pagination={pagination}
      onChange={api.handleChange}
    />
  );
}
