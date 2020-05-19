import React, { useContext, useEffect, useRef, useState } from "react";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { Form, Input, Modal } from "antd";
import { createUserGroup } from "../../../apis/users/groups";
import { useNavigate } from "@reach/router";

/**
 * React component to render a button to create a new user group
 * @returns {*}
 * @constructor
 */
export function CreateNewUserGroupButton() {
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
        className={"t-create-group-btn"}
        onClick={() => setVisible(true)}
        text={i18n("UserGroupsPage.create")}
      />
      <Modal
        className="t-new-group-modal"
        title={i18n("CreateNewUserGroupButton.title")}
        visible={visible}
        onCancel={onCancel}
        onOk={onOk}
        okButtonProps={{ className: "t-confirm-new-group" }}
        okText={i18n("CreateNewUserGroupButton.btn.ok")}
        cancelText={i18n("CreateNewUserGroupButton.btn.cancel")}
      >
        <Form layout="vertical" form={form}>
          <Form.Item
            label={i18n("CreateNewUserGroupButton.name")}
            name="name"
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
