import React from "react";
import { Button, Checkbox, Space, Table } from "antd";
import type { TableColumnProps, TableProps } from "antd/es";
import { SampleDetailViewer } from "../../samples/SampleDetailViewer";
import useSamplesTableState from "./hooks/useSamplesTableState";
import {
  ProjectSample,
  useFetchPagedSamplesQuery,
} from "../../../redux/endpoints/project-samples";
import SampleQuality from "./components/SampleQuality";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import ProjectTag from "../../../pages/search/ProjectTag";
import { useGetAssociatedProjectsQuery } from "../../../redux/endpoints/project";
import { useParams } from "react-router-dom";
import SampleIcons from "./components/SampleIcons";
import { useAppDispatch, useTypedSelector } from "../../../redux/store";
import { FilterValue } from "antd/es/table/interface";
import { tableUpdated } from "../../../layouts/project-samples/projectSamplesSlice";

/**
 * React component to render the project samples table
 * @constructor
 */
export default function SamplesTable(): JSX.Element {
  const dispatch = useAppDispatch();

  const { projectId } = useParams();

  const state = useTypedSelector((state) => state.projectSamples);

  const { data, isSuccess } = useFetchPagedSamplesQuery({
    projectId: Number(projectId),
    body: state.options,
  });

  const [samples, selection, pagination, api] = useSamplesTableState();
  const { data: associatedProjects } = useGetAssociatedProjectsQuery(
    Number(projectId)
  );

  const handleTableChange: TableProps<ProjectSample>["onChange"] = (
    ...options
  ): void => {
    dispatch(tableUpdated(options));
    // const { associated, ...otherSearch } = tableFilters;
    // const filters =
    //   associated === undefined
    //     ? undefined
    //     : { associated: associated as FilterValue };
  };

  const columns: TableColumnProps<ProjectSample>[] = [
    {
      title: () => {
        const indeterminate =
          selection.selectedCount < selection.total &&
          selection.selectedCount > 0;
        return (
          <Checkbox
            className="t-select-all"
            onChange={selection.updateSelectAll}
            checked={selection.selectedCount > 0}
            indeterminate={indeterminate}
          />
        );
      },
      dataIndex: "key",
      width: 40,
      render: (text, item) => {
        return (
          <Space>
            <Checkbox
              onChange={(e) => selection.onRowSelectionChange(e, item)}
              checked={selection.selected[Number(item.key)] !== undefined}
            />
            <SampleIcons sample={item} />
          </Space>
        );
      },
    },
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
      dataSource={data?.content}
      columns={columns}
      pagination={pagination}
      onChange={handleTableChange}
    />
  );
}
