import React from "react";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { Space, Typography } from "antd";
import { UserChangePasswordForm } from "./UserChangePasswordForm";
import { UserResetPasswordLink } from "./UserResetPasswordLink";

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
    <Space direction="vertical">
      <Typography.Title level={4}>
        {i18n("UserSecurityPage.title")}
      </Typography.Title>
      {canChangePassword && <UserChangePasswordForm userId />}
      {canCreatePasswordReset && mailConfigured && (
        <UserResetPasswordLink
          userId
          firstName={user.firstName}
          lastName={user.lastName}
        />
      )}
    </Space>
  );
}
