import React, { useState, lazy, Suspense } from "react";
import PropTypes from "prop-types";
import { Layout } from "antd";
import { CartSamples } from "./CartSamples";
const SampleDetails = lazy(() => import("../../../components/SampleDetails"));
import { CartTools } from "./CartTools";

const { Content, Sider } = Layout;

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
      <Suspense fallback={<span />}>
        <SampleDetails />
      </Suspense>
    </Content>
  );
}

Cart.propTypes = {
  count: PropTypes.number
};
