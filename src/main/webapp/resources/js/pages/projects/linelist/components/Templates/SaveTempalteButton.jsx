import React from "react";
import { Button } from "antd";
import { SaveTemplateModal } from "./SaveTemplateModal";

const { i18n } = window.PAGE;

export class SaveTemplateButton extends React.Component {
  state = { visible: false };

  openModal = () => {
    this.setState({ visible: true });
  };

  closeModal = () => {
    this.setState({ visible: false });
  };

  constructor(props) {
    super(props);
  }

  render() {
    return this.props.modified !== null ? (
      <React.Fragment>
        <Button
          className="primary"
          icon="save"
          style={{ marginLeft: ".5rem", marginTop: ".5px" }}
          onClick={this.openModal}
        >
          {i18n.linelist.templates.saveModified}
        </Button>
        <SaveTemplateModal
          visible={this.state.visible}
          onClose={this.closeModal}
          {...this.props}
        />
      </React.Fragment>
    ) : null;
  }
}
