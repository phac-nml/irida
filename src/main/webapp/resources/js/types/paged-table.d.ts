import type {
  FilterValue,
  TablePaginationConfig,
} from "antd/lib/table/interface";

export type PagedTableResponse<T> = {
  content: T[];
  total: number;
};

export type TableFilters = Record<string, FilterValue | null> | undefined;

export type PagedTableOptions = {
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
