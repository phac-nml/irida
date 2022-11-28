import React, { useMemo } from "react";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";
import { Button, Table, TableProps } from "antd";
import { SearchProject, SearchSample } from "./SearchLayout";
import ProjectTag from "./ProjectTag";
import { ColumnsType } from "antd/lib/table";
import { SampleDetailViewer } from "../../components/samples/SampleDetailViewer";

type SearchSamplesTableParams = {
  samples:
    | {
        content: SearchSample[];
        total: number;
      }
    | undefined;
  handleTableChange: TableProps<SearchSample>["onChange"];
};

/**
 * React component to render a table to display samples found in global search
 * @param samples
 * @param handleTableChange
 * @constructor
 */
export default function SearchSamplesTable({
  samples,
  handleTableChange,
}: SearchSamplesTableParams) {
  const columns = useMemo<ColumnsType<SearchSample>>(
    () => [
      {
        key: `identifier`,
        dataIndex: "id",
        title: i18n("SearchTable.identifier"),
        width: 100,
      },
      {
        key: `sampleName`,
        dataIndex: "name",
        title: i18n("SearchTable.name"),
        sorter: true,
        render: (name, sample) => {
          return (
            <SampleDetailViewer
              sampleId={sample.id}
              projectId={sample.projects[0].id}
            >
              <Button type="link" style={{ padding: 0 }} size="small">
                {name}
              </Button>
            </SampleDetailViewer>
          );
        },
      },
      {
        key: `organism`,
        dataIndex: `organism`,
        title: i18n("SearchTable.organism"),
        sorter: true,
      },
      {
        key: `projects`,
        dataIndex: `projects`,
        title: i18n("SearchSamplesTable.project"),
        render: (projects: SearchProject[]) => {
          return projects.map((project) => (
            <ProjectTag key={`pTag-${project.id}`} project={project} />
          ));
        },
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
      dataSource={samples?.content}
      columns={columns}
      pagination={
        samples?.total ? getPaginationOptions(samples.total) : undefined
      }
      onChange={handleTableChange}
    />
  );
}
