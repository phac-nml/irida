import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Button, Checkbox, Form, Input, Modal } from "antd";
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
  exists: {
    status: "warning",
    message:
      "__ A template by this name already exists, saving will overwrite the existing template __",
    valid: true
  },
  length: {
    status: "error",
    message: i18n.linelist.templates.saveModal.length,
    valid: false
  },
  existsError: {
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
  state = { name: "", requiresOverwrite: false, overwrite: false };

  constructor(props) {
    super(props);

    // Just get the template name for validation (don't want duplicate names)
    // and remove the first one as it is "All Fields".
    this.names = props.templates
      .toJS()
      .map(t => t.name)
      .slice(1);
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
    this.props.onClose();
  };

  templateNameChange = () => {
    const name = this.inputRef.current.input.value;
    this.validateName(name);
  };

  validateName = name => {
    if (name.length === 0) {
      this.setState({ name, ...validations.required });
    } else if (name.length < 5) {
      this.setState({ name, ...validations.length });
    } else if (this.names.includes(name)) {
      this.setState({ name, ...validations.exists });
    } else {
      this.setState({ name, ...validations.valid });
    }
  };

  static getDerivedStateFromProps(props, state) {
    if (typeof props.template !== "undefined") {
      if (props.template.id > 0) {
        state.name = props.template.name;
        Object.assign(state, {
          requiresOverwrite: true,
          ...validations.exists
        });
      } else {
        state.name = "";
        Object.assign(state, {
          requiresOverwrite: false,
          ...validations.empty
        });
      }
    }
    return state;
  }

  render() {
    const { template } = this.props;
    if (typeof template === "undefined") {
      return null;
    }
    return (
      <Modal
        closable={false}
        title={i18n.linelist.templates.saveModal.title}
        visible={this.props.visible}
        destroyOnClose={true}
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
            <Input
              onChange={this.templateNameChange}
              ref={this.inputRef}
              defaultValue={this.state.name}
            />
          </FormItem>
          {this.state.requiresOverwrite ? (
            <FormItem>
              <Checkbox checked={this.state.overwrite}>
                Overwrite existing template?
              </Checkbox>
            </FormItem>
          ) : null}
        </Form>
      </Modal>
    );
  }
}

SaveTemplateModal.propTypes = {
  onClose: PropTypes.func.isRequired,
  current: PropTypes.number.isRequired,
  templates: ImmutablePropTypes.list.isRequired
};
