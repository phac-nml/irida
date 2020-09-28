import React from "react";
import { Checkbox, Form, Radio, Select } from "antd";
import { useLaunchState } from "./launch-context";

function BooleanParameter({ parameter, onChange }) {
  return (
    <Checkbox
      checked={parameter.value || parameter.value === "true"}
      onChange={(e) => onChange({ parameter, value: e.target.checked })}
    >
      {parameter.label}
    </Checkbox>
  );
}

function ShortList({ parameter, onChange }) {
  return (
    <Form.Item label={parameter.label}>
      <Radio.Group
        defaultValue={parameter.value}
        onChange={(e) => onChange({ parameter, value: e.target.value })}
      >
        {parameter.options.map((option) => (
          <Radio key={option.value} value={option.value}>
            {option.label}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
}

function LongList({ parameter, onChange }) {
  return (
    <Form.Item label={parameter.label}>
      <Select
        onChange={(value) => onChange({ parameter, value })}
        value={parameter.value}
      >
        {parameter.options.map((option) => (
          <Select.Option key={option.value} value={option.value}>
            {option.text}
          </Select.Option>
        ))}
      </Select>
    </Form.Item>
  );
}

export function PipelineParametersWithOptions() {
  const { api, parametersWithOptions } = useLaunchState();

  const checkTrueFalse = (value) =>
    typeof value === "boolean" || value === "true" || value === "false";

  const isBoolean = (options) =>
    options.length === 2 &&
    checkTrueFalse(options[0].value) &&
    checkTrueFalse(options[1].value);

  return parametersWithOptions ? (
    <section>
      {parametersWithOptions.map((set) => {
        if (isBoolean(set.options)) {
          return (
            <BooleanParameter
              onChange={api.setParameterWithOption}
              key={set.name}
              parameter={set}
            />
          );
        } else if (set.options.length < 5) {
          return (
            <ShortList
              onChange={api.setParameterWithOption}
              key={set.name}
              parameter={set}
            />
          );
        }
        return (
          <LongList
            key={set.name}
            onChange={api.setParameterWithOption}
            parameter={set}
          />
        );
      })}
    </section>
  ) : null;
}
