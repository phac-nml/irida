import {
  ProjectSample,
  useFetchPagedSamplesQuery,
} from "../../../redux/endpoints/project-samples";
import { useParams } from "react-router-dom";
import {
  TableFilterConfirmFn,
  TableFilters,
  TableOptions,
} from "../../../types/ant-design";
import { TableProps } from "antd/es";
import { SelectedSample } from "../../../types/irida";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";
import { FilterByFile } from "../../../pages/projects/redux/samplesSlice";
import { useState } from "react";
import { formatSearch, formatSort } from "../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";

export type SamplesTableState = {
  options: TableOptions;
  selectedCount: number;
  selected: { [key: string]: SelectedSample };
  loadingLong?: boolean;
  filterByFile?: FilterByFile | null;
};

export const INITIAL_TABLE_OPTIONS: TableOptions = {
  filters: { associated: null },
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
};

export default function useSamplesTableState() {
  const { projectId } = useParams();
  const [tableOptions, setTableOptions] = useState<TableOptions>({
    ...INITIAL_TABLE_OPTIONS,
  });
  const [filterByFile, setFilterByFile] = useState<boolean>(false);
  const [selected, setSelected] = useState<Record<number, ProjectSample>>({});

  const handleChange: TableProps<ProjectSample>["onChange"] = (
    pagination,
    tableFilters,
    sorter
  ) => {
    const { associated, ...filters } = tableFilters;
    const search = formatSearch(filters);
    const newFilters: TableFilters = {
      associated: associated === undefined ? null : associated,
    };
    // if (filterByFile) search.push(filterByFile.fileFilter);

    if (
      !(
        isEqual(tableOptions.search, search) &&
        isEqual(tableOptions.filters, filters)
      )
    ) {
      setSelected({});
    }

    setTableOptions({
      filters: newFilters,
      pagination,
      order: formatSort(sorter),
      search,
    });
  };

  const handleSearch = (selectedKeys, confirm: TableFilterConfirmFn): void => {
    confirm();
  };

  /**
   * Handle clearing a table column dropdown filter
   * @param clearFilters
   * @param confirm
   */
  const handleClearSearch = (
    clearFilters: () => void,
    confirm: TableFilterConfirmFn
  ): void => {
    clearFilters();
    confirm({ closeDropdown: false });
  };

  const { data } = useFetchPagedSamplesQuery({
    projectId,
    body: tableOptions,
  });

  return [
    data?.content,
    getPaginationOptions(data?.total),
    {
      handleClearSearch,
      handleSearch,
      handleChange,
    },
  ];
}
