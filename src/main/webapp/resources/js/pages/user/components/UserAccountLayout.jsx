import React from "react";
import { Outlet } from "react-router-dom";
import { Col, Menu, Row } from 'antd';
import { PageWrapper } from "../../../components/page/PageWrapper";
import UserAccountNav from "./UserAccountNav";

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  return (
    <PageWrapper title="User Account">
      <Row>
        <Col span={5}>
          <UserAccountNav />
        </Col>
        <Col span={1} />
        <Col span={18}>
          <Outlet />
        </Col>
      </Row>
    </PageWrapper>
  );
}
