import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Alert } from "antd";
import { SPACE_SM } from "../../styles/spacing";
import { getI18N } from "../../utilities/i18n-utilties";

function GalaxySubmitErrorComponent({ errored }) {
  return (
    <div>
      {errored ? (
        <Alert
          style={{ marginBottom: SPACE_SM }}
          type="error"
          showIcon
          message={getI18N("GalaxySubmissionError.message")}
          description={getI18N("GalaxySubmissionError.description")}
        />
      ) : null}
    </div>
  );
}

GalaxySubmitErrorComponent.propTypes = {
  errored: PropTypes.bool.isRequired
};

const mapStateToProps = state => ({
  errored: state.galaxyReducer.errored
});

const mapDispatchToProps = dispatch => ({});

export const GalaxySubmitError = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxySubmitErrorComponent);
