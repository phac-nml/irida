import React, { useMemo } from "react";
import type { ColumnType } from "antd/es/list";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";
import { Table, TablePaginationConfig, Tag } from "antd";
import { FilterValue, SorterResult } from "antd/es/table/interface";
import { TableParams } from "./index";
import { SampleTableType, SearchSample } from "./SearchLayout";
import ProjectTag from "./ProjectTag";

type SearchSamplesTableParams = {
  samples:
    | {
        content: SearchSample[];
        total: number;
      }
    | undefined;
  handleTableChange: (
    pagination: TablePaginationConfig,
    filters: Record<string, FilterValue>,
    sorter: SorterResult<SampleTableType>
  ) => TableParams;
};
export default function SearchSamplesTable({
  samples,
  handleTableChange,
}: SearchSamplesTableParams) {
  const columns = useMemo<ColumnType[]>(
    () => [
      {
        key: `name`,
        dataIndex: "name",
        title: "NAME",
      },
      {
        key: `organism`,
        dataIndex: `organism`,
        title: "ORGANISM",
      },
      {
        key: `projects`,
        dataIndex: `projects`,
        title: `PROJECTS`,
        render: (projects) => {
          return projects.map((project) => <ProjectTag project={project} />);
        },
      },
      {
        key: `createdDate`,
        dataIndex: `createdDate`,
        title: `CREATED DATE`,
        render: (text: string) => formatInternationalizedDateTime(text),
      },
      {
        key: `modifiedDate`,
        dataIndex: `modifiedDate`,
        title: `MODIFIED DATE`,
        render: (text: string) => formatInternationalizedDateTime(text),
      },
    ],
    []
  );

  return (
    <Table
      dataSource={samples?.content}
      columns={columns}
      pagination={
        samples?.total ? getPaginationOptions(samples.total) : undefined
      }
      onChange={handleTableChange}
    />
  );
}
