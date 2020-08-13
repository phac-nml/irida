import React, { useState } from "react";
import { Divider, Form, Input, Select } from "antd";

const { Option } = Select;

export function PipelineParameters({ parameters }) {
  const [selected, setSelected] = useState(0);
  return (
    <>
      <Select
        defaultValue={selected}
        onChange={setSelected}
        style={{ width: "100%" }}
      >
        {parameters.map((option) => (
          <Option key={option.id} value={option.id}>
            {option.label}
          </Option>
        ))}
      </Select>
      <Divider />
      {
        <Form layout={"vertical"}>
          {parameters[selected].parameters.map((p) => (
            <Form.Item key={p.label} label={p.label}>
              <Input value={p.value} />
            </Form.Item>
          ))}
        </Form>
      }
    </>
  );
}
