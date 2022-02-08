import { Checkbox, Form, Radio, Select } from "antd";
import React from "react";
import { isTruthy } from "../../utilities/form-utilities";

/**
 * React component to render an input that has pre-defined options.
 * @param item
 * @returns {JSX.Element}
 * @constructor
 */
export function InputWithOptions({ item }) {
  if (isTruthy(item.options)) {
    return (
      <Form.Item name={item.name} valuePropName="checked">
        <Checkbox>{item.label}</Checkbox>
      </Form.Item>
    );
  } else if (item.options.length < 6) {
    return (
      <Form.Item name={item.name} label={item.label}>
        <Radio.Group options={item.options} />
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
