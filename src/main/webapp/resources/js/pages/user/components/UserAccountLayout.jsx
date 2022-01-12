import React from "react";
import { Outlet } from "react-router-dom";
import { Col, Row } from "antd";
import { PageWrapper } from "../../../components/page/PageWrapper";
import UserAccountNav from "./UserAccountNav";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { isAdmin } from "../../../utilities/role-utilities";

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  const goToAdminUserListPage = () =>
    (window.location.href = setBaseUrl(`admin/users`));

  return (
    <PageWrapper
      title={i18n("UserAccountLayout.page.title")}
      onBack={isAdmin ? goToAdminUserListPage : undefined}
    >
      <Row>
        <Col span={5}>
          <UserAccountNav />
        </Col>
        <Col offset={1} span={18}>
          <Outlet />
        </Col>
      </Row>
    </PageWrapper>
  );
}
