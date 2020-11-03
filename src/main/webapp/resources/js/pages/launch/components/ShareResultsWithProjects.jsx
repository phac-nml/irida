import React from "react";
import { Checkbox, Form } from "antd";

export function ShareResultsWithProjects() {
  return (
    <Form.Item name="shareResultsWithProjects" valuePropName="checked">
      <Checkbox>{i18n("ShareResultsWithProjects.label")}</Checkbox>
    </Form.Item>
  );
}
