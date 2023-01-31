/**
 * Properties that are available on the Ant Design Grid Component
 */

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
}

export = ANTD;
export as namespace ANTD;
