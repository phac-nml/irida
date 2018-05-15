import React from "react";
import { Button } from "antd";

const { i18n } = window.PAGE;

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
      <React.Fragment>
        <Button size="small" type="dashed" onClick={this.showSaveModal}>
          {this.props.saving
            ? "SAVING MOTHERFUCKER!"
            : i18n.linelist.templates.saveModified}
        </Button>
      </React.Fragment>
    ) : null;
  }
}
