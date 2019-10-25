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
  type: PropTypes.string,
  activeKey: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  tabPosition: PropTypes.string,
  animated: PropTypes.bool,
  children: PropTypes.array.isRequired
};
