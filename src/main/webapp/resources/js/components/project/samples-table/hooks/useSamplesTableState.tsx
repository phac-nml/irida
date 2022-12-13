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
import { CheckboxChangeEvent } from "antd/lib/checkbox";
import { INITIAL_TABLE_OPTIONS } from "../../../../layouts/project-samples/projectSamplesSlice";

export type HandleSearchFn = (confirm: TableFilterConfirmFn) => void;

export type HandleClearSearchFn = (
  confirm: TableFilterConfirmFn,
  clearFilters?: () => void
) => void | undefined;

export type UseSamplesTableState = [
  samples: ProjectSample[] | undefined,
  selection: {
    updateSelectAll: (even: CheckboxChangeEvent) => void;
    selectedCount: number;
    selected: Record<number, ProjectSample>;
    onRowSelectionChange: (
      event: CheckboxChangeEvent,
      sample: ProjectSample
    ) => void;
  },
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

  /**
   * Handle table change events, for example, pagination and table size.
   * This is called from the Ant Design Table component directly.
   *
   * @param pagination
   * @param tableFilters
   * @param sorter
   */
  const handleChange: TableProps<ProjectSample>["onChange"] = (
    pagination,
    tableFilters,
    sorter
  ): void => {
    const { associated, ...otherSearch } = tableFilters;
    const search = formatSearch(otherSearch as TableFilters);
    const order = formatSort(sorter);
    const filters =
      associated === undefined
        ? undefined
        : { associated: associated as FilterValue };
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
      filters,
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

  function updateSelectAll(event: CheckboxChangeEvent): void {
    console.log(e.target.checked);
  }

  return [
    data?.content,
    {
      updateSelectAll,
      selectedCount: Object.keys(selected).length,
      selected,
      onRowSelectionChange: (event, sample) => {
        console.log(event, sample);
      },
    },
    isSuccess ? getPaginationOptions(data.total) : undefined,
    {
      getColumnSearchProps: (
        dataIndex: string | string[],
        filterName: string,
        placeholder: string
      ): ColumnSearchReturn =>
        getColumnSearchProps(
          dataIndex,
          handleSearch,
          handleClearSearch,
          filterName,
          placeholder
        ),
      getDateColumnSearchProps: (filterName: string): ColumnSearchReturn =>
        getDateColumnSearchProps(filterName, handleSearch, handleClearSearch),
    },
  ];
}
