import React from "react";
import { Select } from "antd";
const { Option } = Select;

export function TemplateSelect(props) {
  const templates = props.templates.toJS();
  if (templates.length) {
    return (
      <Select
        defaultValue={"" + props.current}
        style={{ width: 250 }}
        onSelect={id => props.fetchTemplate(id)}
      >
        <Option value="-1">_None_</Option>
        {templates.map(t => (
          <Option key={t.id} value={t.id}>
            {t.label}
          </Option>
        ))}
      </Select>
    );
  } else {
    return <span />;
  }
}
