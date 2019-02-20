import React from "react";
import PropTypes from "prop-types";
import { Checkbox, Form, Input } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { connect } from "react-redux";
import { actions } from "./reducer";
import { validateEmail } from "../../utilities/validation-utilities";

/**
 * Component to display a form containing all required and user modifiable fields.
 * @param {string} email - email for galaxy
 * @param  {boolean} makepairedcollection - whether to organize data into a library
 * @param {function} updateEmail - handles updates to the email address
 * @param {function} updateMakePairedCollection - handles update to makepairedcollection
 * @returns {*}
 */
function ExportToGalaxyFormComponent({
  email,
  makepairedcollection,
  updateEmail,
  updateMakePairedCollection
}) {
  const emailModified = e => updateEmail(e.target.value);

  const makePairedCollectionModified = e =>
    updateMakePairedCollection(e.target.checked);

  const validEmail = validateEmail(email);

  return (
    <Form layout="vertical" hideRequiredMark>
      <Form.Item
        label={getI18N("ExportToGalaxyForm.email")}
        validateStatus={validEmail ? "success" : "error"}
        help={getI18N("ExportToGalaxyForm.email.help")}
      >
        <Input onChange={emailModified} value={email} />
      </Form.Item>
      <Form.Item help={getI18N("ExportToGalaxyForm.makepairedcollection.help")}>
        <Checkbox
          onChange={makePairedCollectionModified}
          checked={makepairedcollection}
        >
          {getI18N("ExportToGalaxyForm.makepairedcollection")}
        </Checkbox>
      </Form.Item>
    </Form>
  );
}

ExportToGalaxyFormComponent.propTypes = {
  email: PropTypes.string.isRequired,
  makepairedcollection: PropTypes.bool.isRequired,
  updateEmail: PropTypes.func.isRequired,
  updateMakePairedCollection: PropTypes.func.isRequired
};

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
