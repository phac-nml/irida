import React from "react";
import { Alert, Button, Form, Input, Space, Typography } from "antd";

/**
 * React component to display the user password page.
 * @returns {*}
 * @constructor
 */
export default function UserPasswordPage() {
  const [valid, setValid] = React.useState(true);

  return (
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
      <Form layout="vertical" autoComplete="off">
        <Form.Item
          label={i18n("UserPasswordPage.form.label.password")}
          name="password"
          rules={[
            { required: true, message: i18n("UserPasswordPage.alert.rule1") },
            { min: 8, message: i18n("UserPasswordPage.alert.rule2") },
            { pattern: new RegExp("^.*[A-Z].*$"), message: i18n("UserPasswordPage.alert.rule3") },
            { pattern: new RegExp("^.*[a-z].*$"), message: i18n("UserPasswordPage.alert.rule4") },
            { pattern: new RegExp("^.*[0-9].*$"), message: i18n("UserPasswordPage.alert.rule5") },
            { pattern: new RegExp("^.*[^A-Za-z0-9].*$"), message: i18n("UserPasswordPage.alert.rule6") },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label={i18n("UserPasswordPage.form.label.confirmPassword")}
          name="confirmPassword"
          dependencies={["password"]}
          rules={[
            { required: true, message: i18n("UserPasswordPage.alert.rule1") },
            ({ getFieldValue }) => ({
              validator(rule, value) {
                if (getFieldValue("password") !== value) {
                  return Promise.reject(i18n("UserPasswordPage.alert.rule7"));
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
          <Button
            type="primary"
            htmlType={i18n("UserPasswordPage.form.button.submit")}
          >
            Submit
          </Button>
        </Form.Item>
      </Form>
    </Space>
  );
}
