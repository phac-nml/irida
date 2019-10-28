/**
 * Component to render styled side tabs. Use
 * this whenever vertical tabs within a page
 * are required
 */

import React from "react";
import PropTypes from "prop-types";
import { Tabs } from "antd";
import styled from "styled-components";

const VerticalTabs = styled(Tabs)`
  .ant-tabs-tab {
    @media only screen and (min-width: 800px) {
      width: 200px;
    }
  }
`;

/**
 * Stateless UI component for creating vertical tabs
 * @param {string} type - layout type of tabs
 * @param {string} tabPosition - location of tabs on page
 * @param {string} activeKey - the key of the current tab that is displayed
 * @param {function} onChange - function which updates the current active tab key
 * @param {bool} animated - whether or not to add animation to when user selects another tab. This only works if tabPosition is set to 'top' or 'bottom'
 * @param {array} children - the content to display in the tabs
 *
 * @returns {Element} - Returns a 'VerticalTabs' component which displays the tabs vertically
 */

export function SideTabs({
  type = "card",
  tabPosition = "left",
  animated = false,
  activeKey,
  onChange,
  children,
  ...props
}) {
  return (
    <VerticalTabs
      type={type}
      activeKey={activeKey}
      onChange={onChange}
      tabPosition={tabPosition}
      animated={animated}
      {...props}
    >
      {children}
    </VerticalTabs>
  );
}

SideTabs.propTypes = {
  /*layout type of tabs*/
  type: PropTypes.string,
  /*the key of the current tab that is displayed*/
  activeKey: PropTypes.string.isRequired,
  /*function which updates the current active tab key*/
  onChange: PropTypes.func.isRequired,
  /*location of tabs on page*/
  tabPosition: PropTypes.string,
  /*whether or not to add animation to tab*/
  animated: PropTypes.bool,
  /*the content to display in the tabs*/
  children: PropTypes.array.isRequired
};
