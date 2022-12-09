import React from "react";
import { useParams } from "react-router-dom";
import { useFetchPagedSamplesQuery } from "../../../redux/endpoints/project-samples";
import { Button, Select, Space, Table } from "antd";
import type { TableColumnProps } from "antd/es";
import { Project, Sample } from "../../../types/irida";
import { SampleDetailViewer } from "../../samples/SampleDetailViewer";
import { IconSearch } from "../../icons/Icons";
import { blue6 } from "../../../styles/colors";
import { FilterConfirmProps } from "antd/es/table/interface";
import { SearchOutlined } from "@ant-design/icons";
import useSamplesTableState, { ProjectSample } from "./useSamplesTableState";

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
      setSelectedKeys: (param: string | (string & any[])[]) => void;
      selectedKeys: string[];
      confirm: (param: FilterConfirmProps) => void;
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
            icon={<IconSearch />}
            size="small"
            style={{ width: 90 }}
          >
            {i18n("Filter.search")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{ color: filtered ? blue6 : undefined }} />
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
