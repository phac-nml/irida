import React from "react";
import PropTypes from "prop-types";
import { Tag } from "antd";
import { SaveTemplateButton } from "./SaveTemplateButton";
import { UpdateTemplateButton } from "./UpdateTemplateButton";
import { NO_TEMPLATE_ID } from "../../../reducers/templates";

const { i18n } = window.PAGE;

/**
 * This class represents the contents of an option in an
 * [antd Select]{@link https://ant.design/components/select/}
 * for Metadata Templates
 */
export function TemplateSelectOption(props) {
  const { template, modified, index, current, saved } = props;
  const { name, fields, id } = template;

  /**
   * Render an update or save button depending on whether the option is for
   * an existing template or a new one.
   */
  function renderUpdateSave() {
    if (id === NO_TEMPLATE_ID) {
      return (
        <SaveTemplateButton
          showSaveModal={props.showSaveModal}
          saveTemplate={props.saveTemplate}
        />
      );
    }
    return <UpdateTemplateButton {...props} />;
  }

  return (
    <React.Fragment>
      <div className="templates-option">
        <span className="template-option--name">{name}</span>
        <span>
          {saved && index === current ? (
            <Tag color="green">
              {i18n.linelist.templates.saved.toUpperCase()}
            </Tag>
          ) : null}
          {modified !== null && modified.name === name
            ? renderUpdateSave()
            : null}
          {index > 0 ? (
            <Tag className="field-count">
              {/* - 1 Because the fields include the sample name itself. */}
              {fields.length - 1}
            </Tag>
          ) : null}
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
  modified: PropTypes.object,
  saveTemplate: PropTypes.func.isRequired,
  showSaveModal: PropTypes.func.isRequired
};
