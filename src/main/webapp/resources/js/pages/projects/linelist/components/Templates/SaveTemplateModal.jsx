import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Button, Form, AutoComplete, Modal, Tag } from "antd";
const FormItem = Form.Item;
const { Option } = AutoComplete;

const { i18n } = window.PAGE;

const validations = {
  empty: { status: "", message: "" },
  valid: { status: "success", message: "" },
  required: {
    status: "error",
    message: i18n.linelist.templates.saveModal.required
  },
  exists: {
    status: "warning",
    message: i18n.linelist.templates.saveModal.exists
  }
};

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

export class SaveTemplateModal extends React.Component {
  constructor(props) {
    super(props);
    this.templates = props.templates.toJS().slice(1);

    this.state = {
      options: this.templates.map(t => this.createTemplateOption(t)),
      valid: false,
      ...validations.empty
    };
  }

  createTemplateOption = template => {
    return (
      <Option key={template.name}>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <strong>{template.name}</strong>
          <Tag>{template.fields.length}</Tag>
        </div>
      </Option>
    );
  };

  showModal = () => {
    this.setState({
      visible: true
    });
  };

  handleOk = e => {
    this.resetForm();
  };

  handleCancel = e => {
    console.log(e);
    this.resetForm();
  };

  resetForm = () => {
    this.setState({
      value: "",
      visible: false,
      ...validations.empty
    });
  };

  onSearch = value => {
    let message;
    let options;
    let valid = true;
    if (value.length) {
      const filtered = this.templates.filter(t =>
        t.name.toLowerCase().includes(value.toLowerCase().trim())
      );
      const exists = filtered.length === 1 && filtered[0].name === value.trim();
      if (exists) {
        filtered.shift();
      }

      options = [
        <Option key={value}>{value}</Option>,
        ...filtered.map(t => this.createTemplateOption(t))
      ];
      message = exists ? validations.exists : validations.valid;
    } else {
      options = this.templates.map(t => this.createTemplateOption(t));
      message = validations.required;
      valid = false;
    }

    this.setState({ value, valid, options, ...message });
  };

  onSelect = value => {
    const filtered = this.templates.filter(t =>
      t.name.toLowerCase().includes(value.toLowerCase())
    );
    if (filtered.length === 1 && filtered[0].name === value) {
      this.setState({ value, ...validations.exists, valid: true });
    } else {
      this.setState({ value, ...validations.valid, valid: true });
    }
  };

  render() {
    return this.props.modified !== null ? (
      <React.Fragment>
        <Button
          className="primary"
          icon="save"
          style={{ marginLeft: ".5rem", marginTop: ".5px" }}
          onClick={this.showModal}
        >
          {i18n.linelist.templates.saveModified}
        </Button>
        <Modal
          closable={false}
          title={i18n.linelist.templates.saveModal.title}
          visible={this.state.visible}
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
              <AutoComplete
                onSearch={this.onSearch}
                onSelect={this.onSelect}
                optionLabelProp="value"
                value={this.state.value}
              >
                {this.state.options}
              </AutoComplete>
            </FormItem>
          </Form>
        </Modal>
      </React.Fragment>
    ) : null;
  }
}

SaveTemplateModal.propTypes = {
  modified: PropTypes.object,
  templates: ImmutablePropTypes.list.isRequired
};
