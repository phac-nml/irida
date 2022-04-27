import React from "react";

import { Tag } from "antd";

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
      <div className="templates-option">
        <span
          className="template-option--name"
          style={{
            maxWidth: 170,
            overflow: "hidden",
            textOverflow: "ellipsis",
          }}
        >
          {name}
        </span>
        <span>
          {saved && index === current ? (
            <Tag color="green">
              {i18n("linelist.templates.saved").toUpperCase()}
            </Tag>
          ) : null}
          <Tag className="templates-option--field-count">
            {/* We subtract 2 from the fields' length as the Select box and sample name should not be included in this count */}
            {fields.filter((f) => !f.hide).length - 2}
          </Tag>
        </span>
      </div>
    </>
  );
}