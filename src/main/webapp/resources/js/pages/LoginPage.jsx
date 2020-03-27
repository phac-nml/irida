import React from "react";
import { render } from "react-dom";
import { Button, Col, Form, Input, Row, Typography } from "antd";
import { IconLocked, IconUser } from "../components/icons/Icons";
import { setBaseUrl } from "../utilities/url-utilities";
import { SPACE_MD } from "../styles/spacing";

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
    >
      <Item
        rules={[
          {
            required: true,
            message: i18n("LoginPage.username.required")
          }
        ]}
      >
        <Input name="username" prefix={<IconUser />} />
      </Item>
      <Item>
        <Input name="password" prefix={<IconLocked />} type="password" />
      </Item>
      <Item>
        <Button type="primary" block htmlType="submit">
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
            alt="IRIDA"
          />
        </Row>
        <LoginForm />
      </Col>
    </Row>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
