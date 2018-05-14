import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTYpes from "react-immutable-proptypes";
import { TemplateSelect } from "./TemplateSelect";
import { SaveTemplateButton } from "./SaveTempalteButton";
import {
  PopoverContents,
  HelpPopover
} from "../../../../../components/popovers";
const { i18n } = window.PAGE;

const content = (
  <React.Fragment>
    <p>{i18n.linelist.templates.Popover.content}</p>
    <p>{i18n.linelist.templates.Popover.description}</p>
  </React.Fragment>
);

export function Templates(props) {
  return (
    <div style={{ marginBottom: "1rem" }}>
      <TemplateSelect {...props} />
      <SaveTemplateButton {...props} />
      <HelpPopover
        content={<PopoverContents contents={content} />}
        title={i18n.linelist.templates.Popover.title}
      />
    </div>
  );
}

Templates.propTypes = {
  saveTemplate: PropTypes.func.isRequired,
  templates: ImmutablePropTYpes.list.isRequired,
  modified: PropTypes.object
};
