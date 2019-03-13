import React from "react";
import PropTypes from "prop-types";
import { Col, Menu } from "antd";
import { Link } from "@reach/router";
import { COLOR_BORDER_LIGHT } from "../../../styles/colors";

/**
 * Stateless UI component for creating tbs in the CartTools.
 * @param {string} pathname - currently visible path
 * @param {list} paths - list containing path definitions
 * @returns {*}
 */
export function CartToolsMenu({ pathname, paths }) {
  return (
    <Col span={24}>
      <Menu
        mode="horizontal"
        selectedKeys={[pathname]}
        style={{ borderBottom: `1px solid ${COLOR_BORDER_LIGHT}` }}
      >
        {paths.map(path => (
          <Menu.Item style={{ paddingTop: 10, height: 65 }} key={path.key}>
            <Link to={path.link}>{path.text}</Link>
          </Menu.Item>
        ))}
      </Menu>
    </Col>
  );
}

CartToolsMenu.propTypes = {
  /** The current visible path */
  pathname: PropTypes.string.isRequired,
  /** List of paths */
  paths: PropTypes.array.isRequired
};
