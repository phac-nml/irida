/**
 * Component to render a basic list with just a
 * title and description for each item in the list
 */

import React from "react";
import { List, Typography } from "antd";
import { BasicListProps } from "./BasicList.types";

const { Text } = Typography;

/**
 * Stateless UI component for displaying a basic list with a title and
 * description for each item
 *
 * @param {{dataSource: Array}} dataSource - data for the List component to display
 * @param {string} itemLayout - layout of the list
 * @param {object} grid - grid type of list
 */
export function BasicList({
  itemLayout = "horizontal",
  dataSource,
  grid = undefined,
}: BasicListProps): JSX.Element {
  return (
    <List
      itemLayout={itemLayout}
      dataSource={dataSource}
      grid={grid}
      renderItem={(item) => (
        <List.Item>
          <List.Item.Meta
            title={<Text strong>{item.title}</Text>}
            description={item.desc}
            {...item.props}
          />
        </List.Item>
      )}
    />
  );
}
