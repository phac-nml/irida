import React from "react";
import { Popover, Icon } from "antd";
import PropTypes from "prop-types";
import { TemplateSelect } from "./TemplateSelect";
import { SaveTemplateModal } from "./SaveTemplateModal";
const { i18n } = window.PAGE;

const content = (
  <div style={{ maxWidth: "250px" }}>
    <p>{i18n.linelist.templates.Popover.content}</p>
    <p>{i18n.linelist.templates.Popover.description}</p>
  </div>
);

export function Templates(props) {
  return (
    <div style={{ marginBottom: "1rem" }}>
      <TemplateSelect {...props} />
      <SaveTemplateModal
        modified={props.modified}
        validating={props.validating}
        validateTemplateName={props.validateTemplateName}
      />
      <Popover content={content} title={i18n.linelist.templates.Popover.title}>
        <Icon
          type="question-circle-o"
          style={{ color: "RGBA(46, 149, 248, 1.00)", margin: "0 .5rem" }}
        />
      </Popover>
    </div>
  );
}

Templates.propTypes = {
  modified: PropTypes.bool.isRequired,
  validateTemplateName: PropTypes.func.isRequired,
  validating: PropTypes.bool.isRequired
};
