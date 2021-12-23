import { Form, Input, Modal } from "antd";
import React from "react";

export function CreateRemoteApiModal({ children }) {
  const [visible, setVisible] = React.useState(false);
  const [form] = Form.useForm();

  const submitForm = async () => {
    const values = await form.validateFields();
    form.resetFields();
    console.log(values);
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Modal
        className="t-create-api"
        title={`Add Remote Connection`}
        visible={visible}
        onCancel={() => setVisible(false)}
        onOk={submitForm}
      >
        <Form
          form={form}
          layout="vertical"
          name="remote_api"
          initialValues={{
            name: "",
            clientId: "",
            clientSecret: "",
            serviceURI: "",
          }}
        >
          <Form.Item
            name="name"
            label={i18n("remoteapi.name")}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="clientId"
            label={i18n("remoteapi.clientid")}
            rules={[{ required: true }, { pattern: /^\S+$/g }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="clientSecret"
            label={i18n("remoteapi.details.secret")}
            rules={[{ required: true }, { pattern: /^\S+$/g }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="serviceURI"
            label={i18n("remoteapi.serviceurl")}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
