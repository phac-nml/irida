import React from "react";
import PropTypes from "prop-types";
import { Button, Popconfirm } from "antd";

const { i18n } = window.PAGE;

export class UpdateTemplateButton extends React.Component {
  constructor(props) {
    super(props);
  }

  saveTemplate = e => {
    e.stopPropagation();
    const { name, fields, id } = this.props.modified;
    this.props.saveTemplate(name, fields, id);
  };

  render() {
    return (
      <Popconfirm
        title={i18n.linelist.templates.update.title}
        onConfirm={this.saveTemplate}
        okText={i18n.linelist.templates.update.confirm}
        cancelText={i18n.linelist.templates.update.cancel}
      >
        <Button
          type="dashed"
          size="small"
          onClick={e => e.stopPropagation()}
        >
          {i18n.linelist.templates.update.button}
        </Button>
      </Popconfirm>
    );
  }
}

UpdateTemplateButton.propTypes = {
  modified: PropTypes.object.isRequired,
  saveTemplate: PropTypes.func.isRequired
};