import React, { Component } from "react";
import { Checkbox, Form, Input } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { connect } from "react-redux";
import { actions } from "./reducer";
import { validateEmail } from "../../utilities/validation-utilities";

/**
 * Component to display a form containing all required and user
 * modifiable fields.
 */
class ExportToGalaxyFormComponent extends Component {

  updateEmail = e => this.props.updateEmail(e.target.value);

  updatedMakePairedCollection = e => this.props.updateMakePairedCollection(e.target.checked);

  render() {
    const validEmail = validateEmail(this.props.email);

    return (
      <Form layout="vertical" hideRequiredMark>
        <Form.Item
          label={getI18N("ExportToGalaxyForm.email")}
          validateStatus={validEmail ? "success" : "error"}
          validateMessage="DSafsfd"
          help={getI18N("ExportToGalaxyForm.email.help")}
        >
          <Input
            onChange={this.updateEmail}
            value={this.props.email}
          />
        </Form.Item>
        <Form.Item
          help={getI18N("ExportToGalaxyForm.makepairedcollection.help")}
        >
          <Checkbox
            onChange={this.updatedMakePairedCollection}
            checked={this.props.makepairedcollection}
          >
            {getI18N("ExportToGalaxyForm.makepairedcollection")}
          </Checkbox>
        </Form.Item>
      </Form>
    );
  }
}

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({
  email: state.galaxyReducer.email,
  makepairedcollection: state.galaxyReducer.makepairedcollection
});

const mapDispatchToProps = dispatch => ({
  updateEmail: email => dispatch(actions.setEmail(email)),
  updateMakePairedCollection: value =>
    dispatch(actions.setMakePairedCollection(value))
});

export const GalaxyDetailsForm = connect(
  mapStateToProps,
  mapDispatchToProps
)(ExportToGalaxyFormComponent);
