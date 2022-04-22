import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { Col, Row } from "antd";
import { PageWrapper } from "../../../components/page/PageWrapper";
import UserAccountNav from "./UserAccountNav";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  const {userId} = useParams();
  const {data: userDetails = {}} = useGetUserDetailsQuery(userId);

  const goToAdminUserListPage = () =>
    (window.location.href = setBaseUrl(`admin/users`));

  return (
    <>
      <PageWrapper
        title={
          userDetails.admin
            ? i18n("UserAccountLayout.page.title", userDetails.user?.username)
            : userDetails.user?.username
        }
        onBack={userDetails.admin ? goToAdminUserListPage : undefined}
      >
        <Row>
          <Col span={4}>
            <UserAccountNav/>
          </Col>
          <Col xs={{span: 8, offset: 1}} md={{span: 16, offset: 1}}
               xl={{span: 32, offset: 1}}>
            <React.Suspense fallback={<ContentLoading/>}>
              <Outlet/>
            </React.Suspense>
          </Col>
        </Row>
      </PageWrapper>
    </>
  );
}
