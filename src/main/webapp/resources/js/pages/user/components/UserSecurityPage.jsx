import React from "react";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { Alert, Typography } from "antd";
import { UserChangePasswordForm } from "./UserChangePasswordForm";
import { UserResetPasswordLink } from "./UserResetPasswordLink";
import { SPACE_SM } from "../../../styles/spacing";

/**
 * React component to display the user security page.
 * @returns {*}
 * @constructor
 */
export default function UserSecurityPage() {
  const { userId } = useParams();
  const { user, canChangePassword, canCreatePasswordReset, mailConfigured } =
    useSelector((state) => state.userReducer);

  return (
    <>
      <Typography.Title level={4}>
        {i18n("UserSecurityPage.title")}
      </Typography.Title>
      {canCreatePasswordReset && !mailConfigured && (
        <Alert
          style={{ marginBottom: SPACE_SM }}
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
      {canChangePassword && <UserChangePasswordForm userId={userId} />}
      {canCreatePasswordReset && mailConfigured && (
        <UserResetPasswordLink
          userId={userId}
          firstName={user.firstName}
          lastName={user.lastName}
        />
      )}
    </>
  );
}
