import React from "react";
import { Checkbox, Divider, Form, Radio, Select } from "antd";

/**
 * React Component to display workflow "parameters with options", this would include
 * any pipeline specific parameters that have multiple options, not just fill in the
 * blank.
 * <ul>
 *   <li>If the context has assigned the parameter as "truthy" then it is render as a checkbox</li>
 *   <li>Else if it has less then 7 options, it is rendered as regular radio group</li>
 *   <li>Else it will be rendered as a select dropdown</li>
 * </ul>
 *
 * @param {array} parameters - list of all parameters that have options.
 * @returns {*}
 * @constructor
 */
export function ParameterWithOptions({ parameters }) {
  const content = parameters.map((parameter) => {
    switch (parameter.type) {
      case "checkbox":
        return (
          <Form.Item
            key={parameter.name}
            name={parameter.name}
            valuePropName="checked"
          >
            <Checkbox>{parameter.label}</Checkbox>
          </Form.Item>
        );
      case "radio":
        return (
          <Form.Item
            key={parameter.name}
            name={parameter.name}
            label={parameter.label}
          >
            <Radio.Group options={parameter.options} />
          </Form.Item>
        );
      default:
        return (
          <Form.Item
            key={parameter.name}
            name={parameter.name}
            label={parameter.label}
          >
            <Select>
              {parameter.options.map((option) => (
                <Select.Option key={option.value} value={option.value}>
                  {option.label}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        );
    }
  });

  return (
    <section>
      {content}
      <Divider />
    </section>
  );
}
