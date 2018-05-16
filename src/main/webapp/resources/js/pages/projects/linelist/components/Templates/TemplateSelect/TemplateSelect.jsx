import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Select } from "antd";
import { TemplateSelectOption } from "./TemplateSelectOption";

const { Option } = Select;

/**
 * Component to render an [antd Select]{@link https://ant.design/components/select/}
 * component to select a specific Metadata template.
 */
export function TemplateSelect(props) {
  const { current, useTemplate } = props;
  const templates = props.templates.toJS();

  return (
    <Select
      disabled={templates.length === 1}
      value={current}
      style={{ width: 250 }}
      onSelect={useTemplate}
    >
      {templates.map((template, index) => (
        <Option key={template.id} value={index} title={template.name}>
          <TemplateSelectOption {...props} index={index} template={template} />
        </Option>
      ))}
    </Select>
  );
}

TemplateSelect.propTypes = {
  current: PropTypes.number.isRequired,
  templates: ImmutablePropTypes.list.isRequired,
  useTemplate: PropTypes.func.isRequired
};
