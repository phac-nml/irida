import React from "react";

import PropTypes from "prop-types";
import { Tag } from "antd";
import { SaveTemplateButton } from "./SaveTemplateButton";

/**
 * This class represents the contents of an option in an
 * [antd Select]{@link https://ant.design/components/select/}
 * for Metadata Templates
 */
export function TemplateSelectOption(props) {
  const { template, index, current, saved } = props;
  const { name, fields, modified } = template;

  /**
   * Render an update or save button depending on whether the option is for
   * an existing template or a new one.
   */
  function renderUpdateSave() {
    return (
      <SaveTemplateButton
        template={props.template}
        showSaveModal={props.showSaveModal}
        saveTemplate={props.saveTemplate}
      />
    );
  }

  return (
    <React.Fragment>
      <div className="templates-option">
        <span
          className="template-option--name"
          style={{
            maxWidth: 190,
            overflow: "hidden",
            textOverflow: "ellipsis"
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
          {modified.length === 0 ? null : renderUpdateSave()}
          <Tag className="templates-option--field-count">
            {fields.filter(f => !f.hide).length}
          </Tag>
        </span>
      </div>
    </React.Fragment>
  );
}

TemplateSelectOption.propTypes = {
  template: PropTypes.object.isRequired,
  saved: PropTypes.bool.isRequired,
  current: PropTypes.number.isRequired,
  index: PropTypes.number.isRequired,
  saveTemplate: PropTypes.func.isRequired,
  showSaveModal: PropTypes.func.isRequired
};
