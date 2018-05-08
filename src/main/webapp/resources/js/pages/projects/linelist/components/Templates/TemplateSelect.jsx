import React from "react";
import { Select } from "antd";
const { Option } = Select;

const { i18n } = window.PAGE;
export function TemplateSelect(props) {
  const templates = props.templates.toJS();
  return (
    <Select
      disabled={templates.length === 0}
      defaultValue={"" + props.current}
      style={{ width: 250 }}
      onSelect={id => props.fetchTemplate(id)}
    >
      <Option value="-1">{i18n.linelist.Select.none}</Option>
      {templates.map(t => (
        <Option key={t.id} value={t.id}>
          {t.label}
        </Option>
      ))}
    </Select>
  );
}
