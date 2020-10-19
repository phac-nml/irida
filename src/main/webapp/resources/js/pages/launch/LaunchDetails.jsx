import React from "react";
import { Form, Input } from "antd";

export function LaunchDetails() {
  return (
    <>
      <Form.Item
        label={i18n("LaunchDetails.name")}
        name="name"
        rules={[{ required: true, message: "PIPELINES REQUIRE A NAME" }]}
      >
        <Input />
      </Form.Item>
      <Form.Item label={i18n("LaunchDetails.description")} name="description">
        <Input.TextArea />
      </Form.Item>
    </>
  );
}
