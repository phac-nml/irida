import { ListItemLayout } from "antd/es/list";

export type BasicListItem =
  | { title: string; desc: number; props?: JSX.ElementAttributesProperty[] }
  | {
      title: string;
      desc: JSX.Element;
      props?: JSX.ElementAttributesProperty[];
    }
  | { title: string; desc: string; props?: JSX.ElementAttributesProperty[] };

// TODO (Josh - 6/8/22): Is there a better place for this?  Predefined in AntD?
export type Grid = {
  column?: number;
  gutter?: number;
  xs?: number;
  sm?: number;
  md?: number;
  lg?: number;
  xl?: number;
  xxl?: number;
};

export interface BasicListProps {
  itemLayout?: ListItemLayout | undefined;
  dataSource: BasicListItem[];
  grid?: Grid;
}
