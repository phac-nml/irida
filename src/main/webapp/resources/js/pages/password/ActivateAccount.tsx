import React from "react";
import { Alert, Button, Form, Input, InputRef } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_MD } from "../../styles/spacing";
import { useActivateAccountMutation } from "../../apis/password-reset";

const { Item } = Form;

export interface ActivateAccountProps {
  updateDisplayLoginPage: (value: boolean) => void;
}

/**
 * React component to render the forgot password form
 * @param updateDisplayLoginPage Function to update whether to display login page
 * @constructor
 */
export function ActivateAccount({
  updateDisplayLoginPage,
}: ActivateAccountProps): JSX.Element {
  const [loading, setLoading] = React.useState(false);
  const [activateAccount] = useActivateAccountMutation();
  const [activateAccountForm] = Form.useForm();
  const [message, setMessage] = React.useState(null);
  const activationIdRef = React.useRef<InputRef>(null);

  /**
   * When the component gets added to the page,
   * focus on the activationId input.
   */
  React.useEffect(() => {
    if (activationIdRef.current !== null) {
      activationIdRef.current.focus();
      activationIdRef.current.select();
    }
  }, [activationIdRef]);

  const submitActivateAccountForm = () => {
    setLoading(true);
    activateAccount({
      identifier: activateAccountForm.getFieldValue("activationId"),
    })
      .unwrap()
      .then((response) => {
        // response.data.message has the identifier
        window.location.replace(
          setBaseUrl(`/password_reset/${response.message}`)
        );
        activateAccountForm.resetFields();
      })
      .catch((error) => {
        setLoading(false);
        setMessage(error.data.error);
      });
  };

  return (
    <>
      <Alert
        message={i18n("ActivateAccount.alert.description")}
        className="t-activation-id-description"
        style={{ marginTop: SPACE_MD }}
        type="info"
        showIcon
      />
      {message !== null && (
        <Alert
          message={message}
          className="t-activation-id-error-alert"
          style={{ marginTop: SPACE_MD }}
          type="error"
          showIcon
        />
      )}

      <Form
        name="accountActivationForm"
        form={activateAccountForm}
        onFinish={submitActivateAccountForm}
        size="large"
        style={{ marginTop: SPACE_MD }}
      >
        <Item
          name="activationId"
          rules={[
            {
              required: true,
              message: i18n("ActivateAccount.activationIdRequired"),
            },
          ]}
        >
          <Input
            name="activationId"
            id="activationId"
            ref={activationIdRef}
            placeholder={i18n("ActivateAccount.input.placeholder")}
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
            {i18n("ActivateAccount.button.activateAccount")}
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
        {i18n("ActivateAccount.link.returnToLogin")}
      </Button>
    </>
  );
}
