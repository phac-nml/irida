import React from "react";
import { Select } from "antd";
const { Option } = Select;

const renderSelect = props => (
  <Select
    defaultValue={"" + props.current}
    style={{ width: 250 }}
    onSelect={id => props.fetchTemplate(id)}
  >
    <Option value="-1">_None_</Option>
    {props.templates.map(t => (
      <Option key={t.id} value={t.id}>
        {t.label}
      </Option>
    ))}
  </Select>
);

export const TemplateSelect = props => {
  if (props.templates.length) {
    return renderSelect(props);
  } else {
    return <span />;
  }
};
