/**
 * Component to render a basic list with just a
 * title and description for each item in the list
 */

import React from "react";
import { List, Typography } from "antd";

const {Text} = Typography;

/**
 * Stateless UI component for displaying a basic list with a title and
 * description for each item
 *
 * @param {{dataSource: Array}} dataSource - data for the List component to display
 * @param {string} itemLayout - layout of the list
 * @param {object} grid - grid type of list
 *
 * @returns {Element} - Returns an antd 'List' component with passed data
 */

export function BasicList({
                            itemLayout = "horizontal",
                            dataSource = {dataSource},
                            grid = undefined
                          }) {
  return (
    <List
      itemLayout={itemLayout}
      dataSource={dataSource}
      grid={grid}
      renderItem={item => (
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