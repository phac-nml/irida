import React from "react";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { Alert, Space, Typography } from "antd";
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
      {!(canCreatePasswordReset && mailConfigured) && (
        <Alert
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
    </Space>
  );
}
