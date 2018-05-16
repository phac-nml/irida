import React from "react";
import { Button } from "antd";

const { i18n } = window.PAGE;

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
    return this.props.modified !== null ? (
      <Button size="small" type="dashed" onClick={this.showSaveModal}>
        {i18n.linelist.templates.saveModified}
      </Button>
    ) : null;
  }
}
