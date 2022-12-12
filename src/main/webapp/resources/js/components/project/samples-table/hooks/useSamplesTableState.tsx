import {
  ProjectSample,
  useFetchPagedSamplesQuery,
} from "../../../../redux/endpoints/project-samples";
import { useParams } from "react-router-dom";
import {
  ColumnSearchReturn,
  TableFilterConfirmFn,
  TableFilters,
  TableOptions,
  TableSearch,
} from "../../../../types/ant-design";
import { TableProps } from "antd/es";
import { SelectedSample } from "../../../../types/irida";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";
import { useState } from "react";
import {
  formatSearch,
  formatSort,
} from "../../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";
import {
  FilterValue,
  SorterResult,
  TableCurrentDataSource,
  TablePaginationConfig,
} from "antd/es/table/interface";
import getDateColumnSearchProps from "../components/date-column-search";
import getColumnSearchProps from "../components/column-search";

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

export type HandleSearchFn = (confirm: TableFilterConfirmFn) => void;

export type HandleClearSearchFn = (
  confirm: TableFilterConfirmFn,
  clearFilters?: () => void
) => void | undefined;

export type UseSamplesTableState = [
  samples: ProjectSample[] | undefined,
  pagination: TablePaginationConfig | undefined,
  api: {
    handleChange: (
      pagination: TablePaginationConfig,
      tableFilters: Record<string, FilterValue | null>,
      sorter: SorterResult<ProjectSample> | SorterResult<ProjectSample>[],
      extra: TableCurrentDataSource<ProjectSample>
    ) => void | undefined;
    getColumnSearchProps: (
      dataIndex: string | string[],
      filterName: string,
      placeholder: string
    ) => ColumnSearchReturn;
    getDateColumnSearchProps: (filterName: string) => ColumnSearchReturn;
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

  const handleSearch = (confirm: TableFilterConfirmFn): void => {
    confirm();
  };

  /**
   * Handle clearing a table column dropdown filter
   * @param clearFilters
   * @param confirm
   */
  const handleClearSearch = (
    confirm: TableFilterConfirmFn,
    clearFilters: (() => void) | undefined
  ): void | undefined => {
    if (clearFilters) {
      clearFilters();
    }
    confirm({ closeDropdown: false });
  };

  function getColumnSearchProperties(
    dataIndex: string | string[],
    filterName: string,
    placeholder: string
  ): ColumnSearchReturn {
    return getColumnSearchProps(
      dataIndex,
      handleSearch,
      handleClearSearch,
      filterName,
      placeholder
    );
  }

  function getDateColumnSearchProperties(
    filterName: string
  ): ColumnSearchReturn {
    return getDateColumnSearchProps(
      filterName,
      handleSearch,
      handleClearSearch
    );
  }

  return [
    data?.content,
    isSuccess ? getPaginationOptions(data.total) : undefined,
    {
      handleChange,
      getColumnSearchProps: getColumnSearchProperties,
      getDateColumnSearchProps: getDateColumnSearchProperties,
    },
  ];
}
