import React from "react";
import { useDispatch } from "react-redux";
import { Outlet, useParams } from "react-router-dom";
import { Col, Row } from "antd";
import { PageWrapper } from "../../../components/page/PageWrapper";
import UserAccountNav from "./UserAccountNav";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";
import { setUserDetails } from "../services/userReducer";

/**
 * React component that layouts the user account page.
 * @returns {*}
 * @constructor
 */
export default function UserAccountLayout() {
  const dispatch = useDispatch();
  const { userId } = useParams();
  const {
    data: userDetails,
    isLoading,
    isSuccess,
  } = useGetUserDetailsQuery(userId);

  const goToAdminUserListPage = () =>
    (window.location.href = setBaseUrl(`admin/users`));

  React.useEffect(() => {
    if (isSuccess) {
      dispatch(
        setUserDetails(
          userDetails.user,
          userDetails.admin,
          userDetails.locales,
          userDetails.allowedRoles,
          userDetails.canEditUserInfo,
          userDetails.canEditUserStatus,
          userDetails.canChangePassword,
          userDetails.canCreatePasswordReset,
          userDetails.mailConfigured
        )
      );
    }
  }, [isSuccess]);

  return (
    <>
      {isLoading ? (
        <ContentLoading message={i18n("UserAccountLayout.loading.message")} />
      ) : (
        <PageWrapper
          title={
            userDetails.admin
              ? i18n("UserAccountLayout.page.title", userDetails.user.username)
              : userDetails.user.username
          }
          onBack={userDetails.admin ? goToAdminUserListPage : undefined}
        >
          <Row>
            <Col span={4}>
              <UserAccountNav />
            </Col>
            <Col offset={1} span={8}>
              <React.Suspense fallback={<ContentLoading />}>
                <Outlet />
              </React.Suspense>
            </Col>
          </Row>
        </PageWrapper>
      )}
    </>
  );
}
