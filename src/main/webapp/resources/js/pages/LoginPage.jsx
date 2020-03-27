import React from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, Row, Typography } from "antd";
import { IconLocked, IconUser } from "../components/icons/Icons";
import { setBaseUrl } from "../utilities/url-utilities";
import { SPACE_MD } from "../styles/spacing";
import { blue6 } from "../styles/colors";

const { Item } = Form;

function LoginForm() {
  const [form] = Form.useForm();

  const onFinish = () => document.getElementById("loginForm").submit();

  return (
    <Form
      form={form}
      onFinish={onFinish}
      name="loginForm"
      action={setBaseUrl(`/login`)}
      method="POST"
      size="large"
    >
      <Item
        rules={[
          {
            required: true,
            message: i18n("LoginPage.username.required")
          }
        ]}
      >
        <Input name="username" prefix={<IconUser style={{ color: blue6 }} />} />
      </Item>
      <Item
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
      </Item>
      <Item>
        <Button id="t-submit-btn" type="primary" block htmlType="submit">
          {i18n("LoginPage.submit")}
        </Button>
      </Item>
      <Item>
        <Row justify="space-between">
          <a href={setBaseUrl(`password_reset`)}>{i18n("LoginPage.forgot")}</a>
          <a href={setBaseUrl(`password_reset/activate`)}>
            {i18n("LoginPage.activate")}
          </a>
        </Row>
      </Item>
    </Form>
  );
}

function LoginPage() {
  return (
    <Row justify="center">
      <Col
        lg={{ span: 6 }}
        md={{ span: 12 }}
        sm={{ span: 16 }}
        xs={{ span: 20 }}
      >
        <Row justify="center" style={{ marginBottom: SPACE_MD }}>
          <img
            src={setBaseUrl("/resources/img/irida_logo_light.svg")}
            height={60}
            alt={i18n("generic.irida.website")}
          />
        </Row>
        {window.PAGE?.hasErrors ? (
          <Alert
            type="error"
            className="t-login-error"
            style={{ marginBottom: SPACE_MD }}
            message={
              <span className="t-login-error">
                {i18n("LoginPage.error.message")}
              </span>
            }
            description={
              <>
                {i18n("LoginPage.error.description")}{" "}
                <a href={setBaseUrl("password_reset")}>
                  {i18n("LoginPage.recover")}
                </a>
              </>
            }
            showIcon
          />
        ) : null}
        <LoginForm />
      </Col>
    </Row>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
