import React, { Component } from "react";
import PropTypes from "prop-types";
import { Button } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { connect } from "react-redux";
import { exportToGalaxy } from "../../apis/galaxy/galaxy";

export class GalaxySubmissionComponent extends Component {
  static propTypes = {
    disabled: PropTypes.bool.isRequired
  };

  state = {
    submitting: false
  };

  submit = () =>
    this.setState(
      { submitting: true },
      exportToGalaxy(
        this.props.email,
        this.props.makepairedcollection,
        this.props.oauthCode,
        this.props.oauthRedirect,
        this.props.samples
      )
    );

  render() {
    return (
      <Button
        type="primary"
        disabled={this.props.disabled}
        loading={this.state.submitting}
        onClick={this.submit}
      >
        {getI18N("GalaxyFinalSubmission.submit")}
      </Button>
    );
  }
}

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
