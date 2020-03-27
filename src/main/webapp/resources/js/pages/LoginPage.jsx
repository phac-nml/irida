import React from "react";
import { render } from "react-dom";
import { Col, Form, Input, Row, Typography } from "antd";
import { IconLocked, IconUser } from "../components/icons/Icons";

const { Item } = Form;

function LoginForm() {
  const [form] = Form.useForm();

  return (
    <Form form={form}>
      <Item name="username">
        <Input prefix={<IconUser />} />
      </Item>
      <Item name="password">
        <Input prefix={<IconLocked />} type="password" />
      </Item>
    </Form>
  );
}

function LoginPage() {
  return (
    <Row justify="center">
      <Col span={12}>
        <Row justify="center">
          <Typography.Title>IRIDA</Typography.Title>
        </Row>
        <LoginForm />
      </Col>
    </Row>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
