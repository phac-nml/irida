/**
 * Properties that are available on the Ant Design Grid Component
 */

import type {
  FilterValue,
  TablePaginationConfig,
} from "antd/lib/table/interface";

declare namespace ANTD {
  interface GridProps {
    column?: number;
    gutter?: number;
    xs?: number;
    sm?: number;
    md?: number;
    lg?: number;
    xl?: number;
    xxl?: number;
  }

  type TagColor =
    | "magenta"
    | "red"
    | "volcano"
    | "orange"
    | "gold"
    | "lime"
    | "green"
    | "cyan"
    | "blue"
    | "geekblue"
    | "purple";

  export type TableFilters = Record<string, FilterValue | null> | undefined;

  export type TableOptions = {
    filters: TableFilters;
    pagination: TablePaginationConfig;
    order: TableSortOrder[] | undefined;
    search: TableSearch[];
    reload?: number;
  };

  export type TableSortOrder = {
    property: string;
    direction: "asc" | "desc";
  };

  export type TableOperation =
    | "IN"
    | "MATCH"
    | "MATCH_IN"
    | "GREATER_THAN_EQUAL"
    | "LESS_THAN_EQUAL";

  export type TableSearch = {
    property: string;
    value: string | string[];
    operation: TableOperation;
    _file?: boolean;
  };
}

export = ANTD;
export as namespace ANTD;
