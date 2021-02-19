import React from "react";
import { Button, Form, Input, Modal, Select, Steps } from "antd";

export function MetadataFieldCreate({ onCreate = Function.prototype }) {
  const [form] = Form.useForm();
  const [visible, setVisible] = React.useState(false);
  const [current, setCurrent] = React.useState(0);

  const steps = [
    {
      title: "Select type of field",
      content: (
        <Form.Item label={"Type"} name="type">
          <Select defaultValue="text">
            <Select.Option value="text">Text</Select.Option>
            <Select.Option value="ontology">Ontology</Select.Option>
          </Select>
        </Form.Item>
      ),
    },
    {
      title: "Field Information",
      content: (
        <Form.Item label={"Name"} name="name">
          <Input />
        </Form.Item>
      ),
    },
  ];

  return (
    <>
      <Button onClick={() => setVisible(true)}>Add New Field</Button>
      <Modal
        title={"Add Metadata Field"}
        visible={visible}
        onCancel={() => setVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Steps current={current}>
            <
          </Steps>
        </Form>
      </Modal>
    </>
  );
}
