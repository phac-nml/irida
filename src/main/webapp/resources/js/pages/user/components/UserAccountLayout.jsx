import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { Col, Layout, PageHeader, Row } from "antd";
import UserAccountNav from "./UserAccountNav";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";
import { SPACE_LG } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  const ADMIN_USERS_URL = "admin/users";

  const {Content, Sider} = Layout;
  const {userId} = useParams();
  const {data: userDetails = {}} = useGetUserDetailsQuery(userId);
  const showBack = userDetails.admin && document.referrer.includes(ADMIN_USERS_URL, 0);
  const goToAdminUserListPage = () =>
    (window.location.href = setBaseUrl(ADMIN_USERS_URL));

  return (
    <Layout style={{height: "100%", minHeight: "100%"}}>
      <Row>
        <Col xl={{span: 12, offset: 6}}
             m={{span: 20, offset: 2}}
             xs={{span: 24}}>
          <PageHeader
            title={
              showBack
                ? i18n("UserAccountLayout.page.title", userDetails.user?.username)
                : userDetails.user?.username
            }
            onBack={showBack ? goToAdminUserListPage : undefined}
          />
          <Layout>
            <Sider width={200} style={{backgroundColor: grey1}}>
              <UserAccountNav/>
            </Sider>
            <Content
              style={{
                backgroundColor: grey1,
                padding: SPACE_LG
              }}>
              <React.Suspense fallback={<ContentLoading/>}>
                <Outlet/>
              </React.Suspense>
            </Content>
          </Layout>
        </Col>
      </Row>
    </Layout>
  );
}
