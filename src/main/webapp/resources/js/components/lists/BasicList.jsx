/**
 * Component to render a basic list with just a
 * title and description for each item in the list
 */

import React from "react";
import PropTypes from "prop-types";
import { List, Typography } from "antd";

const { Text } = Typography;

export function BasicList({
  itemLayout = "horizontal",
  dataSource = { dataSource },
  ...props
}) {
  return (
    <List
      itemLayout="horizontal"
      dataSource={dataSource}
      renderItem={item => (
        <List.Item>
          <List.Item.Meta
            title={<Text strong>{item.title}</Text>}
            description={item.desc}
          />
        </List.Item>
      )}
    />
  );
}

BasicList.propTypes = {
  itemLayout: PropTypes.string,
  dataSource: PropTypes.array.isRequired
};
