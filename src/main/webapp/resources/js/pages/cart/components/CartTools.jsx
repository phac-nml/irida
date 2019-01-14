import React from "react";
import { Router, Link, Location } from "@reach/router";
import { Col, Row, Menu } from "antd";
import { spacing } from "../../../styles";

const { pipelines } = window.PAGE.i18n;

const Pipelines = () => {
  return <div>PIPELINES GO IN HERE!</div>;
};

const Tools = () => {
  return <div>TOOLS GO IN HERE</div>;
};

export default class CartTools extends React.Component {
  static propTypes = {};

  render() {
    return (
      <Row
        type="flex"
        style={{
          height: "100%",
          padding: spacing.DEFAULT,
          backgroundColor: "white"
        }}
      >
        <Location>
          {({ location }) => (
            <>
              <Col span={24}>
                <Menu mode="horizontal" selectedKeys={[location.pathname]}>
                  <Menu.Item key="/cart/pipelines">
                    <Link to="cart/pipelines">{pipelines.label}</Link>
                  </Menu.Item>
                  <Menu.Item key="/cart/tools">
                    <Link to="cart/tools">Tools</Link>
                  </Menu.Item>
                </Menu>
              </Col>
              <Router>
                <Pipelines path="cart/pipelines" default />
                <Tools path="cart/tools" />
              </Router>
            </>
          )}
        </Location>
      </Row>
    );
  }
}
