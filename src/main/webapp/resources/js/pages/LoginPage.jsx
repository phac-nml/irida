import React, { useState } from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Icon, Input, Row } from "antd";
import { getI18N } from "../utilities/i18n-utilties";
import { grey1 } from "../styles/colors";

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
    <Row
      style={{
        minHeight: "100vh",
        backgroundColor: grey1,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "1rem"
      }}
      type="flex"
    >
      <Col
        style={{
          padding: "1rem",
          maxWidth: 400,
          minWidth: 250
        }}
        xs={24}
        xl={6}
      >
        <img
          style={{ marginBottom: 24 }}
          src={window.PAGE.logo}
          alt="IRIDA Platform"
        />
        {window.PAGE.hasErrors ? (
          <Alert
            style={{ marginBottom: 28 }}
            type="error"
            message={
              <span className="t-login-error">
                  {getI18N("LoginPage.error.message")}
                </span>
            }
            description={getI18N("LoginPage.error.description")}
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
                  message: getI18N("LoginPage.username.required")
                }
              ]
            })(
              <Input
                name="username"
                prefix={
                  <Icon type="user" style={{ color: "rgba(0,0,0,.25)" }} />
                }
                placeholder={getI18N("LoginPage.username")}
              />
            )}
          </Form.Item>
          <Form.Item>
            {getFieldDecorator("password", {
              rules: [
                {
                  required: true,
                  message: getI18N("LoginPage.password.required")
                }
              ]
            })(
              <Input
                name="password"
                prefix={
                  <Icon type="lock" style={{ color: "rgba(0,0,0,.25)" }} />
                }
                type="password"
                placeholder={getI18N("LoginPage.password")}
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
              {getI18N("LoginPage.submit")}
            </Button>
            {window.PAGE.emailConfigured ? (
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <a href={`${window.PAGE.BASE_URL}password_reset`}>
                  {getI18N("LoginPage.forgot")}
                </a>

                <a href={`${window.PAGE.BASE_URL}password_reset/activate`}>
                  {getI18N("LoginPage.activate")}
                </a>
              </div>
            ) : null}
          </Form.Item>
        </Form>
      </Col>
    </Row>
  );
}

const WrappedLoginForm = Form.create({ name: "loginForm" })(LoginPage);

render(<WrappedLoginForm />, document.querySelector("#root"));
