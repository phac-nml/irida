import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { AutoComplete, Button, Checkbox, Form, Modal } from "antd";
import { fromJS } from "immutable";

const { i18n } = window.PAGE;

/*
Internationalized messages for state of validation for the template name.
 */
const validations = {
  empty: {
    status: "",
    message: "",
    valid: false,
    existing: false,
    overwrite: false
  },
  valid: { status: "success", message: "", valid: true, existing: false },
  required: {
    existing: false,
    status: "error",
    message: i18n.linelist.templates.saveModal.required,
    valid: false,
    overwrite: false
  },
  length: {
    existing: false,
    status: "error",
    message: i18n.linelist.templates.saveModal.length,
    valid: false,
    overwrite: false
  },
  nameExists: {
    existing: true,
    status: "error",
    message: i18n.linelist.templates.saveModal.nameExists,
    valid: false,
    overwrite: false
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

const defaultState = {
  name: "",
  existing: false,
  names: [],
  ...validations.empty,
  overwrite: false
};

/**
 * Component to render a [antd Modal]{@link https://ant.design/components/modal/}
 * for save a new Metadata Template.
 */
export class SaveTemplateModal extends React.Component {
  state = defaultState;

  constructor(props) {
    super(props);
  }

  static getDerivedStateFromProps(props, state) {
    if (typeof props.template !== "undefined") {
      Object.assign(state, defaultState);
    }

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
      state._names = fromJS(names); // This is to be kept is a clean state.
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
    if (name.length === 0) {
      /*
      All templates require a name.
      Save button should be disabled.
       */
      this.setState({ name, ...validations.required });
    } else if (name.length < 5) {
      /*
      Template names should be at least 5 characters long.
      Save button should be disabled.
       */
      this.setState({ name, ...validations.length });
    } else if (this.state.names.includes(name)) {
      /*
      A template already has this name.  There cannot be 2 templates with the
      same name.
       */
      this.setState({ name, ...validations.nameExists }, () => console.log(this.state));
    } else {
      /*
      New name, add it to the top of a new  list (if it is not a new list
      it the list grows with every letter types === BAD)
       */
      const names = this.state._names.toJS();
      names.unshift(name);
      this.setState({ name, names, ...validations.valid });
    }
  };

  overwriteChange = e => {
    const checked = e.target.checked;
    this.setState({ valid: checked, overwrite: checked });
  };

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
