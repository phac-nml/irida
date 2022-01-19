import React from "react";
import { useParams } from "react-router-dom";
import {
  Alert,
  Button,
  Form,
  Input,
  notification,
  Space,
  Typography,
} from "antd";
import {
  useGetUserDetailsQuery,
  useEditUserDetailsMutation,
} from "../../../apis/users/users";
import { ContentLoading } from "../../../components/loader";
import { UserResetPasswordLink } from "./UserResetPasswordLink";

/**
 * React component to display the user password page.
 * @returns {*}
 * @constructor
 */
export default function UserPasswordPage() {
  const { userId } = useParams();
  const { data: userDetails, isLoading } = useGetUserDetailsQuery(userId);
  const [editUser] = useEditUserDetailsMutation();
  const [form] = Form.useForm();

  const onFormFinish = (values) => {
    editUser({ userId: userId, ...values })
      .unwrap()
      .then((payload) => {
        notification.success({
          message: i18n("UserPasswordPage.notification.success"),
        });
        form.resetFields();
      })
      .catch((error) => {
        notification.error({
          message: i18n("UserPasswordPage.notification.error"),
        });
        const fields = Object.entries(error.data).map(([field, error]) => ({
          name: field,
          errors: [error],
        }));
        form.setErrors(fields);
      });
  };

  return (
    <>
      {isLoading ? (
        <ContentLoading message={i18n("UserDetailsPage.loading.message")} />
      ) : (
        <Space direction="vertical">
          <Typography.Title level={4}>
            {i18n("UserPasswordPage.page.title")}
          </Typography.Title>
          <Alert
            message={i18n("UserPasswordPage.alert.title")}
            description={
              <Typography.Paragraph>
                {i18n("UserPasswordPage.alert.description")}
                <ul>
                  <li>{i18n("UserPasswordPage.alert.rule2")}</li>
                  <li>{i18n("UserPasswordPage.alert.rule3")}</li>
                  <li>{i18n("UserPasswordPage.alert.rule4")}</li>
                  <li>{i18n("UserPasswordPage.alert.rule5")}</li>
                  <li>{i18n("UserPasswordPage.alert.rule6")}</li>
                  <li>{i18n("UserPasswordPage.alert.rule8")}</li>
                  <li>{i18n("UserPasswordPage.alert.rule9")}</li>
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
              label={i18n("UserPasswordPage.form.label.password")}
              name="password"
              rules={[
                {
                  required: true,
                  message: i18n("UserPasswordPage.alert.rule1"),
                },
                { min: 8, message: i18n("UserPasswordPage.alert.rule2") },
                {
                  pattern: new RegExp("^.*[A-Z].*$"),
                  message: i18n("UserPasswordPage.alert.rule3"),
                },
                {
                  pattern: new RegExp("^.*[a-z].*$"),
                  message: i18n("UserPasswordPage.alert.rule4"),
                },
                {
                  pattern: new RegExp("^.*[0-9].*$"),
                  message: i18n("UserPasswordPage.alert.rule5"),
                },
                {
                  pattern: new RegExp("^.*[^A-Za-z0-9].*$"),
                  message: i18n("UserPasswordPage.alert.rule6"),
                },
              ]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item
              label={i18n("UserPasswordPage.form.label.confirmPassword")}
              name="confirmPassword"
              dependencies={["password"]}
              rules={[
                {
                  required: true,
                  message: i18n("UserPasswordPage.alert.rule1"),
                },
                ({ getFieldValue }) => ({
                  validator(rule, value) {
                    if (getFieldValue("password") !== value) {
                      return Promise.reject(
                        i18n("UserPasswordPage.alert.rule7")
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
                {i18n("UserPasswordPage.form.button.submit")}
              </Button>
            </Form.Item>
          </Form>
          {userDetails.user.canCreatePasswordReset &&
            userDetails.user.mailConfigured && (
              <UserResetPasswordLink
                firstName={userDetails.user.firstName}
                lastName={userDetails.user.lastName}
              />
            )}
        </Space>
      )}
    </>
  );
}
