import React, { useMemo } from "react";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";
import { Table, TableProps } from "antd";
import { SearchProject } from "./SearchLayout";
import ProjectTag from "./ProjectTag";
import { ColumnsType } from "antd/lib/table";

type SearchProjectsTableParams = {
  projects:
    | {
        content: SearchProject[];
        total: number;
      }
    | undefined;
  handleTableChange: TableProps<SearchProject>["onChange"];
};

/**
 * React component to render a table of projects fround in the glbal search.
 * @param projects
 * @param handleTableChange
 * @constructor
 */
export default function SearchProjectsTable({
  projects,
  handleTableChange,
}: SearchProjectsTableParams) {
  const columns = useMemo<ColumnsType<SearchProject>>(
    () => [
      {
        key: `name`,
        dataIndex: "name",
        title: i18n("SearchTable.name"),
        render: (_, project) => <ProjectTag project={project} />,
        sorter: true,
      },
      {
        key: `organism`,
        dataIndex: `organism`,
        title: i18n("SearchTable.organism"),
        sorter: true,
      },
      {
        key: `samples`,
        dataIndex: `samples`,
        title: i18n("SearchProjectsTable.samples"),
        width: 150,
      },
      {
        key: `createdDate`,
        dataIndex: `createdDate`,
        title: i18n("SearchTable.created"),
        render: (text: string) => formatInternationalizedDateTime(text),
        sorter: true,
        width: 200,
      },
      {
        key: `modifiedDate`,
        dataIndex: `modifiedDate`,
        title: i18n("SearchTable.updated"),
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
      className="t-search-projects"
    />
  );
}
