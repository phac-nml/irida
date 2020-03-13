/**
 * Component to render a basic list with just a
 * title and description for each item in the list
 */

import React from "react";
import PropTypes from "prop-types";
import { List, Typography } from "antd";

const { Text } = Typography;

/**
 * Stateless UI component for displaying a basic list with a title and
 * desciption for each item
 *
 * @param {array} dataSource - data for the List component to display
 * @param {string} itemLayout - layout of the list
 *
 * @returns {Element} - Returns an antd 'List' component with passed data
 */

export function BasicList({
  itemLayout = "horizontal",
  dataSource = { dataSource }
}) {
  return (
    <List
      itemLayout={itemLayout}
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
  /*layout of the list*/
  itemLayout: PropTypes.string,
  /*data for the List component to display*/
  dataSource: PropTypes.arrayOf(
    PropTypes.shape({
      title: PropTypes.string.isRequired,
      desc: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
        PropTypes.object
      ])
    })
  ).isRequired
};
