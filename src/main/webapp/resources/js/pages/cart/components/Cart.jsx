import React from "react";
import { Layout } from "antd";
import CartSamples from "./CartSamples";
import SampleDetails from "./SampleDetailsContainer";


const { Content, Sider } = Layout;

export default function Cart({ count }) {
  return (
    <Layout style={{ height: "100%" }}>
      <Layout>
        <Sider theme="light" width={400}>
          <CartSamples count={count} />
        </Sider>
        <Content style={{ padding: 10 }}>
          Cart has stuff in it: <strong>{count} in fact</strong>
        </Content>
      </Layout>
      <SampleDetails/>
    </Layout>
  );
}
