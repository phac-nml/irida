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

export type TableFilters = {
  [name: string]: string; // TODO: (Josh - 12/6/22) UPDATE THIS
};

export type TableOptions = {
  filters: TableFilters;
  pagination: TablePagination;
  order: {
    property: string;
    direction: "asc" | "desc";
  };
  search: string[];
};

export type TablePagination = {
  current: number;
  pageSize: number;
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
