import React, { useState } from "react";
import PropTypes from "prop-types";
import { Button } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { connect } from "react-redux";
import { exportToGalaxy } from "../../apis/galaxy/oauth";

/**
 * Component to actually send the samples to a Galaxy Client
 * @param {boolean} disabled - if the form is ready to be submitted.
 * @param {string} email - users galaxy email
 * @param {boolean} makepairedcollection
 * @param  {string} oauthCode
 * @param {string} oauthRedirect
 * @param {array} samples
 * @returns {*}
 */
export function GalaxySubmissionComponent({
  disabled,
  email,
  makepairedcollection,
  oauthCode,
  oauthRedirect,
  samples
}) {
  const [submitting, setSubmitting] = useState(false);

  const submit = () => {
    setSubmitting(true);
    exportToGalaxy(
      email,
      makepairedcollection,
      oauthCode,
      oauthRedirect,
      samples
    );
  };

  return (
    <Button
      type="primary"
      disabled={disabled}
      loading={submitting}
      onClick={submit}
    >
      {getI18N("GalaxyFinalSubmission.submit")}
    </Button>
  );
}

GalaxySubmissionComponent.propTypes = {
  disabled: PropTypes.bool.isRequired,
  email: PropTypes.string,
  makepairedcollection: PropTypes.bool,
  oauthCode: PropTypes.string,
  oauthRedirect: PropTypes.string,
  samples: PropTypes.array
};

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({
  disabled: !state.galaxyReducer.submittable,
  email: state.galaxyReducer.email,
  makepairedcollection: state.galaxyReducer.makepairedcollection,
  oauthCode: state.galaxyReducer.code,
  oauthRedirect: state.galaxyReducer.redirect,
  samples: state.galaxyReducer.samples
});

const mapDispatchToProps = dispatch => ({});

export const GalaxySubmission = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxySubmissionComponent);
