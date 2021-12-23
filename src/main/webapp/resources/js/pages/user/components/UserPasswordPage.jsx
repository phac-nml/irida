import React from "react";
import {
  Alert,
  Button,
  Form,
  Input,
  Space,
  Typography,
} from "antd";

/**
 * React component to display the user password page.
 * @returns {*}
 * @constructor
 */
export default function UserPasswordPage() {
  const [valid, setValid] = React.useState(true);

  return(
    <Space direction="vertical">
      <Typography.Title level={4}>{i18n("UserPasswordPage.page.title")}</Typography.Title>
      <Alert
        message={i18n("UserPasswordPage.alert.title")}
        description={
          <Typography.Paragraph>
            {i18n("UserPasswordPage.alert.description")}
              <ul>
                <li>{i18n("UserPasswordPage.alert.rule1")}</li>
                <li>{i18n("UserPasswordPage.alert.rule2")}</li>
                <li>{i18n("UserPasswordPage.alert.rule3")}</li>
                <li>{i18n("UserPasswordPage.alert.rule4")}</li>
                <li>{i18n("UserPasswordPage.alert.rule5")}</li>
                <li>{i18n("UserPasswordPage.alert.rule6")}</li>
                <li>{i18n("UserPasswordPage.alert.rule7")}</li>
                <li>{i18n("UserPasswordPage.alert.rule8")}</li>
              </ul>
            </Typography.Paragraph>
          }
          type="error"
          showIcon
        />
      <Form
        layout="vertical"
        autoComplete="off"
      >
        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: 'Password canot be blank.' }]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label="Confirm Password"
          name="confirmPassword"
          rules={[{ required: true, message: 'Confirm password canot be blank.' }]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
          >
            Submit
          </Button>
        </Form.Item>
      </Form>
    </Space>
  )
}
