import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AutoComplete, Button, Checkbox, Form, Modal } from "antd";
import { fromJS } from "immutable";

const { i18n } = window.PAGE;

/**
 * Custom footer for the SaveTemplateModal allowing the save button to
 * be disabled if the Template name is not valid.
 */
function Footer(props) {
  return (
    <div>
      <Button onClick={props.onCancel}>{i18n.form.btn.cancel}</Button>
      <Button
        className="t-modal-save-template-btn"
        type="primary"
        disabled={props.disabled}
        onClick={props.onClick}
      >
        {i18n.form.btn.save}
      </Button>
    </div>
  );
}

const defaultState = {
  name: "",
  status: "",
  message: "",
  existing: false,
  names: [],
  overwrite: false,
  valid: false
};

/**
 * Component to render a [antd Modal]{@link https://ant.design/components/modal/}
 * for save a new Metadata Template.
 */
export class SaveTemplateModal extends React.Component {
  state = defaultState;

  validations = [
    {
      type: "required",
      fn: name => name.length === 0,
      state: {
        existing: false,
        status: "error",
        message: i18n.linelist.templates.saveModal.required,
        valid: false,
        overwrite: false
      }
    },
    {
      type: "length",
      fn: name => name.length < 5,
      state: {
        existing: false,
        status: "error",
        message: i18n.linelist.templates.saveModal.length,
        valid: false,
        overwrite: false
      }
    },
    {
      type: "nameExists",
      fn: name => this.state.names.includes(name),
      state: {
        existing: true,
        status: "error",
        message: i18n.linelist.templates.saveModal.nameExists,
        valid: false,
        overwrite: false
      }
    },
    {
      type: "valid",
      fn: name => {
        const names = this.state._names.toJS();
        names.unshift(name);
        this.setState({ names });
        return true;
      },
      state: { status: "success", message: "", valid: true, existing: false }
    }
  ];

  constructor(props) {
    super(props);
  }

  static getDerivedStateFromProps(props, state) {
    if (props.templates.size > 0) {
      // Just get the template name for validation (don't want duplicate names)
      // and remove the first one as it is "All Fields".
      const names = [];
      props.templates.toJS().forEach(t => {
        if (t.id !== null) {
          names.push(t.name);
        }
      });
      state.names = names;
      state._names = fromJS(names);
    }
    return state;
  }

  saveTemplate = () => {
    const template = this.props.templates.get(this.props.current).toJS();
    const fields = template.modified.filter(t => !t.hide);
    const name = this.state.name;
    const overwrite = this.state.overwrite;
    let id = undefined;

    if (overwrite) {
      // Get the template to overwrite because we need its id.
      const t = this.props.templates.find(t => t.get("name") === name);
      id = t.get("id");
    }

    this.props.saveTemplate(name, fields, id);
    this.resetForm();
  };

  handleCancel = e => {
    this.resetForm();
  };

  resetForm = () => {
    this.props.onClose();
  };

  validateName = name => {
    name = name.trim();
    for (const validation of this.validations) {
      if (validation.fn(name)) {
        this.setState({ name, ...validation.state });
        break;
      }
    }
  };

  overwriteChange = e => {
    const checked = e.target.checked;
    this.setState({ valid: checked, overwrite: checked });
  };

  render() {
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
            onClick={this.saveTemplate}
          />
        }
      >
        <Form layout="vertical">
          <Form.Item
            hasFeedback
            label={i18n.linelist.templates.saveModal.name}
            validateStatus={this.state.status}
            help={this.state.message}
          >
            <AutoComplete
              dataSource={this.state.names}
              onChange={this.validateName}
            />
          </Form.Item>
          {this.state.existing ? (
            <Checkbox onChange={this.overwriteChange}>
              {i18n.linelist.templates.saveModal.overwrite}
            </Checkbox>
          ) : null}
        </Form>
      </Modal>
    );
  }
}

SaveTemplateModal.propTypes = {
  template: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  current: PropTypes.number.isRequired,
  templates: ImmutablePropTypes.list.isRequired
};
