import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Button, Form, Input, Modal } from "antd";
const FormItem = Form.Item;

const { i18n } = window.PAGE;

/*
Internationalized messages for state of validation for the template name.
 */
const validations = {
  empty: { status: "", message: "", valid: false },
  valid: { status: "success", message: "", valid: true },
  required: {
    status: "error",
    message: i18n.linelist.templates.saveModal.required,
    valid: false
  },
  length: {
    status: "error",
    message: i18n.linelist.templates.saveModal.length,
    valid: false
  },
  exists: {
    status: "error",
    message: i18n.linelist.templates.saveModal.exists,
    valid: false
  }
};

/**
 * Custom footer for the SaveTemplateModal allowing the save button to
 * be disabled if the Template name is not valid.
 */
function Footer(props) {
  return (
    <div>
      <Button onClick={props.onCancel}>{i18n.form.btn.cancel}</Button>
      <Button type="primary" disabled={props.disabled} onClick={props.onClick}>
        {i18n.form.btn.save}
      </Button>
    </div>
  );
}

/**
 * Component to render a [antd Modal]{@link https://ant.design/components/modal/}
 * for save a new Metadata Template.
 */
export class SaveTemplateModal extends React.Component {
  inputRef = React.createRef();

  constructor(props) {
    super(props);

    // Just get the template name for validation (don't want duplicate names)
    // and remove the first one as it is "All Fields".
    this.names = props.templates
      .toJS()
      .map(t => t.name)
      .slice(1);

    this.state = {
      valid: false,
      ...validations.empty
    };
  }

  handleOk = () => {
    const template = this.props.templates.get(this.props.current).toJS();
    const fields = template.modified;
    const name = this.inputRef.current.input.value.trim();
    this.props.saveTemplate(name, fields, null);
    this.resetForm();
  };

  handleCancel = e => {
    this.resetForm();
  };

  resetForm = () => {
    this.inputRef.current.input.value = "";
    this.setState(validations.empty);
    this.props.onClose();
  };

  onKeyUp = () => {
    const value = this.inputRef.current.input.value.trim();
    if (value.length === 0) {
      this.setState(validations.required);
    } else if (this.names.includes(value)) {
      this.setState(validations.exists);
    } else if (value.length < 5) {
      this.setState(validations.length);
    } else {
      this.setState(validations.valid);
    }
  };

  render() {
    return (
      <Modal
        closable={false}
        title={i18n.linelist.templates.saveModal.title}
        visible={this.props.visible}
        footer={
          <Footer
            disabled={!this.state.valid}
            onCancel={this.handleCancel}
            onClick={this.handleOk}
          />
        }
      >
        <Form layout="vertical">
          <FormItem
            hasFeedback
            label={i18n.linelist.templates.saveModal.name}
            validateStatus={this.state.status}
            help={this.state.message}
          >
            <Input onKeyUp={this.onKeyUp} ref={this.inputRef} />
          </FormItem>
        </Form>
      </Modal>
    );
  }
}

SaveTemplateModal.propTypes = {
  onClose: PropTypes.func.isRequired,
  templates: ImmutablePropTypes.list.isRequired
};
