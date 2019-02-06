import React from "react";
import PropTypes from "prop-types";
import { SPACING } from "../../../styles";
import { Col, Menu } from "antd";
import { Link } from "@reach/router";

/**
 * Stateless UI component for creating tbs in the CartTools.
 * @param {string} pathname - currently visible path
 * @param {list} paths - list containing path definitions
 * @returns {*}
 */
export function CartToolsMenu({ pathname, paths }) {
  return (
    <Col span={24} style={{ paddingBottom: SPACING.DEFAULT }}>
      <Menu mode="horizontal" selectedKeys={[pathname]}>
        {paths.map(path => (
          <Menu.Item key={path.key}>
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
