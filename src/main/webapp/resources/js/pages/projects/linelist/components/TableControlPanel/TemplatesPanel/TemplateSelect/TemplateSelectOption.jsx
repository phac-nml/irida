import React from "react";

import PropTypes from "prop-types";
import { Tag } from "antd";
import styled from "styled-components";

const TemplateOption = styled.div`
  display: flex;
  justify-content: space-between;

  .ant-select-selection-item & {
    .templates-option--field-count {
      display: none;
    }
  }
`;

const TemplateOptionField = styled.div`
  width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
`;

/**
 * This class represents the contents of an option in an
 * [antd Select]{@link https://ant.design/components/select/}
 * for Metadata Templates
 */
export function TemplateSelectOption(props) {
  const { template, index, current, saved } = props;
  const { name, fields } = template;

  return (
    <>
      <TemplateOption>
        <TemplateOptionField className="t-template-name">{name}</TemplateOptionField>
        <span>
          {saved && index === current ? (
            <Tag color="green">
              {i18n("linelist.templates.saved").toUpperCase()}
            </Tag>
          ) : null}
          <Tag className="templates-option--field-count">
            {fields.filter(f => !f.hide).length}
          </Tag>
        </span>
      </TemplateOption>
    </>
  );
}

TemplateSelectOption.propTypes = {
  template: PropTypes.object.isRequired,
  saved: PropTypes.bool.isRequired,
  current: PropTypes.number.isRequired,
  index: PropTypes.number.isRequired
};
