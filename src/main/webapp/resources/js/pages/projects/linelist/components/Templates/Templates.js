import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTYpes from "react-immutable-proptypes";
import { TemplateSelect } from "./TemplateSelect";
import { SaveTemplateModal } from "./SaveTemplateModal";
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
      <SaveTemplateModal
        modified={props.modified}
        templates={props.templates}
      />
      <HelpPopover
        content={<PopoverContents contents={content} />}
        title={i18n.linelist.templates.Popover.title}
      />
    </div>
  );
}

Templates.propTypes = {
  templates: ImmutablePropTYpes.list.isRequired,
  modified: PropTypes.object
};
