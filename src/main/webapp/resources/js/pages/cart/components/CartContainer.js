import React from "react";
import { Layout } from "antd";
import Cart from "./Cart";
import * as PropTypes from "prop-types";

const { Content, Sider } = Layout;

export default class CartContainer extends React.Component {
  render() {
    let { total } = this.props;
    return (
      <Layout style={{ height: "100%" }}>
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
}

CartContainer.propTypes = {
  total: PropTypes.any,
  ids: PropTypes.any
};
