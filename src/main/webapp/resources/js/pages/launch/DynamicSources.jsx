import React from "react";
import { Form, Select } from "antd";
import { useLaunch } from "./launch-context";

/**
 * React component to render all Dynamic Sources - a set of select inputs.
 * These will get returned with the regular parameters to start the pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function DynamicSources() {
  const [{ dynamicSources: sources }] = useLaunch();
  return (
    sources && (
      <section>
        {sources.map((source) => (
          <Form.Item key={source.label} label={source.label} name={source.id}>
            <Select>
              {source.options.map((option) => (
                <Select.Option key={option.value} value={option.value}>
                  {option.label}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        ))}
      </section>
    )
  );
}
