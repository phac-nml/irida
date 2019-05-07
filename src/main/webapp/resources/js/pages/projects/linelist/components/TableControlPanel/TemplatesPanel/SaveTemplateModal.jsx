import React from "react";
import isEqual from "lodash/isEqual";
import PropTypes from "prop-types";
import { Button, Checkbox, Form, Modal, Select } from "antd";

const { Item } = Form;
const { Option } = Select;
const { i18n } = window.PAGE;

/**
 * Utility method to sort array of strings case insensitively.
 * @param {string} a
 * @param {string} b
 * @returns {number}
 */
const sortNames = (a, b) => a.toLowerCase().localeCompare(b.toLowerCase());

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

/**
 * Component to render a modal for the user to save the current state of the
 * linelist as a new MetadataTemplate.
 */
export class SaveTemplateModal extends React.Component {
  static propTypes = {
    template: PropTypes.object,
    onClose: PropTypes.func.isRequired,
    current: PropTypes.number.isRequired,
    templates: PropTypes.array.isRequired
  };

  validations = [
    {
      type: "required",
      fn: name => name.length === 0,
      state: {
        existingTemplate: false,
        status: "error",
        message: i18n.linelist.templates.saveModal.required,
        valid: false,
        overwriteTemplate: false
      }
    },
    {
      type: "length",
      fn: name => name.length < 5,
      state: {
        status: "error",
        message: i18n.linelist.templates.saveModal.length,
        valid: false,
        existingTemplate: false,
        overwriteTemplate: false
      }
    },
    {
      type: "nameExists",
      fn: name => this._options.findIndex(o => o === name) > -1,
      state: {
        status: "error",
        message: i18n.linelist.templates.saveModal.nameExists,
        valid: false,
        existingTemplate: true,
        overwriteTemplate: false
      }
    },
    {
      type: "valid",
      fn: () => true,
      state: {
        status: "success",
        message: "",
        valid: true,
        existingTemplate: false
      }
    }
  ];

  constructor(props) {
    super(props);
    this._options = this.props.templates.map(t => t.name).sort(sortNames);

    this.state = {
      options: this._options,
      disabledLabel: this._options[0] // Name of the "all fields" option. Cannot save by that name.
    };
  }

  /**
   * React function immediately after updating occurs.  Used to check if an update
   * to the UI is required.
   * @param {object} prevProps
   */
  componentDidUpdate(prevProps) {
    const template = this.props.template;

    /*
    Check to see if there is a new template being used, the new template
    will require a refresh to the UI.
     */
    if (!isEqual(prevProps.template, template)) {
      const existingTemplate = template.id > -1;
      const value = existingTemplate ? template.name : undefined;
      this.setState({
        value,
        existingTemplate,
        overwriteTemplate: !existingTemplate,
        valid: !existingTemplate
      });
    }
  }

  /**
   * Handle hiding the current modal.
   */
  hideModal = () => {
    this.setState({
      visible: false
    });
  };

  /**
   * What to do when searching the templates.
   * @param {string} value - Value user searched in the select
   */
  onSearch = value => {
    let options = Array.from(this._options);
    if (value) {
      // Determine if the name is in the current list of templates
      if (!options.includes(value)) {
        options.unshift(value);
      }
    }

    // Validate the field to make sure that the template name is usable
    for (const validation of this.validations) {
      if (validation.fn(value)) {
        this.setState({ value, options, ...validation.state });
        break;
      }
    }
  };

  /**
   * How to filter each option in the select group
   * @param {string} inputValue - Value entered by user
   * @param {object} option - current select option to filter
   * @returns {boolean}
   */
  filterOption = (inputValue, option) => {
    return option.props.children
      .toLowerCase()
      .includes(inputValue.toLowerCase());
  };

  /**
   * Save the current template using the entered name.
   */
  saveTemplate = () => {
    const fields = this.props.template.modified.filter(t => !t.hide);
    const name = this.state.value;
    const overwrite = this.state.overwriteTemplate;
    let id = undefined;

    if (overwrite) {
      // Get the template to overwrite because we need its id.
      const t = this.props.templates.find(t => t.name === name);
      id = t.id;
    }

    this.props.saveTemplate(name, fields, id);
    this.props.onClose();
  };

  /**
   * Event handler for when a user selects to overwrite and existing template.
   * @param {object} e - Checkbox event
   */
  onExistingChange = e => {
    this.setState({
      valid: true,
      overwriteTemplate: true
    });
  };

  render() {
    const { existingTemplate, options, overwriteTemplate, value } = this.state;

    return (
      <Modal
        closable={false}
        title="Modal"
        visible={this.props.visible}
        footer={
          <Footer
            disabled={!this.state.valid}
            onCancel={this.props.onClose}
            onClick={this.saveTemplate}
          />
        }
      >
        <Form>
          <Item
            label={i18n.linelist.templates.saveModal.name}
            hasFeedback
            validateStatus={this.state.status}
            help={this.state.message}
          >
            <Select
              showSearch
              className="t-template-name"
              value={value}
              style={{ width: "100%" }}
              filterOption={this.filterOption}
              onSearch={this.onSearch}
              onChange={this.onSearch}
              onBlur={this.onSearch}
            >
              {options.map(template => (
                <Option
                  disabled={template === this.state.disabledLabel}
                  key={template}
                >
                  {template}
                </Option>
              ))}
            </Select>
          </Item>
          {existingTemplate ? (
            <Item>
              <Checkbox
                checked={overwriteTemplate}
                onChange={this.onExistingChange}
              >
                Overwrite Existing Template
              </Checkbox>
            </Item>
          ) : null}
        </Form>
      </Modal>
    );
  }
}
