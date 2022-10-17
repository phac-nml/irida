import { Form, Input, Modal } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createUserGroup } from "../../../apis/users/groups";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { useResetFormOnCloseModal } from "../../../hooks";

/**
 * React component to render a button to create a new user group
 * @param baseUrl - either /admin/groups for admin panel or /groups for main app
 * baseUrl should already be set in parent component
 * @returns {*}
 * @constructor
 */
export function CreateNewUserGroupButton({ baseUrl }) {
  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const inputRef = useRef();
  const [visible, setVisible] = useState();
  const [error, setError] = useState();

  const navigate = useNavigate();

  /*
  Ant Design form
   */
  const [form] = Form.useForm();
  useResetFormOnCloseModal({
    form,
    visible,
  });

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
          navigate(`${baseUrl}/${data.id}`, { replace: true });
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
        open={visible}
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
