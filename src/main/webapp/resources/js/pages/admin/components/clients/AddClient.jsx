import React, { useEffect, useRef, useState } from "react";
import { Form, Input, Modal, Select, Checkbox } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { useNavigate } from "@reach/router";
import { addNewClient, getAddClientPage } from "../../../../apis/clients/clients";

/**
 * Component to add a button which will open a modal to add a client.
 * @returns {*}
 * @constructor
 */
export function AddClient() {
  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const inputRef = useRef();
  const [visible, setVisible] = useState();
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const [error, setError] = useState();

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
   * Action to take when the form is submitted
   */
  const onOk = () => {
    form.validateFields().then((values) => {
      addNewClient(values)
        .then((data) => {
          form.resetFields();
          setVisible(false);
          navigate(`groups/${data.id}`, { replace: true });
        })
        .catch((error) => {
          setError(error.response.data.name);
        });
    });
  };

  /**
   * Action to take when cancelling creating a group
   */
  const onCancel = () => {
    form.resetFields();
    setError(undefined);
    setVisible(false);
  };

  const onClick = () => {
    getAddClientPage().then(() => {
      setVisible(true);
    });
  }

  return (
    <>
      <AddNewButton
        className={"t-add-client-btn"}
        onClick={onClick}
        text={i18n("AdminPanel.addClient")}
      />
      <Modal
        className="t-add-client-modal"
        title={i18n("client.create")}
        visible={visible}
        onCancel={onCancel}
        onOk={onOk}
        okButtonProps={{ className: "t-confirm-new-client" }}
        okText={i18n("client.create")}
        cancelText={i18n("AdminPanel.cancel")}
      >
        <Form layout="vertical" form={form}>
          {/*CLIENT ID*/}
          <Form.Item
            label={i18n("client.clientid")}
            name="clientId"
            validateStatus={error ? "error" : null}
            help={error}
            rules={[
              {
                required: true,
                message: i18n("CreateNewUserGroupButton.name-warning"),
              },
            ]}
          >
            <Input
              ref={inputRef}
              name="clientId"
              placeholder={i18n("client.clientid")}
              onChange={() =>
                typeof error !== "undefined" ? setError(undefined) : null
              }
            />
          </Form.Item>
          {/*TOKEN VALIDITY*/}
          <Form.Item
            label={i18n("client.details.tokenValidity")}
            name="accessTokenValiditySeconds"
          >
            <Select
              name="accessTokenValiditySeconds"
              options={window.PAGE.available_token_validity}
              selected={window.PAGE.validity}
            />
          </Form.Item>
          {/*GRANT TYPES*/}
          <Form.Item
            label={i18n("client.grant-types")}
            name="authorizedGrantTypes"
          >
            <Select
              name="authorizedGrantTypes"
              options={window.PAGE.available_token_validity}
              selected={window.PAGE.validity}
            />
          </Form.Item>
          {/*REFRESH TOKEN VALIDITY*/}

          <Form.Item
            label={i18n("client.details.refreshTokenValidity")}
            name="refreshTokenValidity"
          >
            <Checkbox
              name="refreshTokenValidity"
            />
          </Form.Item>
          {/*Redirect URL*/}
          <Form.Item
            label={i18n("client.clientid")}
            name="registeredRedirectUri"
            validateStatus={error ? "error" : null}
            help={error}
            rules={[
              {
                required: true,
                message: i18n("CreateNewUserGroupButton.name-warning"),
              },
            ]}
          >
            <Input
              ref={inputRef}
              name="registeredRedirectUri"
              placeholder={i18n("client.registeredRedirectUrl")}
              onChange={() =>
                typeof error !== "undefined" ? setError(undefined) : null
              }
            />
          </Form.Item>
          {/*SCOPES*/}
          <Form.Item
            label={i18n("client.scope.read")}
            name="scope_read"
          >
            <Checkbox
              name="scope_read"
            />
          </Form.Item>
          <Form.Item
            label={i18n("client.scope.autoApprove")}
            name="scope_auto_read"
          >
            <Checkbox
              name="scope_auto_read"
            />
          </Form.Item>
          <Form.Item
            label={i18n("client.scope.write")}
            name="scope_write"
          >
            <Checkbox
              name="scope_write"
            />
          </Form.Item>
          <Form.Item
            label={i18n("client.scope.autoApprove")}
            name="scope_auto_write"
          >
            <Checkbox
              name="scope_auto_write"
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}