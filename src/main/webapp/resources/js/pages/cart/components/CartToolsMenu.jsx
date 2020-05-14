import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { Button, Menu } from "antd";
import { Link } from "@reach/router";
import { grey1, grey6 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { AnalysesQueue } from "../../../components/AnalysesQueue";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { IconMenuFold, IconMenuUnfold } from "../../../components/icons/Icons";

const MenuWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 65px;
  border-bottom: ${BORDERED_LIGHT};
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
  return (
    <MenuWrapper>
      <Menu
        mode="horizontal"
        selectedKeys={[pathname]}
        style={{ borderBottom: BORDERED_LIGHT }}
      >
        {paths.map(path => (
          <Menu.Item key={path.link}>
            <Link to={path.link}>{path.text}</Link>
          </Menu.Item>
        ))}
      </Menu>
      <AnalysesQueue />
      <Button
        type="link"
        onClick={toggleSidebar}
        style={{ color: grey6, fontSize: 24, margin: SPACE_MD }}
      >
        {collapsed ? <IconMenuFold /> : <IconMenuUnfold />}
      </Button>
    </MenuWrapper>
  );
}

CartToolsMenu.propTypes = {
  /** The current visible path */
  pathname: PropTypes.string.isRequired,
  /** List of paths */
  paths: PropTypes.array.isRequired
};
