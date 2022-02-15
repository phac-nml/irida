import { Form, Input, message, Modal, Radio, Space, Typography } from "antd";
import React, { useContext, useEffect, useRef, useState } from "react";
import { validateClientId } from "../../../../../apis/clients/clients";
import { PagedTableContext } from "../../../../../components/ant.design/PagedTable";
import { HelpPopover } from "../../../../../components/popovers";
import { SPACE_MD } from "../../../../../styles/spacing";
import { REFRESH_TOKEN_VALIDITY, TOKEN_VALIDITY } from "../constants";

const { Item } = Form;
const { Paragraph } = Typography;

/**
 * React component to display an Ant Design Modal containing a form to
 * create a new Irida Client
 * @param children - button to open modal
 * @param {function} onComplete - how to handle the create / update
 * @param {object} existing - use for updating an existing client
 * @returns {JSX.Element}
 * @constructor
 */
export function AddClientModal({ children, onComplete, existing = null }) {
  const { updateTable } = useContext(PagedTableContext);

  const [visible, setVisible] = React.useState(false);
  const clientIdRef = useRef();
  const [grantType, setGrantType] = useState("password");

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

  if (existing !== null) {
    let [read, write] = existing.scope;
    if (existing.autoApprovableScopes.includes("read")) {
      read = "auto";
    }
    if (existing.autoApprovableScopes.includes("write")) {
      write = "auto";
    }

    const refreshToken = existing.authorizedGrantTypes.includes("refresh_token")
      ? existing.refreshTokenValiditySeconds
      : 0;

    const grantType = existing.authorizedGrantTypes.includes("password")
      ? "password"
      : "authorization_code";

    existing = {
      clientId: existing.clientId,
      tokenValidity: existing.accessTokenValiditySeconds,
      grantType,
      redirectURI: existing.redirectUri,
      refreshToken,
      read,
      write,
    };
  }

  /**
   * Action to take when the form is submitted
   */
  const onOk = async () => {
    try {
      const values = await form.validateFields();
      await onComplete(values);
      updateTable();
      setVisible(false);
    } catch (errors) {
      // Re-enforce the error to the user
      errors.errorFields.forEach((error) =>
        message.error(error.errors.join(", "))
      );
    }
  };

  const closeModal = () => {
    form.resetFields();
    setVisible(false);
  };

  const initialValues = {
    tokenValidity: 21600,
    grantType: "password",
    refreshToken: 0,
    read: "read",
    write: "no",
    ...existing,
  };
  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Modal
        className="t-client-modal"
        visible={visible}
        title={i18n("AddClientModal.title")}
        onOk={onOk}
        onCancel={closeModal}
        width={800}
        okButtonProps={{
          className: "t-create-btn",
        }}
      >
        <Form form={form} layout="vertical" initialValues={initialValues}>
          <Item
            label={
              <>
                {i18n("AddClientForm.clientId")}
                <HelpPopover content={i18n("AddClientForm.clientId.help")} />
              </>
            }
            name="clientId"
            rules={[
              {
                required: true,
                message: i18n("AddClientForm.clientId.required"),
              },
              {
                pattern: /^\S*$/,
                message: i18n("AddClientForm.clientId.spaces"),
              },
              { min: 5, message: i18n("AddClientForm.clientId.minimum") },
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
            <Input ref={clientIdRef} disabled={existing !== null} />
          </Item>
          <Item
            label={
              <>
                {i18n("AddClientForm.tokenValidity")}
                <HelpPopover
                  content={i18n("AddClientForm.tokenValidity.help")}
                />
              </>
            }
            name="tokenValidity"
          >
            <Radio.Group>
              {TOKEN_VALIDITY.map((token) => (
                <Radio.Button key={token.value} value={token.value}>
                  {token.text}
                </Radio.Button>
              ))}
            </Radio.Group>
          </Item>
          <Item label={i18n("AddClientForm.grantTypes")} name="grantType">
            <Radio.Group onChange={(e) => setGrantType(e.target.value)}>
              <Radio style={radioStyle} value="password">
                <>
                  {i18n("AddClientForm.grant.password")}
                  <HelpPopover
                    content={i18n("AddClientForm.grant.password.help")}
                  />
                </>
              </Radio>
              <Radio style={radioStyle} value="authorization_code">
                <Space>
                  {i18n("AddClientForm.grant.authorizationCode")}
                  <HelpPopover
                    width={400}
                    content={
                      <section>
                        <Paragraph>
                          {i18n("AddClientForm.grant.authorizationCode.help")}
                        </Paragraph>
                        <Paragraph>
                          {i18n("AddClientForm.grant.authorizationCode.help2")}
                        </Paragraph>
                      </section>
                    }
                  />

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
                </Space>
              </Radio>
            </Radio.Group>
          </Item>
          <Item
            label={
              <>
                {i18n("AddClientForm.refreshToken")}
                <HelpPopover
                  width={400}
                  content={
                    <section>
                      <Paragraph>
                        {i18n("AddClientForm.refreshToken.help")}
                      </Paragraph>
                      <Paragraph>
                        {i18n("AddClientForm.refreshToken.help2")}
                      </Paragraph>
                    </section>
                  }
                />
              </>
            }
            name="refreshToken"
          >
            <Radio.Group>
              {REFRESH_TOKEN_VALIDITY.map((token) => (
                <Radio.Button key={token.value} value={token.value}>
                  {token.text}
                </Radio.Button>
              ))}
            </Radio.Group>
          </Item>
          <Item label={i18n("AddClientForm.readScope")} name="read">
            <Radio.Group>
              <Radio.Button value="no">
                {i18n("AddClientForm.scopeNotAllowed")}
              </Radio.Button>
              <Radio.Button value="read">
                {i18n("AddClientForm.scopeAllowed")}
              </Radio.Button>
              <Radio.Button value="auto">
                {i18n("AddClientForm.scopeAllowedAutoApprove")}
              </Radio.Button>
            </Radio.Group>
          </Item>
          <Item label={i18n("AddClientForm.writeScope")} name="write">
            <Radio.Group>
              <Radio.Button value="no">
                {i18n("AddClientForm.scopeNotAllowed")}
              </Radio.Button>
              <Radio.Button value="write">
                {i18n("AddClientForm.scopeAllowed")}
              </Radio.Button>
              <Radio.Button value="auto">
                {i18n("AddClientForm.scopeAllowedAutoApprove")}
              </Radio.Button>
            </Radio.Group>
          </Item>
        </Form>
      </Modal>
    </>
  );
}
