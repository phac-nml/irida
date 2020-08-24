import React, { useContext, useEffect, useRef, useState } from "react";
import { Form, Input, message, Modal, Radio } from "antd";
import { REFRESH_TOKEN_VALIDITY, TOKEN_VALIDITY } from "../constants";
import { SPACE_MD } from "../../../../../styles/spacing";
import { PagedTableContext } from "../../../../../components/ant.design/PagedTable";
import {
  createClient,
  validateClientId,
} from "../../../../../apis/clients/clients";

const { Item } = Form;

export function AddClientModal({ visible, onCancel, onComplete }) {
  const clientIdRef = useRef();
  const [grantType, setGrantType] = useState("password");

  const { updateTable } = useContext(PagedTableContext);

  /*
  Watch for changes to the forms visibility, when it becomes visible
  set keyboard focus onto the user name input.
   */
  useEffect(() => {
    if (visible) {
      setTimeout(() => clientIdRef.current.focus(), 100);
    }
  }, [visible]);

  const [form] = Form.useForm();

  const radioStyle = { display: "block", lineHeight: `35px` };

  /**
   * Action to take when the form is submitted
   */
  const onOk = async () => {
    try {
      const values = await form.validateFields();
      createClient(values).then(() => {
        // Clear the form and reset the table
        updateTable();
        form.resetFields();
        onComplete();
      });
    } catch (errors) {
      // Re-enforce the error to the user
      errors.errorFields.forEach((error) =>
        message.error(error.errors.join(", "))
      );
    }
  };

  const closeModal = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      visible={visible}
      title={i18n("AddClientModal.title")}
      onOk={onOk}
      onCancel={closeModal}
      width={800}
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          tokenValidity: 21600,
          grantType: "password",
          refreshToken: 0,
          read: "read",
          write: "no",
        }}
      >
        <Item
          label={i18n("AddClientForm.clientId")}
          name="clientId"
          rules={[
            { required: true, message: "__FOOBAR_REQUIRED__" },
            { min: 5, message: "__MINIMUM____" },
            () => ({
              validator(rule, value) {
                if (value.length > 4) {
                  return validateClientId(value)
                    .then(() => Promise.resolve())
                    .catch((error) => Promise.reject(error.response.data));
                }
                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input ref={clientIdRef} />
        </Item>
        <Item label={i18n("AddClientForm.tokenValidity")} name="tokenValidity">
          <Radio.Group>
            {TOKEN_VALIDITY.map((token) => (
              <Radio.Button key={token.value} value={token.value}>
                {token.text}
              </Radio.Button>
            ))}
          </Radio.Group>
        </Item>
        <Item label={"GRANT TYPES"} name="grantType">
          <Radio.Group onChange={(e) => setGrantType(e.target.value)}>
            <Radio style={radioStyle} value="password">
              {i18n("AddClientForm.grant.password")}
            </Radio>
            <Radio style={radioStyle} value="authorization_code">
              {i18n("AddClientForm.grant.authorizationCode")}

              {grantType === "authorization_code" ? (
                <Item
                  name="redirectURI"
                  style={{
                    display: "inline-block",
                    marginLeft: SPACE_MD,
                    marginBottom: 0,
                    width: 400,
                  }}
                  rules={[
                    {
                      required: true,
                      message: i18n(
                        "AddClientForm.grant.authorizationCode.redirect.warning"
                      ),
                    },
                  ]}
                >
                  <Input
                    placeholder={i18n(
                      "AddClientForm.grant.authorizationCode.redirect"
                    )}
                  />
                </Item>
              ) : null}
            </Radio>
          </Radio.Group>
        </Item>
        <Item label={"ALLOW REFRESH TOKENS"} name={"refreshToken"}>
          <Radio.Group>
            {REFRESH_TOKEN_VALIDITY.map((token) => (
              <Radio.Button key={token.value} value={token.value}>
                {token.text}
              </Radio.Button>
            ))}
          </Radio.Group>
        </Item>
        <Item label={"READ SCOPE"} name={"read"}>
          <Radio.Group>
            <Radio.Button value="no">Not Allowed</Radio.Button>
            <Radio.Button value="read">Allow</Radio.Button>
            <Radio.Button value="auto">Allow, and auto approve</Radio.Button>
          </Radio.Group>
        </Item>
        <Item label={"Write Scope"} name="write">
          <Radio.Group>
            <Radio.Button value="no">Not Allowed</Radio.Button>
            <Radio.Button value="write">Allow</Radio.Button>
            <Radio.Button value="auto">Allow, and auto approve</Radio.Button>
          </Radio.Group>
        </Item>
      </Form>
    </Modal>
  );
}
