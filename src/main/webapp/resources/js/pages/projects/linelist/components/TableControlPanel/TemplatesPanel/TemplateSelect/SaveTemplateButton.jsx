import React, { useState } from "react";
import { Button } from "antd";
import { SaveTemplateModal } from "../SaveTemplateModal";

/**
 * This component is part of the TemplateSelect.  Displays a save button
 * if the "All Field" or no template is selected and columns modified.
 */
export function SaveTemplateButton({ current, templates, saveTemplate }) {
  const [visible, setVisible] = useState(false);
  const template = templates[current];

  const toggleModal = () => setVisible(!visible);

  return (
    <>
      <Button
        className="t-template-save-btn"
        onClick={toggleModal}
        disabled={!template?.modified.length}
      >
        {i18n("linelist.templates.saveModified")}
      </Button>
      <SaveTemplateModal
        template={template}
        templates={templates}
        visible={visible}
        onClose={toggleModal}
        saveTemplate={saveTemplate}
      />
    </>
  );
}
