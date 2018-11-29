import React from "react";
import { Col, Layout } from "antd";
import Cart from "./Cart";
import * as PropTypes from "prop-types";

const { Content, Sider } = Layout;

export default class CartContainer extends React.Component {
  componentDidMount() {}

  render() {
    let { total, ids } = this.props;
    return (
      <Layout style={{ height: "100%" }}>
        <Layout>
          <Sider theme="light" width={400}>
            <Cart ids={ids} />
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
