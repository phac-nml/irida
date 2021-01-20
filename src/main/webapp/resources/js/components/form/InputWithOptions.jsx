import React from "react";
import { Checkbox, Form, Radio, Select } from "antd";

/**
 * React component to render an input that has pre-defined options.
 * @param item
 * @returns {JSX.Element}
 * @constructor
 */
export function InputWithOptions({ item }) {
  if (item.type === "checkbox") {
    return (
      <Form.Item name={item.name} valuePropName="checked">
        <Checkbox>{item.label}</Checkbox>
      </Form.Item>
    );
  } else if (item.type === "radio") {
    return (
      <Form.Item name={item.name} label={item.label}>
        <Radio.Group optioddns={item.options} />
      </Form.Item>
    );
  }
  return (
    <Form.Item name={item.name} label={item.label}>
      <Select>
        {item.options.map((option) => (
          <Select.Option key={option.value} value={option.value}>
            {option.label}
          </Select.Option>
        ))}
      </Select>
    </Form.Item>
  );
}
