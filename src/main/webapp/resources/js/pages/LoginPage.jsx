import React, { useState } from "react";
import { render } from "react-dom";
import { Alert, Button, Form, Input } from "antd";
import { blue6, grey1 } from "../styles/colors";
import { IconLocked, IconUser } from "../components/icons/Icons";

function LoginPage({ form }) {
  const [loading, setLoading] = useState(false);

  const { getFieldDecorator, getFieldsError } = form;
  const hasErrors = ((fieldsError = getFieldsError()) =>
    Object.keys(fieldsError).some(field => fieldsError[field]))();

  function handleSubmit() {
    if (!hasErrors) {
      setLoading(true);
      document.forms["loginForm"].submit();
    }
  }

  return (
    <>
      <img src="/resources/img/irida_logo_light.svg" alt="" />
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
        action={`${window.PAGE.BASE_URL}login`}
        name="loginForm"
        method="POST"
      >
        <Form.Item label={i18n("LoginPage.username")}>
          {getFieldDecorator("username", {
            rules: [
              {
                required: true,
                message: i18n("LoginPage.username.required")
              }
            ]
          })(
            <Input
              name="username"
              style={{ backgroundColor: grey1 }}
              prefix={<IconUser style={{ color: blue6 }} />}
            />
          )}
        </Form.Item>
        <Form.Item label={i18n("LoginPage.password")}>
          {getFieldDecorator("password", {
            rules: [
              {
                required: true,
                message: i18n("LoginPage.password.required")
              }
            ]
          })(
            <Input
              name="password"
              style={{ backgroundColor: grey1 }}
              prefix={<IconLocked style={{ color: blue6 }} />}
              type="password"
            />
          )}
        </Form.Item>
        <Form.Item>
          <Button
            id="t-submit-btn"
            type="primary"
            block
            loading={loading}
            htmlType="submit"
            disabled={hasErrors}
            onClick={handleSubmit}
          >
            {i18n("LoginPage.submit")}
          </Button>
        </Form.Item>
        <Form.Item style={{ display: "flex", justifyContent: "space-between" }}>
          <Button type="link" href={`${window.PAGE.BASE_URL}password_reset`}>
            {i18n("LoginPage.forgot")}
          </Button>

          <Button
            type="link"
            href={`${window.PAGE.BASE_URL}password_reset/activate`}
          >
            {i18n("LoginPage.activate")}
          </Button>
        </Form.Item>
      </Form>
    </>
  );
}

const WrappedLoginForm = Form.create({ name: "loginForm" })(LoginPage);

render(<WrappedLoginForm />, document.querySelector("#login-root"));
