import React, { useEffect, useRef } from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, Row, Typography } from "antd";
import { IconLocked, IconMail, IconUser } from "../components/icons/Icons";
import { setBaseUrl } from "../utilities/url-utilities";
import { SPACE_MD } from "../styles/spacing";
import { blue6 } from "../styles/colors";
import {
  useCreatePasswordResetEmailMutation,
  useActivateAccountMutation,
} from "../apis/passwordReset";
import { Provider } from "react-redux";
import store from "./store";
import { InfoAlert } from "../components/alerts";

const { Item } = Form;

const { Title } = Typography;

/**
 * React component to render the login form
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
          placeholder="Username"
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
          placeholder="Password"
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
                  history.pushState("forgot", "Forgot Password", "/forgot");
                }}
                style={{ padding: 0 }}
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
                    "/activate"
                  );
                }}
                style={{ marginLeft: 10 }}
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

function ActivationPage({ updateDisplayLoginPage }) {
  const [activateAccount] = useActivateAccountMutation();
  const [activateAccountForm] = Form.useForm();
  const [messageAlert, setMessageAlert] = React.useState(false);
  const [message, setMessage] = React.useState("");

  const handleSubmit = () => {
    activateAccount({
      identifier: activateAccountForm.getFieldValue("activationId"),
    }).then((res) => {
      console.log(res);
      if (res.error) {
        setMessageAlert(true);
        setMessage("Invalid activation ID");
      } else {
        window.location.replace(
          setBaseUrl(`/password_reset/${res.data.identifier}`)
        );
      }
    });
  };

  return (
    <div>
      {messageAlert && (
        <InfoAlert message={message} style={{ marginTop: SPACE_MD }} />
      )}
      <Form
        name="accountActivationForm"
        form={activateAccountForm}
        size="large"
        style={{ marginTop: SPACE_MD }}
      >
        <Item
          name="activationId"
          rules={[
            {
              required: true,
              message: "Activation ID is required",
            },
          ]}
        >
          <Input name="activationId" placeholder="Activation ID" />
        </Item>

        <Item>
          <Button
            id="t-submit-btn"
            type="primary"
            block
            onClick={() => handleSubmit()}
          >
            Submit
          </Button>
        </Item>
      </Form>
      <Button
        type="link"
        style={{ padding: 0 }}
        onClick={() => {
          updateDisplayLoginPage(true);
          window.history.back();
        }}
      >
        Return to Login Page
      </Button>
    </div>
  );
}

function ForgotPasswordPage({ updateDisplayLoginPage }) {
  const [resetPassword] = useCreatePasswordResetEmailMutation();
  const [resetPasswordForm] = Form.useForm();
  const [messageAlert, setMessageAlert] = React.useState(false);
  const [message, setMessage] = React.useState("");

  const submitResetEmail = () => {
    resetPassword({ email: resetPasswordForm.getFieldValue("email") }).then(
      (res) => {
        console.log(res);
        resetPasswordForm.resetFields();
        setMessageAlert(true);
        if (res.error) {
          setMessage(res.error.data.error);
        } else {
          setMessage(res.data.message);
        }
      }
    );
  };

  return (
    <div>
      {messageAlert && (
        <InfoAlert message={message} style={{ marginTop: SPACE_MD }} />
      )}
      <Form
        name="forgotPasswordForm"
        form={resetPasswordForm}
        size="large"
        style={{ marginTop: SPACE_MD }}
      >
        <Item
          name="email"
          rules={[
            {
              required: true,
              message: "Email address is required",
            },
          ]}
        >
          <Input
            name="email"
            type="email"
            prefix={<IconMail style={{ color: blue6 }} />}
            placeholder="Username or Email Address"
            disabled={messageAlert}
          />
        </Item>

        <Item>
          <Button
            id="t-submit-btn"
            type="primary"
            onClick={() => submitResetEmail()}
            disabled={messageAlert}
            block
          >
            Submit
          </Button>
        </Item>
      </Form>
      <Button
        type="link"
        style={{ padding: 0 }}
        onClick={() => {
          updateDisplayLoginPage(true);
          window.history.back();
        }}
      >
        Return to Login Page
      </Button>
    </div>
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
            <ForgotPasswordPage
              updateDisplayLoginPage={updateDisplayLoginPage}
            />
          ) : type === "activate-account" ? (
            <ActivationPage updateDisplayLoginPage={updateDisplayLoginPage} />
          ) : null}
        </Provider>
      </Col>
    </Row>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
