import React, { useMemo } from "react";
import type { ColumnType } from "antd/es/list";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";
import { Table, TablePaginationConfig } from "antd";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { FilterValue, SorterResult } from "antd/es/table/interface";
import { TableParams } from "./index";

type SearchItem = {
  id: number;
  name: string;
  createdDate: number;
  modifiedDate: number;
  organism: string;
};

type SearchProject = SearchItem & {
  samples: SearchSample[];
};

type SearchSample = SearchItem & {
  projects: SearchProject[];
};

type SearchProjectsTableParams = {
  projects: SearchProject[];
  total: number;
  handleTableChange: (
    pagination: TablePaginationConfig,
    filters: Record<string, FilterValue>,
    sorter: SorterResult<SampleTableType>
  ) => TableParams;
};
export default function SearchProjectsTable({
  projects,
  total,
  handleTableChange,
}: SearchProjectsTableParams) {
  const columns = useMemo<ColumnType<any>[]>(
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
        key: `samples`,
        dataIndex: `samples`,
        title: `SAMPLES`,
      },
      {
        key: `createdDate`,
        dataIndex: `createdDate`,
        title: `CREATED DATE`,
        render: (text) => formatInternationalizedDateTime(text),
      },
      {
        key: `modifiedDate`,
        dataIndex: `modifiedDate`,
        title: `MODIFIED DATE`,
        render: (text) => formatInternationalizedDateTime(text),
      },
    ],
    []
  );

  return (
    <Table
      dataSource={projects}
      columns={columns}
      pagination={getPaginationOptions(total)}
      onChange={handleTableChange}
    />
  );
}
