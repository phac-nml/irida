import { Layout } from "antd";
import React from "react";
import { Outlet, useParams } from "react-router-dom";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";
import { grey1 } from "../../../styles/colors";
import { SPACE_LG } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import UserAccountNav from "./UserAccountNav";

const { Content, Sider } = Layout;

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountEditLayout() {
  const ADMIN_USERS_URL = "admin/users";
  const CREATE_USER_URL = "/users/create";

  const { userId } = useParams();
  const { data: userDetails = {} } = useGetUserDetailsQuery(userId);
  const showBack =
    userDetails.admin &&
    (document.referrer.includes(ADMIN_USERS_URL, 0) ||
      document.referrer.includes(CREATE_USER_URL, 0));
  const goToAdminUserListPage = () =>
    (window.location.href = setBaseUrl(ADMIN_USERS_URL));

  return (
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
  );
}
