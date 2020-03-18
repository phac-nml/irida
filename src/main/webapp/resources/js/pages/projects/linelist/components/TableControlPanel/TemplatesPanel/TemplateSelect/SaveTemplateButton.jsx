import React from "react";

import PropTypes from "prop-types";
import { Button } from "antd";

/**
 * This components is part of the TemplateSelect.  Displays a save button
 * if the "All Field" or no template is selected and columns modified.
 */
export function SaveTemplateButton({
  showTemplateModal,
  template,
  saveTemplate
}) {
  const showSaveModal = e => {
    e.stopPropagation();
    showTemplateModal();
  };

  return (
    <Button
      className="t-template-save-btn"
      onClick={showSaveModal}
      disabled={!template.modified.length}
    >
      {i18n("linelist.templates.saveModified")}
    </Button>
  );
}

SaveTemplateButton.propTypes = {
  template: PropTypes.object,
  showSaveModal: PropTypes.func.isRequired
};
