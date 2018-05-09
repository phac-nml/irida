import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Button, Form, Input, Modal } from "antd";
import { InfoAlert } from "../../../../../components/alerts";
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
    const templates = this.props.templates.toJS();
    let name = "";
    if (this.props.modified !== null) {
      const template = templates.filter(
        t => t.id === this.props.modified.id
      )[0];
      name = typeof template === "undefined" ? "" : template.name;
    }
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
              validateStatus={this.state.validation.validateStatus}
              help={this.state.validation.message}
            >
              <Input
                onKeyUp={this.validateTemplateName}
                ref={this.nameInput}
                defaultValue={name}
              />
            </FormItem>
          </Form>
          <InfoAlert message={i18n.linelist.templates.saveModal.info} />
        </Modal>
      </React.Fragment>
    ) : null;
  }
}

SaveTemplateModal.propTypes = {
  modified: PropTypes.object,
  templates: ImmutablePropTypes.list.isRequired
};
