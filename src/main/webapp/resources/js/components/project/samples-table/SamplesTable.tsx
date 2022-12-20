import { Button, Checkbox, Space, Table } from "antd";
import type { TableColumnProps, TableProps } from "antd/es";
import { CheckboxChangeEvent } from "antd/lib/checkbox";
import React from "react";
import { useParams } from "react-router-dom";
import ProjectTag from "../../../pages/search/ProjectTag";
import { useGetAssociatedProjectsQuery } from "../../../redux/endpoints/project";
import {
  ProjectSample,
  useFetchPagedSamplesQuery,
  useLazyFetchMinimalSamplesForFilteredProjectQuery,
} from "../../../redux/endpoints/project-samples";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { SampleDetailViewer } from "../../samples/SampleDetailViewer";
import SampleIcons from "./components/SampleIcons";
import SampleQuality from "./components/SampleQuality";
import getColumnSearchProps from "./components/column-search";
import getDateColumnSearchProps from "./components/date-column-search";
import { useProjectSamples } from "./useProjectSamplesContext";

/**
 * React component to render the project samples table
 * @constructor
 */
export default function SamplesTable(): JSX.Element {
  const { state, dispatch } = useProjectSamples();

  const { projectId } = useParams();

  const { data, isFetching: isFetchingSamples } = useFetchPagedSamplesQuery(
    {
      projectId: Number(projectId),
      body: state.options,
    },
    {
      refetchOnMountOrArgChange: true,
    }
  );

  const [trigger, { isFetching: isFetchingIds }] =
    useLazyFetchMinimalSamplesForFilteredProjectQuery();

  const { data: associatedProjects, isFetching: isFetchingAssociated } =
    useGetAssociatedProjectsQuery(Number(projectId));

  const handleTableChange: TableProps<ProjectSample>["onChange"] = (
    pagination,
    filters,
    sorter
  ): void =>
    dispatch({ type: "tableUpdate", payload: { pagination, filters, sorter } });

  function onRowSelectionChange(item: ProjectSample) {
    return (e: CheckboxChangeEvent) =>
      dispatch({
        type: "rowSelectionChange",
        payload: {
          selected: e.target.checked,
          item,
        },
      });
  }

  const updateSelectAll = async (e: CheckboxChangeEvent) => {
    if (e.target.checked) {
      // Need to get all the associated projects
      const { data } = await trigger({
        projectId: Number(projectId),
        body: state.options,
      });
      if (data) {
        dispatch({ type: "selectAllSamples", payload: { samples: data } });
      }
    } else {
      dispatch({ type: "deselectAllSamples" });
    }
  };

  const columns: TableColumnProps<ProjectSample>[] = [
    {
      title: () => {
        const indeterminate = data
          ? state.selection.count < data?.total && state.selection.count > 0
          : false;
        return (
          <Checkbox
            className="t-select-all"
            onChange={updateSelectAll}
            checked={state.selection.count > 0}
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
              onChange={onRowSelectionChange(item)}
              checked={state.selection.selected[Number(item.key)] !== undefined}
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
      ...getColumnSearchProps(
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
      ...getColumnSearchProps(["sample", "organism"], "t-organism-select"),
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
      ...getColumnSearchProps(["sample", "collectedBy"]),
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
      ...getDateColumnSearchProps("t-created-filter"),
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
      ...getDateColumnSearchProps("t-modified-filter"),
    },
  ];

  return (
    <Table
      loading={isFetchingSamples || isFetchingAssociated || isFetchingIds}
      dataSource={data?.content}
      columns={columns}
      pagination={data?.total ? getPaginationOptions(data.total) : undefined}
      onChange={handleTableChange}
    />
  );
}
