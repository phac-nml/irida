import { Layout } from "antd";
import React from "react";
import { grey2 } from "../../../styles/colors";
import CartSamples from "./CartSamples";

const { Sider } = Layout;

export default function CartSider({ collapsed }) {
  return (
    <Sider
      width={400}
      trigger={null}
      collapsible
      collapsed={collapsed}
      collapsedWidth={0}
      style={{ backgroundColor: grey2 }}
    >
      <CartSamples />
    </Sider>
  );
}
