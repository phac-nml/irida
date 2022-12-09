import {
  FilterConfirmProps,
  FilterValue,
  SortOrder,
  TablePaginationConfig,
} from "antd/es/table/interface";

/**
 * Properties that are available on the Ant Design Grid Component
 */
export interface GridProps {
  column?: number;
  gutter?: number;
  xs?: number;
  sm?: number;
  md?: number;
  lg?: number;
  xl?: number;
  xxl?: number;
}

export type MenuItem = {
  children?: MenuItem[];
  disabled?: boolean;
  key: string;
  label?: string | JSX.Element;
  type?: "divider" | "group";
};

export type TableFilters = Record<string, FilterValue | null>;

export type TableFilterConfirmFn = (param?: FilterConfirmProps) => void;

export type TableSortOrder = {
  property: string;
  direction: SortOrder;
}[];

export type TableOptions = {
  filters: TableFilters;
  pagination: TablePaginationConfig;
  order: TableSortOrder | undefined;
  search: string[];
  reload?: number;
};

export type TableSearch = {
  property: string;
  value: string | string[];
  operation: "IN";
  _file?: boolean;
};

export type TagColor =
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
