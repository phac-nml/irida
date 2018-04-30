import React from "react";
import PropTypes from "prop-types";
import { Select } from "antd";
const { Option } = Select;

export const TemplateSelect = props => {
  return props.templates.length > 0 ? (
    <Select
      value={props.current}
      style={{ width: 250 }}
      onSelect={id => props.fetchTemplate(id)}
    >
      {props.modified ? <Option value={-2}>_Modified_</Option> : null}
      <Option value={0} title="_NONE_">
        _None_
      </Option>
      {props.templates.map(t => (
        <Option key={t.id} value={t.id} title={t.label}>
          {t.label}
        </Option>
      ))}
    </Select>
  ) : null;
};

TemplateSelect.propTypes = {
  modified: PropTypes.bool.isRequired,
  templates: PropTypes.array.isRequired,
  fetchTemplate: PropTypes.func.isRequired
};
