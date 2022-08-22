import { Checkbox, Form, Radio, Select } from "antd";
import React from "react";
import { isTruthy } from "../../utilities/form-utilities";

/**
 * React component to render an input that has pre-defined options.
 * @param item
 * @param initialValue
 * @returns {JSX.Element}
 * @constructor
 */
export function InputWithOptions({ item }) {
  if (isTruthy(item.options)) {
    return (
      <Form.Item
        name={item.name}
        valuePropName="checked"
        rules={[
          {
            required: item.required,
            message: i18n("InputWithOptions.required", item.label),
          },
        ]}
        initialValue={item.value}
      >
        <Checkbox>{item.label}</Checkbox>
      </Form.Item>
    );
  } else if (item.options.length < 6) {
    return (
      <Form.Item
        name={item.name}
        label={item.label}
        rules={[
          {
            required: item.required,
            message: i18n("InputWithOptions.required", item.label),
          },
        ]}
        initialValue={item.value}
      >
        <Radio.Group options={item.options} />
      </Form.Item>
    );
  }
  return (
    <Form.Item
      name={item.name}
      label={item.label}
      rules={[
        {
          required: item.required,
          message: i18n("InputWithOptions.required", item.label),
        },
      ]}
      initialValue={item.value}
    >
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
