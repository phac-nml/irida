import React from "react";
import { Checkbox, Form, Radio, Select } from "antd";
import { useLaunch } from "./launch-context";

/**
 * React component to render all Dynamic Sources - a set of select inputs.
 * These will get returned with the regular parameters to start the pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function DynamicSources() {
  const [{ dynamicSources: sources }] = useLaunch();
  console.log(sources);

  const content = sources.map((parameter) => {
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

  return <section>{content}</section>;
}
