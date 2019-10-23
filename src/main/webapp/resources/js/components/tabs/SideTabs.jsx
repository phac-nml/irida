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
 * Component to render styled side tabs
 */
export function SideTabs(props) {
  return (
    <VerticalTabs
      type="card"
      activeKey={props.activeKey}
      onChange={props.onChange}
      tabPosition={props.tabPosition || "left"}
      animated={props.animated || false}
    >
      {props.children}
    </VerticalTabs>
  );
}

SideTabs.propTypes = {
  activeKey: PropTypes.string,
  onChange: PropTypes.func,
  tabPosition: PropTypes.string,
  animated: PropTypes.bool
};
