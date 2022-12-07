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

export type TableOptions = {
  filters: {
    [column: string]: string;
  };
  pagination: {
    current: number;
    pageSize: number;
  };
  order: { property: string; direction: "asc" | "desc" }[];
  search: TableSearch[];
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
