import React from "react";
import { Alert, Button, Form, Input } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_MD } from "../../styles/spacing";
import { useActivateAccountMutation } from "../../apis/password-reset";

const { Item } = Form;

/**
 * React component to render the forgot password form
 * @param {function} updateDisplayLoginPage Function to update whether to display login page
 * @returns {*}
 * @constructor
 */
export function ActivateAccount({ updateDisplayLoginPage }) {
  const [loading, setLoading] = React.useState(false);
  const [activateAccount] = useActivateAccountMutation();
  const [activateAccountForm] = Form.useForm();
  const [message, setMessage] = React.useState(null);
  const activationIdRef = React.useRef();

  /**
   * When the component gets added to the page,
   * focus on the activationId input.
   */
  React.useEffect(() => {
    activationIdRef.current.focus();
    activationIdRef.current.select();
  }, []);

  const submitActivateAccountForm = () => {
    setLoading(true);
    activateAccount({
      identifier: activateAccountForm.getFieldValue("activationId"),
    }).then((response) => {
      if (response.error) {
        setLoading(false);
        setMessage(response.error.data.error);
      } else {
        // response.data.message has the identifier
        window.location.replace(
          setBaseUrl(`/password_reset/${response.data.message}`)
        );
      }
      activateAccountForm.resetFields();
    });
  };

  return (
    <div>
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
            icon={loading && <LoadingOutlined />}
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
        size="large"
        style={{ padding: 0 }}
        onClick={() => {
          updateDisplayLoginPage(true);
          window.history.back();
        }}
      >
        {i18n("ActivateAccount.link.returnToLogin")}
      </Button>
    </div>
  );
}
