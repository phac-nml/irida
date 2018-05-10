import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Button, Checkbox, Form, Input, Modal } from "antd";
const FormItem = Form.Item;

const { i18n } = window.PAGE;

const validations = {
  valid: { status: "success", message: "" },
  validating: { status: "validating", message: "" },
  exists: {
    status: "warning",
    message: "This name exists"
  }
};

export class SaveTemplateModal extends React.Component {
  nameInput = React.createRef();

  constructor(props) {
    super(props);

    this.state = {
      overwrite: props.id !== null
    };
  }

  showModal = () => {
    this.setState({
      visible: true
    });
  };

  handleOk = e => {
    console.log(e);
    this.setState({
      visible: false
    });
  };

  handleCancel = e => {
    console.log(e);
    this.setState({
      visible: false,
      valid: false
    });
  };

  validateName = () => {
    const value = this.nameInput.current.input.value;
    this.setState(validations.validating);

    if (value === this.props.modified.name) {
      this.setState(validations.exists);
    } else {
      this.setState(validations.valid);
    }
  };

  handleOverwriteChange = e => {
    this.setState({ overwrite: e.target.checked });
  };

  render() {
    const { modified } = this.props;

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
          title={i18n.linelist.templates.saveModal.title}
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form layout="vertical">
            <FormItem
              label={i18n.linelist.templates.saveModal.name}
              hasFeedback
              validateStatus={this.state.status}
              help={this.state.message}
            >
              <Input
                onKeyUp={this.validateName}
                ref={this.nameInput}
                defaultValue={modified.name}
              />
            </FormItem>
            {modified.id !== null ? (
              <FormItem>
                <Checkbox
                  checked={this.state.overwrite}
                  onChange={this.handleOverwriteChange}
                >
                  Overwrite template
                </Checkbox>
              </FormItem>
            ) : null}
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
