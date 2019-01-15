import React from "react";
import { Router, Link, Location } from "@reach/router";
import { Col, Row, Menu } from "antd";
import { spacing } from "../../../styles";
import styled from "styled-components";
import Pipelines from "../../../components/pipelines/Pipelines";

const { pipelines } = window.PAGE.i18n;

const ToolsWrapper = styled(Row)`
  height: 100%;
  background-color: white;
  position: relative;
`;

const ToolsInner = styled.div`
  padding: ${spacing.DEFAULT};
  position: absolute;
  top: 50px;
  right: 0;
  bottom: 0;
  left: 0;
  overflow-x: auto;
`;

export default class CartTools extends React.Component {
  render() {
    return (
      <ToolsWrapper>
        <Location>
          {({ location }) => (
            <>
              <Col span={24} style={{ paddingBottom: spacing.DEFAULT }}>
                <Menu mode="horizontal" selectedKeys={[location.pathname]}>
                  <Menu.Item key="/cart/pipelines">
                    <Link to="cart/pipelines">{pipelines.label}</Link>
                  </Menu.Item>
                </Menu>
              </Col>
              <ToolsInner>
                <Router>
                  <Pipelines path="cart/pipelines" default />
                </Router>
              </ToolsInner>
            </>
          )}
        </Location>
      </ToolsWrapper>
    );
  }
}
