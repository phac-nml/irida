import React, { useContext, useState } from "react";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { Form, Input, Modal } from "antd";
import { createUserGroup } from "../../../apis/users/groups";
import { PagedTableContext } from "../../../components/ant.design/PagedTable";

export function CreateNewUserGroupButton() {
  const [visible, setVisible] = useState();
  const [form] = Form.useForm();
  const { updateTable } = useContext(PagedTableContext);

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
              createUserGroup(values).then(() => {
                updateTable();
                form.resetFields();
                setVisible(false);
              });
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
          <Form.Item label={"DESCRIPTION"} name="description">
            <Input.TextArea name="description" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
