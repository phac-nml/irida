import { Layout } from "antd";
import React, { lazy, Suspense } from "react";
import { useCountQuery } from "../../../apis/cart/cart";

const CartSamples = lazy(() => import("./CartSider"));
const CartTools = lazy(() => import("./CartTools"));

const { Content } = Layout;

export function Cart() {
  const [collapsed, setCollapsed] = React.useState(false);

  const { data: count = 0 } = useCountQuery();

  const toggleSidebar = () => setCollapsed(!collapsed);

  return (
    <Content style={{ display: "flex", alignItems: "stretch" }}>
      <Content style={{ flexGrow: 1 }}>
        <Suspense fallback={<div />}>
          <CartTools
            toggleSidebar={toggleSidebar}
            collapsed={collapsed}
            count={count}
          />
        </Suspense>
      </Content>
      <Suspense fallback={<div style={{ width: 400, height: "100%" }} />}>
        <CartSamples count={count} collapsed={collapsed} />
      </Suspense>
    </Content>
  );
}
