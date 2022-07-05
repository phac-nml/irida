import React from "react";
import { Outlet, useLocation, useNavigate, useParams } from "react-router-dom";
import UserAccountNav from "./UserAccountNav";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";
import { NarrowPageWrapper } from "../../../components/page/NarrowPageWrapper";
import { grey1 } from "../../../styles/colors";
import { SPACE_LG } from "../../../styles/spacing";
import { Layout } from "antd";

const { Content, Sider } = Layout;
/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { userId } = useParams();
  const { data: userDetails = {} } = useGetUserDetailsQuery(userId);
  const showBack = location.pathname.includes("admin");

  return (
    <NarrowPageWrapper
      title={
        showBack
          ? i18n("UserAccountLayout.page.title", userDetails.user?.username)
          : userDetails.user?.username
      }
      onBack={showBack ? () => navigate(-1) : undefined}
    >
      <Layout style={{ backgroundColor: grey1 }}>
        <Sider width={200}>
          <UserAccountNav />
        </Sider>
        <Content
          style={{
            padding: SPACE_LG,
            marginBottom: SPACE_LG,
          }}
        >
          <React.Suspense fallback={<ContentLoading />}>
            <Outlet />
          </React.Suspense>
        </Content>
      </Layout>
    </NarrowPageWrapper>
  );
}
