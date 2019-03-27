import React, { lazy, Suspense, useState } from "react";
import PropTypes from "prop-types";
import { Layout } from "antd";
import { SampleDetailsLoader } from "../../../components/SampleDetails";

const CartSamples = lazy(() => import("./CartSamples"));
const CartTools = lazy(() => import("./CartTools"));

const { Content } = Layout;

export default function Cart({ count }) {
  const [collapsed, setCollapsed] = useState(false);

  const toggleSidebar = () => setCollapsed(!collapsed);

  return (
    <Content style={{ display: "flex", height: "100%" }}>
      <Content style={{ flexGrow: 1 }}>
        <Suspense fallback={<div />}>
          <CartTools toggleSidebar={toggleSidebar} collapsed={collapsed} />
        </Suspense>
      </Content>
      <Suspense fallback={<div style={{ width: 400, height: "100%" }} />}>
        <CartSamples count={count} collapsed={collapsed} />
      </Suspense>
      <SampleDetailsLoader />
    </Content>
  );
}

Cart.propTypes = {
  count: PropTypes.number
};
