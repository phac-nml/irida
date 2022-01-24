import React from "react";
import { useNavigate } from "react-router-dom";
import { Alert, Button, Form, Input, notification, Typography } from "antd";
import { useEditUserDetailsMutation } from "../../../apis/users/users";

/**
 * React component to display the user change password form.
 * @param {number} userId the identification number of the user
 * @returns {*}
 * @constructor
 */
export function UserChangePasswordForm({ userId }) {
  const [editUser] = useEditUserDetailsMutation();
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFormFinish = (values) => {
    editUser({ userId: userId, ...values })
      .unwrap()
      .then((payload) => {
        notification.success({
          message: i18n("UserChangePasswordForm.notification.success"),
        });
        form.resetFields();
        navigate(`/${userId}/details`);
      })
      .catch((error) => {
        const fields = Object.entries(error.data).map(([field, error]) => ({
          name: field,
          errors: [error],
        }));
        form.setFields(fields);
      });
  };

  return (
    <>
      <Typography.Title level={5}>
        {i18n("UserChangePasswordForm.title")}
      </Typography.Title>
      <Alert
        message={i18n("UserChangePasswordForm.alert.title")}
        description={
          <Typography.Paragraph>
            {i18n("UserChangePasswordForm.alert.description")}
            <ul>
              <li>{i18n("UserChangePasswordForm.alert.rule2")}</li>
              <li>{i18n("UserChangePasswordForm.alert.rule3")}</li>
              <li>{i18n("UserChangePasswordForm.alert.rule4")}</li>
              <li>{i18n("UserChangePasswordForm.alert.rule5")}</li>
              <li>{i18n("UserChangePasswordForm.alert.rule6")}</li>
              <li>{i18n("UserChangePasswordForm.alert.rule8")}</li>
              <li>{i18n("UserChangePasswordForm.alert.rule9")}</li>
            </ul>
          </Typography.Paragraph>
        }
        type="info"
        showIcon
      />
      <Form
        form={form}
        layout="vertical"
        onFinish={onFormFinish}
        autoComplete="off"
      >
        <Form.Item
          label={i18n("UserChangePasswordForm.form.label.oldPassword")}
          name="oldPassword"
          rules={[
            {
              required: true,
              message: i18n("UserChangePasswordForm.alert.rule1"),
            },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label={i18n("UserChangePasswordForm.form.label.newPassword")}
          name="newPassword"
          rules={[
            {
              required: true,
              message: i18n("UserChangePasswordForm.alert.rule1"),
            },
            { min: 8, message: i18n("UserChangePasswordForm.alert.rule2") },
            {
              pattern: new RegExp("^.*[A-Z].*$"),
              message: i18n("UserChangePasswordForm.alert.rule3"),
            },
            {
              pattern: new RegExp("^.*[a-z].*$"),
              message: i18n("UserChangePasswordForm.alert.rule4"),
            },
            {
              pattern: new RegExp("^.*[0-9].*$"),
              message: i18n("UserChangePasswordForm.alert.rule5"),
            },
            {
              pattern: new RegExp("^.*[^A-Za-z0-9].*$"),
              message: i18n("UserChangePasswordForm.alert.rule6"),
            },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label={i18n("UserChangePasswordForm.form.label.confirmNewPassword")}
          name="confirmNewPassword"
          dependencies={["newPassword"]}
          rules={[
            {
              required: true,
              message: i18n("UserChangePasswordForm.alert.rule1"),
            },
            ({ getFieldValue }) => ({
              validator(rule, value) {
                if (getFieldValue("newPassword") !== value) {
                  return Promise.reject(
                    i18n("UserChangePasswordForm.alert.rule7")
                  );
                } else {
                  return Promise.resolve();
                }
              },
            }),
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            {i18n("UserChangePasswordForm.form.button.submit")}
          </Button>
        </Form.Item>
      </Form>
    </>
  );
}
