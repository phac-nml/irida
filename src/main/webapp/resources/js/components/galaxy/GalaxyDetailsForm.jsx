import React, { Component } from "react";
import { Button, Checkbox, Form, Input } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { getGalaxyDetails } from "../../apis/galaxy/galaxy";

class ExportToGalaxyFormComponent extends Component {
  state = {
    submitting: false
  };

  componentDidMount() {
    getGalaxyDetails().then(data =>
      this.props.form.setFields({
        email: {
          value: data.email
        }
      })
    );
  }

  setSamples = samples => this.setState({ samples });

  setOauth = oauth2 => this.setState({ oauth2 });

  handleGalaxySubmit = e => {
    e.preventDefault();
    this.props.handleSubmitToGalaxy(this.props.form.getFieldsValue())
  };

  render() {
    const { getFieldDecorator, getFieldError } = this.props.form;

    const emailError = getFieldError("email") || false;

    return (
      <Form
        layout="vertical"
        onSubmit={this.handleGalaxySubmit}
        hideRequiredMark
      >
        <Form.Item
          label={getI18N("ExportToGalaxyForm.email")}
          validateStatus={emailError ? "error" : ""}
          help={getI18N("ExportToGalaxyForm.email.help")}
          hasFeedback
        >
          {getFieldDecorator("email", {
            rules: [
              {
                required: true,
                message: getI18N("ExportToGalaxyForm.email.warning")
              }
            ]
          })(<Input />)}
        </Form.Item>
        <Form.Item
          help={getI18N(
            "ExportToGalaxyForm.makepairedcollection.help"
          )}
        >
          {getFieldDecorator("makepairedcollection", {
            valuePropName: "checked",
            initialValue: true
          })(<Checkbox>{getI18N("ExportToGalaxyForm.makepairedcollection")}</Checkbox>)}
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" disabled={emailError}>
            {getI18N("ExportToGalaxyForm.submit")}
          </Button>
        </Form.Item>
      </Form>
    );
  }
}

export const GalaxyDetailsForm = Form.create({ name: "galaxy_export" })(
  ExportToGalaxyFormComponent
);
