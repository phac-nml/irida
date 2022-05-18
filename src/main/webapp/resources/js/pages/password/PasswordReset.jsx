import React from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, List, Row, Typography } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconLocked } from "../../components/icons/Icons";
import { blue6 } from "../../styles/colors";
import { SPACE_MD, SPACE_SM } from "../../styles/spacing";
import { useSetPasswordMutation } from "../../apis/passwordReset";
import store from "../store";
import { Provider } from "react-redux";
const { Item } = Form;

const params = new URLSearchParams(window.location.search);

const passwordExpired = params.get("expired") || false;
const passwordResetObj = window.PAGE.passwordReset;

/**
 * React component to layout the Password Reset page.
 * @returns {*}
 * @constructor
 */
function PasswordResetForm() {
  const [form] = Form.useForm();
  const [setPassword] = useSetPasswordMutation();

  const [updateSucess, setUpdateSuccess] = React.useState(false);
  const [updateError, setUpdateError] = React.useState(false);

  const passwordRules = [
    i18n("UserChangePasswordForm.alert.rule2"),
    i18n("UserChangePasswordForm.alert.rule3"),
    i18n("UserChangePasswordForm.alert.rule4"),
    i18n("UserChangePasswordForm.alert.rule5"),
    i18n("UserChangePasswordForm.alert.rule6"),
    "Passwords for admins are recommended to be atleast 11 characters",
    "Passwords should not form any words or contain any personal information",
  ];

  const handleSubmit = () => {
    setPassword({
      resetId: passwordResetObj.id,
      password: form.getFieldValue("password"),
    })
      .then((res) => {
        console.log(res);
        if (res.data.message === "success") {
          console.log(res);
          setUpdateSuccess(true);
        } else {
          setUpdateError(true);
        }
      })
      .catch((error) => {
        console.log(error);
        setUpdateError(true);
      });
  };

  return (
    <Row justify="center">
      <Col style={{ width: 600 }}>
        <Row justify="center" style={{ marginBottom: SPACE_MD }}>
          <img
            src={setBaseUrl("/resources/img/irida_logo_light.svg")}
            height={60}
            alt={i18n("generic.irida.website")}
          />
        </Row>

        {!updateSucess
          ? passwordExpired && (
              <Alert
                style={{ marginBottom: SPACE_SM }}
                message={i18n("password.reset.password_expired")}
                type="error"
                showIcon
              />
            )
          : null}

        {!updateSucess && (
          <Alert
            style={{ marginBottom: SPACE_SM }}
            message={i18n("UserChangePasswordForm.alert.title")}
            description={
              <Typography.Paragraph>
                <List
                  header={i18n("UserChangePasswordForm.alert.description")}
                  dataSource={passwordRules}
                  renderItem={(item) => <List.Item>- {item}</List.Item>}
                />
              </Typography.Paragraph>
            }
            type="info"
            showIcon
          />
        )}

        {updateSucess && (
          <Alert
            style={{ marginBottom: SPACE_SM }}
            message={i18n("password.reset.success")}
            action={
              <Button
                type="link"
                onClick={() => window.location.replace(setBaseUrl("/"))}
              >
                {i18n("password.reset.success.link")}
              </Button>
            }
            type="success"
            showIcon
          />
        )}

        <Form form={form} name="resetPasswordForm" size="large">
          <Item
            name="password"
            rules={[
              {
                required: true,
                message: "Password is required",
              },
            ]}
          >
            <Input
              name="password"
              type="password"
              prefix={<IconLocked style={{ color: blue6 }} />}
              placeholder="New Password"
              disabled={updateSucess}
            />
          </Item>
          <Item>
            <Button
              id="t-submit-btn"
              type="primary"
              disabled={updateSucess}
              block
              onClick={() => handleSubmit()}
            >
              Set Password
            </Button>
          </Item>
        </Form>
        <Button
          type="link"
          style={{ padding: 0 }}
          onClick={() => window.location.replace(setBaseUrl("/login"))}
        >
          Return to Login Page
        </Button>
      </Col>
    </Row>
  );
}

export default function PasswordReset() {
  return (
    <Provider store={store}>
      <PasswordResetForm />
    </Provider>
  );
}

render(<PasswordReset />, document.querySelector("#root"));
