import React from "react";
import { Button, Form, Input } from "antd";
import { IconLocked, IconUser } from "../icons/Icons";
import { blue6 } from "../../styles/colors";
import { setBaseUrl } from "../../utilities/url-utilities";

export const LoginFrom = () => {
  const [form] = Form.useForm();

  const onFinish = values => {
    console.log(values);
    document.getElementById("loginForm").submit();
  };

  return (
    <Form
      form={form}
      onFinish={onFinish}
      action={`${window.PAGE.BASE_URL}login`}
      name="loginForm"
      method="POST"
    >
      <Form.Item
        rules={[
          {
            required: true,
            message: i18n("LoginPage.username.required")
          }
        ]}
      >
        <Input name="username" prefix={<IconUser style={{ color: blue6 }} />} />
      </Form.Item>
      <Form.Item
        rules={[
          {
            required: true,
            message: i18n("LoginPage.password.required")
          }
        ]}
      >
        <Input
          name="password"
          prefix={<IconLocked style={{ color: blue6 }} />}
          type="password"
        />
      </Form.Item>
      <Form.Item>
        <Button id="t-submit-btn" type="primary" block htmlType="submit">
          {i18n("LoginPage.submit")}
        </Button>
      </Form.Item>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between"
        }}
      >
        <a type="link" href={setBaseUrl(`password_reset`)}>
          {i18n("LoginPage.forgot")}
        </a>

        <a href={setBaseUrl(`password_reset/activate`)}>
          {i18n("LoginPage.activate")}
        </a>
      </div>
    </Form>
  );
};
