import React, { useState } from "react";
import { render } from "react-dom";
import { Alert, Button, Form, Input } from "antd";
import { blue6 } from "../styles/colors";
import { IconLocked, IconUser } from "../components/icons/Icons";

function LoginPage() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const onFinish = values => {
    console.log(values);
  };

  return (
    <>
      <img
        src="/resources/img/irida_logo_light.svg"
        alt=""
        style={{ marginBottom: 10 }}
      />
      {window.PAGE?.hasErrors ? (
        <Alert
          style={{ margin: `18px 0 28px 0` }}
          type="error"
          message={
            <span className="t-login-error">
              {i18n("LoginPage.error.message")}
            </span>
          }
          description={i18n("LoginPage.error.description")}
          showIcon
          closable
        />
      ) : null}

      <Form
        form={form}
        // onFinish={onFinish}
        action={`${window.PAGE.BASE_URL}login`}
        name="loginForm"
        method="POST"
      >
        <Form.Item
          name="username"
          rules={[
            {
              required: true,
              message: i18n("LoginPage.username.required")
            }
          ]}
        >
          <Input prefix={<IconUser style={{ color: blue6 }} />} />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[
            {
              required: true,
              message: i18n("LoginPage.password.required")
            }
          ]}
        >
          <Input
            prefix={<IconLocked style={{ color: blue6 }} />}
            type="password"
          />
        </Form.Item>
        <Form.Item>
          <Button
            id="t-submit-btn"
            type="primary"
            block
            loading={loading}
            htmlType="submit"
          >
            {i18n("LoginPage.submit")}
          </Button>
        </Form.Item>
        <Form.Item>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between"
            }}
          >
            <a type="link" href={`${window.PAGE.BASE_URL}password_reset`}>
              {i18n("LoginPage.forgot")}
            </a>

            <a href={`${window.PAGE.BASE_URL}password_reset/activate`}>
              {i18n("LoginPage.activate")}
            </a>
          </div>
        </Form.Item>
      </Form>
    </>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
