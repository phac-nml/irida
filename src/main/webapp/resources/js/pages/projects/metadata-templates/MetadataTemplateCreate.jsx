import React from "react";
import { Button, Form, Input, Modal } from "antd";

export function MetadataTemplateCreate({ createTemplate }) {
  const [visible, setVisible] = React.useState(false);
  const [form] = Form.useForm();

  const onOk = async () => {
    const values = await form.validateFields();
    try {
      await createTemplate(values);
      form.resetFields(Object.keys(values));
      setVisible(false);
    } catch (e) {
      console.log(e);
    }
  };

  return (
    <>
      <Button onClick={() => setVisible(true)}>New Template</Button>
      <Modal
        title={"CREATE NEW METADATE TEMPLATE"}
        visible={visible}
        onCancel={() => setVisible(false)}
        okText={"CREATE TEMPLATE"}
        onOk={onOk}
      >
        <Form layout="vertical" form={form}>
          <Form.Item label={"NAME"} name="name">
            <Input />
          </Form.Item>
          <Form.Item label={"DESCRIPTION"} name="description">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
