import React from "react";
import PropTypes from "prop-types";
import { Button, Popconfirm } from "antd";

const { i18n } = window.PAGE;

/**
 * Component to render an [antd Button]{@link https://ant.design/components/button/}
 * that will have updating the current Metadata Template.
 */
export class UpdateTemplateButton extends React.Component {
  constructor(props) {
    super(props);
  }

  saveTemplate = e => {
    e.stopPropagation();
    const { name, modified, id } = this.props.template;
    const fields = modified.filter(f => !f.hide);
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
        <Button type="dashed" size="small" onClick={e => e.stopPropagation()}>
          {i18n.linelist.templates.update.button}
        </Button>
      </Popconfirm>
    );
  }
}

UpdateTemplateButton.propTypes = {
  template: PropTypes.object.isRequired,
  saveTemplate: PropTypes.func.isRequired
};
