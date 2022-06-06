import React from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, List, Row, Typography } from "antd";
import {
  EyeInvisibleOutlined,
  EyeTwoTone,
  LockOutlined,
} from "@ant-design/icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import { blue6 } from "../../styles/colors";
import { SPACE_MD, SPACE_SM } from "../../styles/spacing";
import { useSetPasswordMutation } from "../../apis/password-reset";
import store from "../store";
import { Provider } from "react-redux";
import { validatePassword } from "../../utilities/validation-utilities";

const { Item } = Form;
const passwordExpired =
  new URLSearchParams(window.location.search).get("expired") || false;

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

/**
 * React component to render the Password Reset form.
 * @returns {*}
 * @constructor
 */
function PasswordResetForm() {
  const [passwordResetForm] = Form.useForm();
  const [setPassword] = useSetPasswordMutation();

  const [loading, setLoading] = React.useState(false);
  const [invalidPassword, setInvalidPassword] = React.useState(false);
  const [updateSuccess, setUpdateSuccess] = React.useState(false);
  const [updateError, setUpdateError] = React.useState(false);
  const [errorMessages, setErrorMessages] = React.useState(null);
  const passwordRef = React.useRef();

  /**
   * When the component gets added to the page,
   * focus on the password input input.
   */
  React.useEffect(() => {
    passwordRef.current.focus();
    passwordRef.current.select();
  }, []);

  const passwordRules = [
    i18n("PasswordReset.alert.rule2"),
    i18n("PasswordReset.alert.rule3"),
    i18n("PasswordReset.alert.rule4"),
    i18n("PasswordReset.alert.rule5"),
    i18n("PasswordReset.alert.rule6"),
    i18n("PasswordReset.alert.recommendation1"),
    i18n("PasswordReset.alert.recommendation2"),
  ];

  const submitPasswordResetForm = () => {
    setLoading(true);
    setPassword({
      resetId: passwordResetObj.id,
      password: passwordResetForm.getFieldValue("password"),
    })
      .unwrap()
      .then(() => {
        setUpdateSuccess(true);
        setUpdateError(false);
      })
      .catch((error) => {
        setErrorMessages(error.data.errors);
        setUpdateError(true);
      })
      .finally(() => {
        passwordResetForm.resetFields();
        setLoading(false);
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

        {updateSuccess && (
          <Alert
            style={{ marginBottom: SPACE_SM }}
            message={i18n("PasswordReset.alert.success")}
            className="t-reset-success-alert"
            action={
              <Button
                type="link"
                onClick={() => window.location.replace(setBaseUrl("/"))}
              >
                {i18n("PasswordReset.alert.success.link")}
              </Button>
            }
            type="success"
            showIcon
          />
        )}

        {updateError && (
          <Alert
            style={{ marginBottom: SPACE_SM }}
            className="t-reset-error-alert"
            message={i18n("PasswordReset.followingErrorsOccurred")}
            description={
              <Typography.Paragraph>
                {
                  <List>
                    {Object.keys(errorMessages).map(function (keyName) {
                      return (
                        <List.Item key={keyName}>
                          {errorMessages[keyName]}
                        </List.Item>
                      );
                    })}
                  </List>
                }
              </Typography.Paragraph>
            }
            type="error"
            showIcon
          />
        )}

        {!updateSuccess
          ? passwordExpired && (
              <Alert
                style={{ marginBottom: SPACE_SM }}
                className="t-password-expired-alert"
                message={i18n("PasswordReset.password.expired")}
                type="error"
                showIcon
              />
            )
          : null}

        {!updateSuccess && (
          <>
            <Alert
              style={{ marginBottom: SPACE_SM }}
              message={i18n("PasswordReset.alert.title")}
              className="t-password-policy-alert"
              description={
                <Typography.Paragraph>
                  <List
                    header={i18n("PasswordReset.alert.description")}
                    dataSource={passwordRules}
                    renderItem={(item) => <List.Item>- {item}</List.Item>}
                  />
                </Typography.Paragraph>
              }
              type="info"
              showIcon
            />
            <Form
              form={passwordResetForm}
              name="resetPasswordForm"
              size="large"
              onFinish={submitPasswordResetForm}
            >
              <Item
                name="password"
                rules={[
                  ({}) => ({
                    validator(_, value) {
                      let validationResult = validatePassword(value);
                      validationResult
                        .then(() => setInvalidPassword(false))
                        .catch(() => setInvalidPassword(true));
                      return validationResult;
                    },
                  }),
                ]}
              >
                <Input.Password
                  name="password"
                  id="password"
                  ref={passwordRef}
                  prefix={<LockOutlined style={{ color: blue6 }} />}
                  placeholder={i18n("PasswordReset.input.placeholder")}
                  disabled={loading}
                  iconRender={(visible) =>
                    visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
                  }
                />
              </Item>
              <Item>
                <Button
                  className="t-submit-btn"
                  type="primary"
                  disabled={loading || invalidPassword}
                  loading={loading}
                  block
                  htmlType="submit"
                >
                  {i18n("PasswordReset.button.setPassword")}
                </Button>
              </Item>
            </Form>
          </>
        )}
      </Col>
    </Row>
  );
}

/**
 * React component to render the Password Reset page and provide the store.
 * @returns {*}
 * @constructor
 */
export default function PasswordReset() {
  return (
    <Provider store={store}>
      <PasswordResetForm />
    </Provider>
  );
}

render(<PasswordReset />, document.querySelector("#root"));
