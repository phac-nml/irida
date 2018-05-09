import React from "react";
import PropTypes from "prop-types";
import { Alert, Button, Form, Input, Modal } from "antd";
const FormItem = Form.Item;

const { i18n } = window.PAGE;
export class SaveTemplateModal extends React.Component {
  state = { visible: false, validation: { validateStatus: "", message: "" } };
  nameInput = React.createRef();

  constructor(props) {
    super(props);
  }

  validateName = () => {};

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

  validateTemplateName = () => {
    const value = this.nameInput.current.input.value;
    if (value.length > 1) {
      this.props.validateTemplateName(value);
    }
  };

  componentWillReceiveProps(nextProps) {
    if (nextProps.validating) {
      this.setState({
        validation: {
          validateStatus: "validating",
          message: "_Validating template name_"
        }
      });
    }
  }

  render() {
    return this.props.modified ? (
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
          title="__SAVE_TEMPLATE__"
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form layout="vertical">
            <FormItem
              label="__Template Name__"
              hasFeedback
              validateStatus={this.state.validation.validateStatus}
              help={this.state.validation.message}
            >
              <Input onKeyUp={this.validateTemplateName} ref={this.nameInput} />
            </FormItem>
          </Form>
          <Alert
            type="info"
            message="__Only the column order and visibility will be saved__"
          />
        </Modal>
      </React.Fragment>
    ) : null;
  }
}

SaveTemplateModal.propTypes = {
  modified: PropTypes.bool.isRequired,
  validateTemplateName: PropTypes.func.isRequired
};
