import React from "react";
import { useLaunchState } from "./launch-context";
import { Form, Select } from "antd";

export function PipelineDynamicParameters() {
  const { dynamicSourceTools } = useLaunchState();

  return dynamicSourceTools && dynamicSourceTools.length ? (
    <section>
      {dynamicSourceTools.map((tool) => (
        <Form.Item key={`dynamic-${tool.id}`} label={tool.label}>
          <Select>
            {tool.options.map((option) => (
              <Select.Option
                key={`option-${tool.id}-${option.value}`}
                value={option.value}
              >
                {option.label}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      ))}
    </section>
  ) : null;
}
