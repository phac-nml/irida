import React from "react";
import PropTypes from "prop-types";
import { Button } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { connect } from "react-redux";
import { actions } from "./reducer";

/**
 * Component to actually send the samples to a Galaxy Client
 * @param {boolean} disabled - if the form is ready to be submitted.
 * @param {boolean} submitted - if the form is being submitted
 * @param {string} email - users galaxy email
 * @param {boolean} makepairedcollection
 * @param {array} samples
 * @param {function} submitSamplesToGalaxy - send samples to galaxy through saga.
 * @returns {*}
 */
export function GalaxySubmissionComponent({
  disabled,
  submitted,
  email,
  makepairedcollection,
  samples,
  submitSamplesToGalaxy
}) {
  const submit = () => {
    submitSamplesToGalaxy(email, makepairedcollection, samples);
  };

  return (
    <Button
      type="primary"
      disabled={disabled}
      loading={submitted}
      onClick={submit}
    >
      {getI18N("GalaxyFinalSubmission.submit")}
    </Button>
  );
}

GalaxySubmissionComponent.propTypes = {
  disabled: PropTypes.bool.isRequired,
  submitted: PropTypes.bool.isRequired,
  email: PropTypes.string,
  makepairedcollection: PropTypes.bool,
  samples: PropTypes.array,
  submitSamplesToGalaxy: PropTypes.func.isRequired
};

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({
  disabled: !state.galaxyReducer.submittable,
  submitted: state.galaxyReducer.submitted,
  email: state.galaxyReducer.email,
  makepairedcollection: state.galaxyReducer.makepairedcollection,
  samples: state.galaxyReducer.samples
});

const mapDispatchToProps = dispatch => ({
  submitSamplesToGalaxy: (email, makepairedcollection, samples) =>
    dispatch(actions.submit(email, makepairedcollection, samples))
});

export const GalaxySubmission = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxySubmissionComponent);
