import {
  ProjectSample,
  useFetchPagedSamplesQuery,
} from "../../../redux/endpoints/project-samples";
import { useParams } from "react-router-dom";
import {
  TableFilterConfirmFn,
  TableFilters,
  TableOptions,
  TableSearch,
} from "../../../types/ant-design";
import { TableProps } from "antd/es";
import { SelectedSample } from "../../../types/irida";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import React, { useState } from "react";
import { formatSearch, formatSort } from "../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";
import {
  FilterValue,
  Key,
  SorterResult,
  TableCurrentDataSource,
  TablePaginationConfig,
} from "antd/es/table/interface";
import { Button, Select, Space } from "antd";
import { IconSearch } from "../../icons/Icons";
import { blue6 } from "../../../styles/colors";
import { SearchOutlined } from "@ant-design/icons";
import { FilterDropdownProps } from "antd/lib/table/interface";
import getDateColumnSearchProps from "./components/date-column-search";

export type FilterByFile = {
  filename: string;
  fileFilter: TableSearch;
};

export type SamplesTableState = {
  options: TableOptions;
  selectedCount: number;
  selected: { [key: string]: SelectedSample };
  loadingLong?: boolean;
  filterByFile?: FilterByFile | null;
};

export const INITIAL_TABLE_OPTIONS: TableOptions = {
  filters: {},
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
};

export type UseSamplesTableState = [
  samples: ProjectSample[] | undefined,
  pagination: TablePaginationConfig | undefined,
  api: {
    handleClearSearch: (
      clearFilters: () => void,
      confirm: TableFilterConfirmFn
    ) => void;
    handleSearch: (
      selectedKeys: string[],
      confirm: TableFilterConfirmFn
    ) => void;
    handleChange: (
      pagination: TablePaginationConfig,
      tableFilters: Record<string, FilterValue | null>,
      sorter: SorterResult<ProjectSample> | SorterResult<ProjectSample>[],
      extra: TableCurrentDataSource<ProjectSample>
    ) => void | undefined;
    getColumnSearchProps: (
      dataIndex: string,
      filterName?: string,
      placeholder?: string
    ) => {
      filterDropdown: ({
        setSelectedKeys,
        selectedKeys,
        confirm,
        clearFilters,
      }: FilterDropdownProps) => Element;
      filterIcon: (filtered: boolean) => Element;
    };
  }
];

export default function useSamplesTableState(): UseSamplesTableState {
  const [tableOptions, setTableOptions] = useState<TableOptions>({
    ...INITIAL_TABLE_OPTIONS,
  });
  const [filterByFile, setFilterByFile] = useState<FilterByFile | undefined>();
  const [selected, setSelected] = useState<Record<number, ProjectSample>>({});

  const { projectId } = useParams();

  const { data, isSuccess } = useFetchPagedSamplesQuery({
    projectId: Number(projectId),
    body: tableOptions,
  });

  const getColumnSearchProps = (
    dataIndex: string,
    filterName = "",
    placeholder = ""
  ) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }: FilterDropdownProps) => (
      <div style={{ padding: 8 }}>
        <Select
          className={filterName}
          mode="tags"
          placeholder={placeholder}
          value={selectedKeys[0]}
          onChange={(e) => {
            const values = Array.isArray(e) && e.length > 0 ? [e] : e;
            setSelectedKeys(values as Key[]);
            confirm({ closeDropdown: false });
          }}
          style={{ marginBottom: 8, display: "block" }}
        />
        <Space>
          <Button
            disabled={selectedKeys.length === 0 || selectedKeys[0].length === 0}
            onClick={() => handleClearSearch(clearFilters, confirm)}
            size="small"
            style={{ width: 89 }}
          >
            {i18n("Filter.clear")}
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm)}
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
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
  });

  const handleChange: TableProps<ProjectSample>["onChange"] = (
    pagination,
    tableFilters,
    sorter
  ): void => {
    const { associated, ...otherSearch } = tableFilters;
    const search = formatSearch(otherSearch as TableFilters);
    const order = formatSort(sorter);
    const filters = associated === undefined ? {} : { associated };
    if (filterByFile) search.push(filterByFile.fileFilter);

    if (
      !(
        isEqual(tableOptions.search, search) &&
        isEqual(tableOptions.filters, filters)
      )
    ) {
      setSelected({});
    }

    setTableOptions({
      filters, // TODO: (Josh - 12/9/22) Why is this wrong?
      pagination,
      order,
      search,
    });
  };

  const handleSearch = (
    selectedKeys: Key[],
    confirm: TableFilterConfirmFn
  ): void => {
    confirm();
  };

  /**
   * Handle clearing a table column dropdown filter
   * @param clearFilters
   * @param confirm
   */
  const handleClearSearch = (
    clearFilters: (() => void) | undefined,
    confirm: TableFilterConfirmFn
  ): void | undefined => {
    if (typeof clearFilters === "function") {
      clearFilters();
    }
    confirm({ closeDropdown: false });
  };

  return [
    data?.content,
    isSuccess ? getPaginationOptions(data.total) : undefined,
    {
      handleClearSearch,
      handleSearch,
      handleChange,
      getColumnSearchProps,
      getDateColumnSearchProps,
    },
  ];
}
