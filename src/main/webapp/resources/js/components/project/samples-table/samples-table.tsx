import React from "react";
import { Button, Select, Space, Table } from "antd";
import type { TableColumnProps } from "antd/es";
import { SampleDetailViewer } from "../../samples/SampleDetailViewer";
import { SearchOutlined } from "@ant-design/icons";
import useSamplesTableState from "./useSamplesTableState";
import { TableFilterConfirmFn } from "../../../types/ant-design";
import { ProjectSample } from "../../../redux/endpoints/project-samples";
import SampleQuality from "./components/SampleQuality";
import ProjectTag from "../../../pages/search/ProjectTag";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import getDateColumnSearchProps from "./components/date-column-search";

/**
 * React component to render the project samples table
 * @constructor
 */
export default function SamplesTable(): JSX.Element {
  const [samples, pagination, api] = useSamplesTableState();

  const getColumnSearchProps = (
    dataIndex: string | string[],
    filterName = "",
    placeholder = ""
  ) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }: {
      setSelectedKeys: (selectedKeys: string[]) => void;
      selectedKeys: string[];
      confirm: TableFilterConfirmFn;
      clearFilters: () => void;
    }) => (
      <div style={{ padding: 8 }}>
        <Select
          className={filterName}
          mode="tags"
          placeholder={placeholder}
          value={selectedKeys[0]}
          onChange={(e) => {
            const values = Array.isArray(e) && e.length > 0 ? [e] : e;
            setSelectedKeys(values);
            confirm({ closeDropdown: false });
          }}
          style={{ marginBottom: 8, display: "block" }}
        />
        <Space>
          <Button
            disabled={selectedKeys.length === 0 || selectedKeys[0].length === 0}
            onClick={() => api.handleClearSearch(clearFilters, confirm)}
            size="small"
            style={{ width: 89 }}
          >
            {i18n("Filter.clear")}
          </Button>
          <Button
            type="primary"
            onClick={() => api.handleSearch(selectedKeys, confirm)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
          >
            {i18n("Filter.search")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
  });

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
    // {
    //   title: i18n("SamplesTable.Column.project"),
    //   className: "t-td-project",
    //   dataIndex: ["project", "name"],
    //   sorter: true,
    //   key: "associated",
    //   render: (name, row) => {
    //     return <ProjectTag project={row.project} />;
    //   },
    //   filters: associatedProjects,
    // },
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
