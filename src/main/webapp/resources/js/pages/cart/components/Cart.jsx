import React from "react";
import { Layout } from "antd";
import CartSamples from "./CartSamples";
import SampleDetails from "../../../components/SampleDetails";
import CartTools from "./CartTools";
import { spacing } from "../../../styles";


const { Content, Sider } = Layout;

export default function Cart({ count }) {
  return (
    <Layout style={{ height: "100%" }}>
      <Layout>
        <Sider theme="light" width={400}>
          <CartSamples count={count} />
        </Sider>
        <Content style={{ padding: spacing.DEFAULT }}>
          <CartTools />
        </Content>
      </Layout>
      <SampleDetails/>
    </Layout>
  );
}
