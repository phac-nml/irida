import React from "react";
import { render } from "react-dom";
import styled from "styled-components";
import { Form, Icon, Input, Layout } from "antd";
import { grey1 } from "../styles/colors";
import { SPACE_MD } from "../styles/spacing";

const LoginContent = styled(Layout.Content)`
  display: flex;
  align-items: center;
  justify-content: center;
`;

const LoginWrapper = styled.div`
  background-color: ${grey1};
  padding: ${SPACE_MD};
`;

function LoginPage({ form }) {
  const { getFieldDecorator } = form;
  return (
    <Layout style={{ minHeight: "100vh" }}>
      <LoginContent style={{ display: "flex" }}>
        <LoginWrapper>
          <div>
            <img src={`/resources/img/irida_logo_dark.svg`} />
          </div>
          <Form>
            <Form.Item>
              {getFieldDecorator("username", {
                rules: [
                  { required: true, message: "Please input your username!" }
                ]
              })(
                <Input
                  prefix={
                    <Icon type="user" style={{ color: "rgba(0,0,0,.25)" }} />
                  }
                  placeholder="Username"
                />
              )}
            </Form.Item>
            <Form.Item>
              {getFieldDecorator("password", {
                rules: [
                  { required: true, message: "Please input your Password!" }
                ]
              })(
                <Input
                  prefix={
                    <Icon type="lock" style={{ color: "rgba(0,0,0,.25)" }} />
                  }
                  type="password"
                  placeholder="Password"
                />
              )}
            </Form.Item>
          </Form>
        </LoginWrapper>
      </LoginContent>
    </Layout>
  );
}

const WrappedLoginForm = Form.create({ name: "login_form" })(LoginPage);

render(<WrappedLoginForm />, document.querySelector("#root"));
