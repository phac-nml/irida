import React, { useState } from "react";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { Form, Input, Modal } from "antd";

export function CreateNewUserGroupButton() {
  const [visible, setVisible] = useState();
  const [form] = Form.useForm();

  return (
    <>
      <AddNewButton
        onClick={() => setVisible(true)}
        text={i18n("UserGroupsPage.create")}
      />
      <Modal
        visible={visible}
        onCancel={() => setVisible(false)}
        onOk={() => {
          form
            .validateFields()
            .then((values) => {
              // TODO: Need to submit the form :)
              form.resetFields();
            })
            .catch((info) => {
              console.log("Validate Failed:", info);
            });
        }}
      >
        <Form layout="vertical" form={form}>
          <Form.Item
            label={"GROUP NAME"}
            name="name"
            rules={[
              {
                required: true,
                message:
                  "User groups need to have a name of at least 3 letters.",
              },
            ]}
          >
            <Input name="name" />
          </Form.Item>
          <Form.Item label={"DESCRIPTION"} htmlFor="description">
            <Input.TextArea name="description" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
