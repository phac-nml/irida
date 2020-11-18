import React, { useRef, useEffect } from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, Row } from "antd";
import { IconLocked, IconUser } from "../components/icons/Icons";
import { setBaseUrl } from "../utilities/url-utilities";
import { SPACE_MD } from "../styles/spacing";
import { blue6 } from "../styles/colors";

const { Item } = Form;

/**
 * React component to render the login form
 * @returns {*}
 * @constructor
 */
function LoginForm() {
  const [form] = Form.useForm();
  const usernameRef = useRef();

  /**
   * When the component gets added to the page,
   * focus on the username input.
   */
  useEffect(() => {
    usernameRef.current.focus();
    usernameRef.current.select();
  }, []);

  /**
   * Handler for submitting the login form once all fields are correctly filled out
   * @returns {*}
   */
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
        name="username"
        rules={[
          {
            required: true,
            message: i18n("LoginPage.username.required"),
          },
        ]}
      >
        <Input
          name="username"
          ref={usernameRef}
          prefix={<IconUser style={{ color: blue6 }} />}
        />
      </Item>
      <Item
        name="password"
        rules={[
          {
            required: true,
            message: i18n("LoginPage.password.required"),
          },
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
      {window.TL.emailConfigured ? <Item>
        <Row justify="space-between">
          <a href={setBaseUrl(`password_reset`)}>{i18n("LoginPage.forgot")}</a>
          <a href={setBaseUrl(`password_reset/activate`)}>
            {i18n("LoginPage.activate")}
          </a>
        </Row>
      </Item> : null}
    </Form>
  );
}

/**
 * React component to layout the Login Page.
 * Responsible for displaying any errors that are returned from the server.
 * @returns {*}
 * @constructor
 */
function LoginPage() {
  const urlParams = new URLSearchParams(window.location.search);
  return (
    <Row justify="center">
      <Col style={{ width: 300 }}>
        <Row justify="center" style={{ marginBottom: SPACE_MD }}>
          <img
            src={setBaseUrl("/resources/img/irida_logo_light.svg")}
            height={60}
            alt={i18n("generic.irida.website")}
          />
        </Row>
        {urlParams.has("error") ? (
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
