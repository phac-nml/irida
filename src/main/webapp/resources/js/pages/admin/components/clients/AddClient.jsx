import React, { useEffect, useRef, useState } from "react";
import { Form, Input, Modal, Select } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { useNavigate } from "@reach/router";
import { addNewClient } from "../../../../apis/clients/clients";

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

  const AVAILABLE_TOKEN_VALIDITY = [
    // 30 minutes
    { "1800": "1800" },
    // 1 hour
    { "3600": 3600 },
    // 2 hours
    { "7200": 7200 },
    // 6 hours
    { "21600": 21600 },
    // 12 hours
    { "43200": 43200 },
    // 1 day
    { "86400": 86400 },
    // 2 days
    { "172800": 172800 },
    // 7 days
    { "604800": 604800 }];

  const given_tokenValidity = "43200";

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

  return (
    <>
      <AddNewButton
        className={"t-add-client-btn"}
        onClick={() => setVisible(true)}
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
          <Form.Item
            label={i18n("client.details.tokenValidity")}
            name="accessTokenValiditySeconds"
          >
            <Select
              name="accessTokenValiditySeconds"
              options={AVAILABLE_TOKEN_VALIDITY}
              selected={given_tokenValidity}
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}