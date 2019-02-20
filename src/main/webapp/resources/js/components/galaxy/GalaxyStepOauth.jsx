import React, { useEffect } from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { GalaxyOauth } from "./GalaxyOauth";
import { actions } from "./reducer";
import { connect } from "react-redux";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Component to display the status of the Galaxy Oauth2 token.
 * @param {boolean} authorized - true if the Galaxy Client is authorized
 * @param {function} checkOauthStatus - to check the status of the client.
 */
export function GalaxyStepOauthComponent({ authorized, checkOauthStatus }) {
  useEffect(() => {
    checkOauthStatus();
  }, []);

  return (
    <div style={{ marginBottom: SPACE_SM }}>
      {authorized ? (
        <Alert
          message={getI18N("GalaxyStepOauth.authenticated")}
          type="success"
          showIcon
        />
      ) : (
        <Alert message={<GalaxyOauth />} showIcon type="warning" />
      )}
    </div>
  );
}

GalaxyStepOauthComponent.propTypes = {
  authorized: PropTypes.bool,
  checkOauthStatus: PropTypes.func.isRequired
};

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({
  authorized: state.galaxyReducer.oauthAuthorized,
  error: state.galaxyReducer.oauthError
});

const mapDispatchToProps = dispatch => ({
  checkOauthStatus: () => dispatch(actions.checkOauthStatus())
});

export const GalaxyStepOauth = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxyStepOauthComponent);
