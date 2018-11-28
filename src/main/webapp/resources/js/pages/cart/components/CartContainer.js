import React from "react";
import { Col, Layout } from "antd";
import Cart from "./Cart";

const { Content, Sider } = Layout;

export default function CartContainer({ total }) {
  return (
    <Layout>
      <Layout>
        <Sider theme="light" width={400}>
          <Cart total={total} />
        </Sider>
        <Content>
          Cart Has STuff: <strong>{total} in fact</strong>
        </Content>
      </Layout>
    </Layout>
  );
}
