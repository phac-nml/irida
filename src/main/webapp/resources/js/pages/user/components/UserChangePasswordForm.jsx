import React from "react";
import { useNavigate } from "react-router-dom";
import { Button, Form, Input, notification, Skeleton, Typography } from "antd";
import {
  useChangeUserPasswordMutation,
  useGetUserDetailsQuery,
} from "../../../apis/users/users";
import { validatePassword } from "../../../utilities/validation-utilities";
import { PasswordPolicyAlert } from "../../../components/alerts/PasswordPolicyAlert";

/**
 * React component to display the user change password form.
 * @param {number} userId the identification number of the user
 * @param {boolean} requireOldPassword whether to show the old password form field
 * @returns {*}
 * @constructor
 */
export function UserChangePasswordForm({ userId, requireOldPassword }) {
  const [changeUserPassword] = useChangeUserPasswordMutation();
  const { data: userDetails = {}, isLoading } = useGetUserDetailsQuery(userId);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFormFinish = (values) => {
    changeUserPassword({ userId: userId, ...values })
      .unwrap()
      .then(() => {
        notification.success({
          message: i18n("UserChangePasswordForm.notification.success"),
        });
        form.resetFields();
        navigate(`/${userId}/details`);
      })
      .catch((error) => {
        notification.error({
          message: i18n("UserChangePasswordForm.notification.error"),
        });
        const fields = Object.entries(error.data.errors).map(
          ([field, error]) => ({
            name: field,
            errors: [error],
          })
        );
        form.setFields(fields);
      });
  };

  return (
    <Skeleton loading={isLoading}>
      <Typography.Title level={5}>
        {i18n("UserChangePasswordForm.title")}
      </Typography.Title>
      <Typography.Text type="secondary" hidden={!userDetails.domainAccount}>
        {i18n("UserChangePasswordForm.ldapUserInfo")}
      </Typography.Text>
      <PasswordPolicyAlert />
      <Form
        form={form}
        layout="vertical"
        onFinish={onFormFinish}
        autoComplete="off"
        hidden={userDetails.domainAccount}
      >
        {requireOldPassword && (
          <Form.Item
            label={i18n("UserChangePasswordForm.form.label.oldPassword")}
            name="oldPassword"
            rules={[
              {
                required: true,
                message: i18n("validation-utilities.password.required"),
              },
            ]}
            disabled={userDetails.domainAccount}
          >
            <Input.Password />
          </Form.Item>
        )}
        <Form.Item
          label={i18n("UserChangePasswordForm.form.label.newPassword")}
          name="newPassword"
          rules={[
            () => ({
              validator(_, value) {
                return validatePassword(value);
              },
            }),
          ]}
          disabled={userDetails.domainAccount}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button
            className="t-submit-btn"
            disabled={userDetails.domainAccount}
            type="primary"
            htmlType="submit"
          >
            {i18n("UserChangePasswordForm.form.button.submit")}
          </Button>
        </Form.Item>
      </Form>
    </Skeleton>
  );
}
