import React from "react";
import Form from "antd/es/form";
import Input from "antd/es/input";

export function SavedParameters({ sets }) {
  console.log(sets);
  return sets.map((set) => {
    return (
      <>
        <h3>{set.label}</h3>
        {set.parameters.map((parameter) => (
          <Form.Item label={parameter.label} key={parameter.name}>
            <Input />
          </Form.Item>
        ))}
      </>
    );
  });
}
