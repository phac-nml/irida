import React from "react";
import { Checkbox, Form, Radio } from "antd";

export function ParameterWithOptions({ options }) {
  return options.map((option) => {
    console.log(option);
    if (option.type === "checkbox") {
      // Checkboxes for truthy options.
      return (
        <Form.Item key={option.name} name={option.name} valuePropName="checked">
          <Checkbox>{option.label}</Checkbox>
        </Form.Item>
      );
    } else {
      return (
        <Form.Item key={option.name} name={option.name} label={option.label}>
          <Radio.Group options={option.options} />
        </Form.Item>
      );
    }
  });
}
