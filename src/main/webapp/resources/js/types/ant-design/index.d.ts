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

export type TableFilter = {};

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
