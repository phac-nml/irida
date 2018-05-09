import React from "react";
import { Popover, Tag, Icon, Select } from "antd";
const { Option } = Select;

const { i18n } = window.PAGE;

const content = (
  <div style={{ maxWidth: "250px" }}>
    <p>{i18n.linelist.Select.Popover.content}</p>
    <p>{i18n.linelist.Select.Popover.description}</p>
  </div>
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
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span
                style={{
                  maxWidth: 170,
                  overflow: "hidden",
                  textOverflow: "ellipsis"
                }}
              >
                {t.name}
              </span>{" "}
              <Tag>{t.fields.length}</Tag>
            </div>
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
