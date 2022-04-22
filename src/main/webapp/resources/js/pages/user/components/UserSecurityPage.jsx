import React from "react";
import { useParams } from "react-router-dom";
import { Alert, Typography } from "antd";
import { UserChangePasswordForm } from "./UserChangePasswordForm";
import { UserResetPasswordLink } from "./UserResetPasswordLink";
import { SPACE_SM } from "../../../styles/spacing";
import { useGetUserDetailsQuery } from "../../../apis/users/users";

/**
 * React component to display the user security page.
 * @returns {*}
 * @constructor
 */
export default function UserSecurityPage() {
  const {userId} = useParams();
  const {data: userDetails = {}} = useGetUserDetailsQuery(userId);

  return (
    <>
      <Typography.Title level={4}>
        {i18n("UserSecurityPage.title")}
      </Typography.Title>
      {userDetails.canCreatePasswordReset && !userDetails.mailConfigured && (
        <Alert
          style={{marginBottom: SPACE_SM}}
          message={i18n("UserSecurityPage.alert.title")}
          description={
            <Typography.Paragraph>
              {i18n("UserSecurityPage.alert.description")}
            </Typography.Paragraph>
          }
          type="info"
          showIcon
        />
      )}
      {userDetails.canChangePassword &&
        <UserChangePasswordForm userId={userId}/>}
      {userDetails.canCreatePasswordReset && userDetails.mailConfigured && (
        <UserResetPasswordLink
          userId={userId}
          firstName={userDetails.user.firstName}
          lastName={userDetails.user.lastName}
        />
      )}
    </>
  );
}
