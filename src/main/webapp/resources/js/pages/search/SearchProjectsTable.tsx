import React, { useMemo } from "react";
import type { ColumnType } from "antd/es/list";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";
import { Table, TablePaginationConfig } from "antd";
import { FilterValue, SorterResult } from "antd/es/table/interface";
import { SampleTableType, SearchProject, TableParams } from "./SearchLayout";
import ProjectTag from "./ProjectTag";
import { ColumnsType } from "antd/lib/table";

type SearchProjectsTableParams = {
  projects:
    | {
        content: SearchProject[];
        total: number;
      }
    | undefined;
  handleTableChange: (
    pagination: TablePaginationConfig,
    filters: Record<string, FilterValue>,
    sorter: SorterResult<SampleTableType>
  ) => TableParams;
};
export default function SearchProjectsTable({
  projects,
  handleTableChange,
}: SearchProjectsTableParams) {
  const columns = useMemo<ColumnsType>(
    () => [
      {
        key: `name`,
        dataIndex: "name",
        title: "NAME",
        render: (_, project) => <ProjectTag project={project} />,
        sorter: true,
      },
      {
        key: `organism`,
        dataIndex: `organism`,
        title: "ORGANISM",
        sorter: true,
      },
      {
        key: `samples`,
        dataIndex: `samples`,
        title: `SAMPLES`,
        width: 150,
      },
      {
        key: `createdDate`,
        dataIndex: `createdDate`,
        title: `CREATED DATE`,
        render: (text: string) => formatInternationalizedDateTime(text),
        sorter: true,
        width: 200,
      },
      {
        key: `modifiedDate`,
        dataIndex: `modifiedDate`,
        title: `LAST UPDATED`,
        render: (text: string) => formatInternationalizedDateTime(text),
        sorter: true,
        defaultSortOrder: "descend",
        width: 200,
      },
    ],
    []
  );

  return (
    <Table
      dataSource={projects?.content}
      columns={columns}
      pagination={
        projects?.total ? getPaginationOptions(projects.total) : undefined
      }
      onChange={handleTableChange}
    />
  );
}
