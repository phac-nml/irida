import React, { useEffect, useRef, useState } from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Divider, Form, Input, Modal, Radio, Select } from "antd";
import { addNewClient } from "../../../../apis/clients/clients";
import { SPACE_MD } from "../../../../styles/spacing";

export default function AddClientForm({ visible }) {
  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const inputRef = useRef();

  const [form] = Form.useForm();

  const [tokenValidities, setTokenValidities] = useState([]);
  const [tokenValidity, setTokenValidity] = useState(43200);
  const [grants, setGrants] = useState([]);
  const [refreshTokenValidities, setRefreshTokenValidities] = useState([]);
  const [refreshTokenValidity, setRefreshTokenValidity] = useState(0);
  const [scopeWrite, setScopeWrite] = useState("no");
  const [scopeRead, setScopeRead] = useState("yes");
  const [authorizedGrantTypes, setAuthorizedGrantTypes] = useState("password");

  const [error, setError] = useState();
  useEffect(() => {
    fetch(setBaseUrl(`/ajax/clients/create`))
      .then((response) => response.json())
      .then((data) => {
        console.log(data);
        setTokenValidities(data.tokenValidity);
        setRefreshTokenValidities(data.refreshTokenValidity);
        setGrants(data.grants);
      });
  }, []);

  /*
  Watch for changes to the forms visibility, when it becomes visible
  set keyboard focus onto the user name input.
   */
  useEffect(() => {
    if (visible) {
      setTimeout(() => inputRef.current.focus(), 100);
    }
  }, [visible]);

  /**
   * Action to take when cancelling creating a client
   */
  const onCancel = () => {
    form.resetFields();
    setError(undefined);
    // setVisible(false);
  };

  /**
   * Action to take when the form is submitted
   */
  const onOk = () => {
    form
      .validateFields()
      .then((values) => {
        addNewClient(values)
          .then((data) => {
            form.resetFields();
            setVisible(false);
            navigate(setBaseUrl(`admin/groups/${data.id}`), { replace: true });
          })
          .catch((error) => {
            setError(error.response.data.name);
          });
      })
      .catch((errors) => setError(true));
  };

  const radioStyle = {
    display: "block",
    height: "30px",
    lineHeight: "30px",
  };

  return (
    <Modal
      width={700}
      className="t-add-client-modal"
      title={i18n("client.create")}
      visible={visible}
      onCancel={onCancel}
      // onOk={onOk}
      okButtonProps={{ className: "t-confirm-new-client" }}
      okText={i18n("client.create")}
      cancelText={i18n("AdminPanel.cancel")}
    >
      <Form
        layout="vertical"
        form={form}
        initialValues={{
          scope_write: scopeWrite,
          scope_read: scopeRead,
          accessTokenValiditySeconds: tokenValidity,
          authorizedGrantTypes,
          refreshTokenValidity: refreshTokenValidity,
        }}
      >
        {/*CLIENT ID*/}
        <Form.Item
          label={i18n("AddClientForm.id")}
          name="clientId"
          validateStatus={error ? "error" : null}
          help={error}
          rules={[
            {
              required: true,
              message: i18n("clients.add.warning"),
            },
          ]}
        >
          <Input
            ref={inputRef}
            onChange={() =>
              typeof error !== "undefined" ? setError(undefined) : null
            }
          />
        </Form.Item>
        {/*TOKEN VALIDITY*/}
        <Form.Item
          label={i18n("AddClientForm.tokenValidity")}
          name="accessTokenValiditySeconds"
        >
          <Select value={tokenValidity} onChange={setTokenValidity}>
            {tokenValidities.map((token) => (
              <Select.Option key={token.value} value={Number(token.value)}>
                {token.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        {/*GRANT TYPES*/}
        <Form.Item
          label={i18n("AddClientForm.grant.types")}
          name="authorizedGrantTypes"
        >
          <Radio.Group
            value={authorizedGrantTypes}
            onChange={(e) => setAuthorizedGrantTypes(e.target.value)}
          >
            <Radio style={radioStyle} value="password">
              {i18n("AddClientForm.grant.password")}
            </Radio>
            <Radio style={radioStyle} value="authorization_code">
              {i18n("AddClientForm.grant.authorization_code")}

              {authorizedGrantTypes === "authorization_code" ? (
                <Input
                  style={{ marginLeft: SPACE_MD }}
                  placeholder={i18n("client.registeredRedirectUri")}
                />
              ) : null}
            </Radio>
          </Radio.Group>
        </Form.Item>
        {/*Redirect URL*/}

        {/*REFRESH TOKEN VALIDITY*/}
        <Form.Item
          label={i18n("AddClientForm.refreshTokenValidity")}
          name="refreshTokenValidity"
        >
          <Radio.Group
            value={refreshTokenValidity}
            onChange={(e) => setRefreshTokenValidity(e.target.value)}
          >
            {refreshTokenValidities.map((token) => (
              <Radio.Button key={token.value} value={Number(token.value)}>
                {token.label}
              </Radio.Button>
            ))}
          </Radio.Group>
        </Form.Item>
        {/*SCOPES*/}
        <Divider orientation="left" plain>
          SCOPES
        </Divider>
        <Form.Item name="scope_read" label={i18n("client.scope.read")}>
          <Radio.Group
            value={scopeRead}
            onChange={(e) => setScopeRead(e.target.value)}
          >
            <Radio value={"no"}>No</Radio>
            <Radio value={"yes"}>Yes</Radio>
            <Radio value={"auto"}>Yes and Auto Approve</Radio>
          </Radio.Group>
        </Form.Item>
        <Form.Item name="scope_write" label={i18n("client.scope.write")}>
          <Radio.Group
            value={scopeWrite}
            onChange={(e) => setScopeWrite(e.target.value)}
          >
            <Radio value={"no"}>No</Radio>
            <Radio value={"yes"}>Yes</Radio>
            <Radio value={"auto"}>Yes and Auto Approve</Radio>
          </Radio.Group>
        </Form.Item>
      </Form>
    </Modal>
  );
}
