import React from "react";
import { Popover, Icon, Select } from "antd";
const { Option } = Select;

const { i18n } = window.PAGE;

const content = (
  <p style={{ maxWidth: "250px" }}>{i18n.linelist.Select.Popover.content}</p>
);

export function TemplateSelect(props) {
  const templates = props.templates.toJS();
  return (
    <React.Fragment>
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
      <Popover content={content} title={i18n.linelist.Select.Popover.title}>
        <Icon
          type="question-circle-o"
          style={{ color: "RGBA(46, 149, 248, 1.00)", margin: "0 .5rem" }}
        />
      </Popover>
    </React.Fragment>
  );
}
