import { useActivateAccountMutation } from "../../apis/passwordReset";
import { Button, Form, Input } from "antd";
import React, { useEffect, useRef } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import { InfoAlert } from "../../components/alerts";
import { SPACE_MD } from "../../styles/spacing";
const { Item } = Form;

export function ActivateAccount({ updateDisplayLoginPage }) {
  const [activateAccount] = useActivateAccountMutation();
  const [activateAccountForm] = Form.useForm();
  const [messageAlert, setMessageAlert] = React.useState(false);
  const [message, setMessage] = React.useState("");
  const activationIdRef = useRef();

  useEffect(() => {
    activationIdRef.current.focus();
    activationIdRef.current.select();
  }, []);

  const handleSubmit = () => {
    activateAccount({
      identifier: activateAccountForm.getFieldValue("activationId"),
    }).then((res) => {
      if (res.error) {
        setMessageAlert(true);
        setMessage(i18n("ActivateAccount.invalidActivationId"));
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
              message: i18n("ActivateAccount.activationIdRequired"),
            },
          ]}
        >
          <Input
            name="activationId"
            ref={activationIdRef}
            placeholder={i18n("ActivateAccount.input.placeholder")}
          />
        </Item>

        <Item>
          <Button
            id="t-submit-btn"
            type="primary"
            block
            onClick={() => handleSubmit()}
          >
            {i18n("ActivateAccount.button.activateAccount")}
          </Button>
        </Item>
      </Form>
      <Button
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
