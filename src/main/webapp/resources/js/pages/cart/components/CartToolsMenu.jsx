import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { Menu } from "antd";
import { Link } from "@reach/router";
import { COLOR_BORDER_LIGHT, grey1, grey6 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { AnalysesQueue } from "../../../components/AnalysesQueue";
import { MenuFoldOutlined, MenuUnfoldOutlined } from "@ant-design/icons";

const MenuWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 65px;
  border-bottom: 1px solid ${COLOR_BORDER_LIGHT};
  background-color: ${grey1};
  width: 100%;

  .ant-menu {
    line-height: 65px;
  }
`;

/**
 * Stateless UI component for creating tbs in the CartTools.
 * @param {string} pathname - currently visible path
 * @param {list} paths - list containing path definitions
 * @returns {*}
 */
export function CartToolsMenu({ pathname, paths, toggleSidebar, collapsed }) {
  const MenuIcon = collapsed ? MenuUnfoldOutlined : MenuFoldOutlined;
  return (
    <MenuWrapper>
      <Menu
        mode="horizontal"
        selectedKeys={[pathname]}
        style={{ borderBottom: `1px solid ${COLOR_BORDER_LIGHT}` }}
      >
        {paths.map(path => (
          <Menu.Item key={path.link}>
            <Link to={path.link}>{path.text}</Link>
          </Menu.Item>
        ))}
      </Menu>
      <AnalysesQueue />
      <MenuIcon
        style={{
          color: grey6,
          fontSize: 24,
          margin: SPACE_MD,
          cursor: "pointer"
        }}
        onClick={toggleSidebar}
      />
    </MenuWrapper>
  );
}

CartToolsMenu.propTypes = {
  /** The current visible path */
  pathname: PropTypes.string.isRequired,
  /** List of paths */
  paths: PropTypes.array.isRequired
};
