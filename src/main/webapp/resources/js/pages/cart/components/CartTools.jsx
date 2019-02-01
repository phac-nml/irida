import React from "react";
import { Link, Location, Router } from "@reach/router";
import { Col, Menu, Row } from "antd";
import { SPACING } from "../../../styles";
import styled from "styled-components";
import { Pipelines } from "../../../components/pipelines/Pipelines";
import { getI18N } from "../../../utilities/i18n-utilties";

const ToolsWrapper = styled(Row)`
  height: 100%;
  background-color: white;
  position: relative;
`;

const ToolsInner = styled.div`
  padding: ${SPACING.DEFAULT};
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
              <Col span={24} style={{ paddingBottom: SPACING.DEFAULT }}>
                <Menu mode="horizontal" selectedKeys={[location.pathname]}>
                  <Menu.Item key="/cart/pipelines">
                    <Link to="cart/pipelines">
                      {getI18N("CartTools.menu.pipelines")}
                    </Link>
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
