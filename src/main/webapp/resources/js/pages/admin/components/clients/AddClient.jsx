import React, { useEffect, useRef, useState } from "react";
import { Form, Input, Modal } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { useNavigate } from "@reach/router";
import { createUserGroup } from "../../../../apis/users/groups";

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
      createUserGroup(values)
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
              name="name"
              onChange={() =>
                typeof error !== "undefined" ? setError(undefined) : null
              }
            />
          </Form.Item>
          <Form.Item
            label={i18n(" CreateNewUserGroupButton.description")}
            name="description"
          >
            <Input.TextArea name="description" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}