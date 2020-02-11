import React from "react";

import PropTypes from "prop-types";
import { Button } from "antd";

/**
 * This components is part of the TemplateSelect.  Displays a save button
 * if the "All Field" or no template is selected and columns modified.
 */
export class SaveTemplateButton extends React.Component {
  constructor(props) {
    super(props);
  }

  showSaveModal = e => {
    e.stopPropagation();
    this.props.showSaveModal();
  };

  render() {
    return (
      <Button
        className="t-template-save-btn"
        size="small"
        type="primary"
        ghost
        onClick={this.showSaveModal}
        disabled={typeof this.props.template === "undefined"}
      >
        {i18n("linelist.templates.saveModified")}
      </Button>
    );
  }
}

SaveTemplateButton.propTypes = {
  template: PropTypes.object,
  showSaveModal: PropTypes.func.isRequired
};
