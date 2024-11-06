import React from "react";
import { Button, Form, Input, InputRef } from "antd";
import { MailOutlined } from "@ant-design/icons";
import { InfoAlert } from "../../components/alerts";
import { SPACE_MD } from "../../styles/spacing";
import { blue6 } from "../../styles/colors";
import { useCreatePasswordResetEmailMutation } from "../../apis/password-reset";

const { Item } = Form;

export interface ForgotPasswordProps {
  updateDisplayLoginPage: (value: boolean) => void;
}

/**
 * React component to render the forgot password form
 * @param updateDisplayLoginPage Function to update whether to display login page
 * @constructor
 */
export function ForgotPassword({
  updateDisplayLoginPage,
}: ForgotPasswordProps): JSX.Element {
  const [forgotPassword] = useCreatePasswordResetEmailMutation();
  const [forgotPasswordForm] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [message, setMessage] = React.useState(null);
  const usernameOrEmailRef = React.useRef<InputRef>(null);

  /**
   * When the component gets added to the page,
   * focus on the usernameOrEmail input.
   */
  React.useEffect(() => {
    if (usernameOrEmailRef.current !== null) {
      usernameOrEmailRef.current.focus();
      usernameOrEmailRef.current.select();
    }
  }, [usernameOrEmailRef]);

  const submitForgotPasswordForm = () => {
    setLoading(true);
    setMessage(null);

    forgotPassword({
      usernameOrEmail: forgotPasswordForm.getFieldValue("usernameOrEmail"),
    })
      .unwrap()
      .then((response) => {
        setMessage(response.message);
      })
      .catch((error) => setMessage(error.data.error))
      .finally(() => {
        forgotPasswordForm.resetFields();
        setLoading(false);
      });
  };

  return (
    <>
      {message !== null && (
        <InfoAlert
          message={message}
          className={"t-forgot-password-alert"}
          style={{ marginTop: SPACE_MD }}
        />
      )}
      <Form
        name="forgotPasswordForm"
        form={forgotPasswordForm}
        onFinish={submitForgotPasswordForm}
        size="large"
        style={{ marginTop: SPACE_MD }}
      >
        <Item
          name="usernameOrEmail"
          rules={[
            {
              required: true,
              message: i18n("ForgotPassword.credentialsRequired"),
            },
          ]}
        >
          <Input
            name="usernameOrEmail"
            id="usernameOrEmail"
            ref={usernameOrEmailRef}
            prefix={<MailOutlined style={{ color: blue6 }} />}
            placeholder={i18n("ForgotPassword.input.placeholder")}
            disabled={loading}
          />
        </Item>

        <Item>
          <Button
            className="t-submit-btn"
            type="primary"
            disabled={loading}
            loading={loading}
            block
            htmlType="submit"
          >
            {i18n("ForgotPassword.button.resetPassword")}
          </Button>
        </Item>
      </Form>
      <Button
        className="t-return-to-login"
        type="link"
        style={{ padding: 0 }}
        onClick={() => {
          updateDisplayLoginPage(true);
          window.history.back();
        }}
      >
        {i18n("ForgotPassword.link.returnToLogin")}
      </Button>
    </>
  );
}
