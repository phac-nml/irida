import type { FilterDropdownProps } from "antd/lib/table/interface";
import React from "react";
import { ColumnSearchReturn } from "../../../../types/ant-design";
import TableSearchFilter from "./TableSearchFilter";
import TableDateRangeFilter from "./TableDateRangeFilter";

export type DateColumnSearchFn = (filterName: string) => ColumnSearchReturn;

/**
 * Ant Design Table filter for date columns
 * @param filterName - class name for testing purposes.
 */
export default function getDateColumnSearchProps(
  filterName: string
): ColumnSearchReturn {
  return {
    filterDropdown: (props: FilterDropdownProps) => (
      <TableDateRangeFilter {...props} filterClassName={filterName} />
    ),
    filterIcon: (filtered) => <TableSearchFilter filtered={filtered} />,
  };
}
