import React from "react";
import { Button, Form, Input, Modal, Select, Space } from "antd";

export function MetadataFieldCreate({}) {
  const [form] = Form.useForm();

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
    <Space>
      <p>HELLO</p>
      <Form form={form} layout="vertical">
        {/*<Steps current={current}>*/}
        {/*  <*/}
        {/*</Steps>*/}
      </Form>
    </Space>
  );
}
