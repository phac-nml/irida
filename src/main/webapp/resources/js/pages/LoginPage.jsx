import React, { useState } from "react";
import { render } from "react-dom";
import styled from "styled-components";
import { Alert, Button, Card, Form, Icon, Input, Layout } from "antd";
import { grey1 } from "../styles/colors";
import { SPACE_MD } from "../styles/spacing";
import { getI18N } from "../utilities/i18n-utilties";

const LayoutBackground = styled(Layout)`
  min-height: 100vh;
`;

const LoginContent = styled(Layout.Content)`
  display: flex;
  align-items: center;
  justify-content: center;
`;

const LoginCard = styled(Card)`
  width: 400px;
  background-color: ${grey1};
`;

const Links = styled.div`
  display: flex;
  justify-content: space-between;
`;

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
    <LayoutBackground>
      <LoginContent>
        <LoginCard>
          <div style={{ marginBottom: SPACE_MD }}>
            <img src={window.PAGE.logo} />
          </div>
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
                <Links>
                  <a href={`${window.PAGE.BASE_URL}password_reset`}>
                    {getI18N("LoginPage.forgot")}
                  </a>

                  <a href={`${window.PAGE.BASE_URL}password_reset/activate`}>
                    {getI18N("LoginPage.activate")}
                  </a>
                </Links>
              ) : null}
            </Form.Item>
          </Form>
        </LoginCard>
      </LoginContent>
    </LayoutBackground>
  );
}

const WrappedLoginForm = Form.create({ name: "loginForm" })(LoginPage);

render(<WrappedLoginForm />, document.querySelector("#root"));
