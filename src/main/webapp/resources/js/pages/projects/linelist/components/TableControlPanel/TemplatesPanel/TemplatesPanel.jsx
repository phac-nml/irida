import React from "react";
import PropTypes from "prop-types";
import { SaveTemplateModal } from "./SaveTemplateModal";
import { TemplateSelect } from "./TemplateSelect/TemplateSelect";

/**
 * This component is responsible for rendering all components that handle
 * user interaction with selecting and saving templates.
 */
export class TemplatesPanel extends React.Component {
  state = {
    visible: false // If the save template modal is visible
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
    const { templates, current } = this.props;
    const template = templates[current];

    return (
      <div
        style={{
          height: 75,
          borderBottom: "1px solid rgba(189, 195, 199, 1.00)",
          padding: "1rem"
        }}
      >
        <TemplateSelect {...this.props} showSaveModal={this.showSaveModal} />
        <SaveTemplateModal
          template={template}
          visible={this.state.visible}
          onClose={this.closeModal}
          {...this.props}
        />
      </div>
    );
  }
}

TemplatesPanel.propTypes = {
  current: PropTypes.number.isRequired,
  saveTemplate: PropTypes.func.isRequired,
  templates: PropTypes.array.isRequired,
  modified: PropTypes.object
};
