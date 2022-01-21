import React from "react";
import { useSelector } from "react-redux";
import { Space, Typography } from "antd";
import { UserChangePasswordForm } from "./UserChangePasswordForm";
import { UserResetPasswordLink } from "./UserResetPasswordLink";

/**
 * React component to display the user password page.
 * @returns {*}
 * @constructor
 */
export default function UserPasswordPage() {
  const { user, canChangePassword, canCreatePasswordReset, mailConfigured } =
    useSelector((state) => state.userReducer);

  return (
    <Space direction="vertical">
      <Typography.Title level={4}>
        {i18n("UserSecurityPage.title")}
      </Typography.Title>
      {canChangePassword && <UserChangePasswordForm />}
      {canCreatePasswordReset && mailConfigured && (
        <UserResetPasswordLink
          firstName={user.firstName}
          lastName={user.lastName}
        />
      )}
    </Space>
  );
}
