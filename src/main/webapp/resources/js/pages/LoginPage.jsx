import React, { useState } from "react";
import { render } from "react-dom";
import { Alert, Button, Form, Icon, Input } from "antd";
import { grey6 } from "../styles/colors";

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
    <div
      style={{
        padding: "1rem",
        width: 400
      }}
    >
      <div dangerouslySetInnerHTML={{ __html: window.PAGE.logo }} />
      {window.PAGE.hasErrors ? (
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
        <Form.Item>
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
              prefix={<Icon type="user" style={{ color: grey6 }} />}
              placeholder={i18n("LoginPage.username")}
            />
          )}
        </Form.Item>
        <Form.Item>
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
              prefix={<Icon type="lock" style={{ color: grey6 }} />}
              type="password"
              placeholder={i18n("LoginPage.password")}
            />
          )}
        </Form.Item>
        <Form.Item>
          <Button
            id="t-submit-btn"
            type="primary"
            loading={loading}
            htmlType="submit"
            disabled={hasErrors}
            block
            onClick={() => handleSubmit()}
          >
            {i18n("LoginPage.submit")}
          </Button>
          {window.PAGE.emailConfigured ? (
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <a href={`${window.PAGE.BASE_URL}password_reset`}>
                {i18n("LoginPage.forgot")}
              </a>

              <a href={`${window.PAGE.BASE_URL}password_reset/activate`}>
                {i18n("LoginPage.activate")}
              </a>
            </div>
          ) : null}
        </Form.Item>
      </Form>
    </div>
  );
}

const WrappedLoginForm = Form.create({ name: "loginForm" })(LoginPage);

render(<WrappedLoginForm />, document.querySelector("#root"));
