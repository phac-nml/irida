import React, { useEffect, useRef } from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, Row } from "antd";
import { IconLocked, IconUser } from "../components/icons/Icons";
import { setBaseUrl } from "../utilities/url-utilities";
import { SPACE_MD } from "../styles/spacing";
import { blue6 } from "../styles/colors";

import { Provider } from "react-redux";
import store from "./store";

import { ForgotPassword } from "./password/ForgotPassword";
import { ActivateAccount } from "./password/ActivateAccount";

const { Item } = Form;

/**
 * React component to render the login form
 * @param {function} updateDisplayLoginPage Function to update whether to display login page
 * @param {function} updatePageType Function to update the page type
 * @returns {*}
 * @constructor
 */
function LoginForm({ updateDisplayLoginPage, updatePageType }) {
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
          placeholder={i18n("LoginPage.username")}
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
          placeholder={i18n("LoginPage.password")}
        />
      </Item>
      <Item>
        <Button id="t-submit-btn" type="primary" block htmlType="submit">
          {i18n("LoginPage.submit")}
        </Button>
      </Item>
      {window.TL.emailConfigured ? (
        <Item>
          <Row justify="space-between">
            <Col>
              <Button
                type="link"
                onClick={() => {
                  updateDisplayLoginPage(false);
                  updatePageType("forgot-password");
                  history.pushState(
                    "forgot",
                    "Forgot Password",
                    "/forgot_password"
                  );
                }}
                style={{ padding: 0, marginLeft: 15 }}
              >
                {i18n("LoginPage.forgot")}
              </Button>

              <Button
                type="link"
                onClick={() => {
                  updateDisplayLoginPage(false);
                  updatePageType("activate-account");
                  history.pushState(
                    "activate",
                    "Activate Account",
                    "/activate_account"
                  );
                }}
                style={{ padding: 0, marginLeft: 25 }}
              >
                {i18n("LoginPage.activate")}
              </Button>
            </Col>
          </Row>
        </Item>
      ) : null}
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
  const [displayLoginPage, setDisplayLoginPage] = React.useState(true);
  const [type, setType] = React.useState(null);

  const updateDisplayLoginPage = (value) => {
    setDisplayLoginPage(value);
  };

  const updatePageType = (pageType) => {
    setType(pageType);
  };

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
                <Button onClick={() => setDisplayLoginPage(false)}>
                  {i18n("LoginPage.recover")}
                </Button>
              </>
            }
            showIcon
          />
        ) : null}
        <Provider store={store}>
          {displayLoginPage ? (
            <LoginForm
              updateDisplayLoginPage={updateDisplayLoginPage}
              updatePageType={updatePageType}
            />
          ) : type === "forgot-password" ? (
            <ForgotPassword updateDisplayLoginPage={updateDisplayLoginPage} />
          ) : type === "activate-account" ? (
            <ActivateAccount updateDisplayLoginPage={updateDisplayLoginPage} />
          ) : null}
        </Provider>
      </Col>
    </Row>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
