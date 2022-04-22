import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { Col, Layout, PageHeader, Row } from "antd";
import UserAccountNav from "./UserAccountNav";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  const {Content, Sider} = Layout;
  const {userId} = useParams();
  const {data: userDetails = {}} = useGetUserDetailsQuery(userId);

  const goToAdminUserListPage = () =>
    (window.location.href = setBaseUrl(`admin/users`));

  return (
    <Layout style={{height: "100%", minHeight: "100%"}}>
      <Row>
        <Col xl={{span: 12, offset: 6}}
             m={{span: 20, offset: 2}}
             xs={{span: 24}}>
          <PageHeader
            title={
              userDetails.admin
                ? i18n("UserAccountLayout.page.title", userDetails.user?.username)
                : userDetails.user?.username
            }
            onBack={userDetails.admin ? goToAdminUserListPage : undefined}
          />
          <Layout>
            <Sider width={200} style={{backgroundColor: grey1}}>
              <UserAccountNav/>
            </Sider>
            <Content
              style={{
                backgroundColor: grey1,
                padding: SPACE_MD
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
