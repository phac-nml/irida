import React from "react";
import { Layout } from "antd";
import CartSamples from "./CartSamples";
const { Content, Sider } = Layout;

export default class Cart extends React.Component {
  render() {
    const { count } = this.props;
    return (
      <Layout style={{ height: "100%" }}>
        <Layout>
          <Sider theme="light" width={400}>
            <CartSamples count={count} />
          </Sider>
          <Content>
            Cart Has STuff: <strong>{count} in fact</strong>
          </Content>
        </Layout>
      </Layout>
    );
  }
}
