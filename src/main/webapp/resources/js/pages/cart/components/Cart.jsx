import React, { useState } from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import { Layout } from "antd";
import { CartSamples } from "./CartSamples";
import { SampleDetails } from "../../../components/SampleDetails";
import { CartTools } from "./CartTools";

const { Content, Sider } = Layout;
const Wrapper = styled(Layout)`
  display: flex;
  height: 100%;
  width: 100%;
`;
//
// const Sidebar = styled(Sider)`
//   height: 100%;
//   width: 350px;
// `;
//
// const Content = styled.div`
//   height: 100%;
//   flex-grow: 1;
// `;

export default function Cart({ count }) {
  const [collapsed, setCollapsed] = useState(false);

  const toggleSidebar = () => setCollapsed(!collapsed);

  return (
    <Content style={{ display: "flex", height: "100%" }}>
      <Content style={{ flexGrow: 1 }}>
        <CartTools toggleSidebar={toggleSidebar} collapsed={collapsed} />
      </Content>
      <Sider
        width={400}
        trigger={null}
        collapsible
        collapsed={collapsed}
        collapsedWidth={0}
      >
        <CartSamples count={count} />
      </Sider>
      <SampleDetails />
    </Content>
  );
}

Cart.propTypes = {
  count: PropTypes.number
};
