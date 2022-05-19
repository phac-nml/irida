import { useCreatePasswordResetEmailMutation } from "../../apis/passwordReset";
import { Button, Form, Input } from "antd";
import React, { useEffect, useRef } from "react";
import { InfoAlert } from "../../components/alerts";
import { SPACE_MD } from "../../styles/spacing";
import { IconMail } from "../../components/icons/Icons";
import { blue6 } from "../../styles/colors";
const { Item } = Form;

/**
 * React component to render the forgot password form
 * @param {function} updateDisplayLoginPage Function to update whether to display login page
 * @returns {*}
 * @constructor
 */
export function ForgotPassword({ updateDisplayLoginPage }) {
  const [forgotPassword] = useCreatePasswordResetEmailMutation();
  const [forgotPasswordForm] = Form.useForm();
  const [messageAlert, setMessageAlert] = React.useState(false);
  const [message, setMessage] = React.useState("");
  const emailRef = useRef();

  /**
   * When the component gets added to the page,
   * focus on the usernameOrEmail input.
   */
  useEffect(() => {
    emailRef.current.focus();
    emailRef.current.select();
  }, []);

  const submitResetEmail = () => {
    forgotPassword({
      usernameOrEmail: forgotPasswordForm.getFieldValue("usernameOrEmail"),
    }).then((res) => {
      forgotPasswordForm.resetFields();
      setMessageAlert(true);
      if (res.error) {
        setMessage(res.error.data.error);
      } else {
        setMessage(res.data.message);
      }
    });
  };

  return (
    <div>
      {messageAlert && (
        <InfoAlert message={message} style={{ marginTop: SPACE_MD }} />
      )}
      <Form
        name="forgotPasswordForm"
        form={forgotPasswordForm}
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
            ref={emailRef}
            prefix={<IconMail style={{ color: blue6 }} />}
            placeholder={i18n("ForgotPassword.input.placeholder")}
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
            {i18n("ForgotPassword.button.resetPassword")}
          </Button>
        </Item>
      </Form>
      <Button
        size="large"
        type="link"
        style={{ padding: 0 }}
        onClick={() => {
          updateDisplayLoginPage(true);
          window.history.back();
        }}
      >
        {i18n("ForgotPassword.link.returnToLogin")}
      </Button>
    </div>
  );
}
