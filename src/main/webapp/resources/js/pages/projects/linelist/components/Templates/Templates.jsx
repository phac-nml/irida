import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTYpes from "react-immutable-proptypes";
import { SaveTemplateModal } from "./SaveTemplateModal";
import { TemplateSelect } from "./TemplateSelect";
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

export class Templates extends React.Component {
  state = {
    visible: false
  };

  closeModal = () => {
    this.setState({ visible: false });
  };

  showSaveModal = () => {
    this.setState({ visible: true });
  };

  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div style={{ marginBottom: "1rem" }}>
        <TemplateSelect
          {...this.props}
          showSaveModal={this.showSaveModal}
        />
        <SaveTemplateModal
          visible={this.state.visible}
          onClose={this.closeModal}
          {...this.props}
        />
        <HelpPopover
          content={<PopoverContents contents={content} />}
          title={i18n.linelist.templates.Popover.title}
        />
      </div>
    );
  }
}

Templates.propTypes = {
  saveTemplate: PropTypes.func.isRequired,
  templates: ImmutablePropTYpes.list.isRequired,
  modified: PropTypes.object
};
