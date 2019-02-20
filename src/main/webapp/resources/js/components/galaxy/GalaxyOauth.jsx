import React, { useState } from "react";
import PropTypes from "prop-types";
import { Button } from "antd";
import { actions } from "./reducer";
import { connect } from "react-redux";
import { getI18N } from "../../utilities/i18n-utilties";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Component to display the authentication status of the galaxy client.
 * If the client is not authorized, the user is presented a button which
 * will commence the Oauth2 process.
 */
function GalaxyOauthComponent({ authenticateOauthClient }) {
  const [authenticating, setAuthenticating] = useState(false);

  const authenticate = () => {
    setAuthenticating(true);
    authenticateOauthClient();
  };

  return (
    <div>
      <div style={{ marginBottom: SPACE_SM }}>
        {getI18N("GalaxyOauth.Authentication.Required")}
      </div>
      <Button
        block
        onClick={authenticate}
        loading={authenticating}
        disabled={authenticating}
        icon="lock"
      >
        {authenticating
          ? getI18N("GalaxyStepOauth.authenticating")
          : getI18N("GalaxyStepOauth.authenticate")}
      </Button>
    </div>
  );
}

GalaxyOauthComponent.propTypes = {
  authenticateOauthClient: PropTypes.func.isRequired
};

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({});

const mapDispatchToProps = dispatch => ({
  authenticateOauthClient: () => dispatch(actions.authenticateOauthClient())
});

export const GalaxyOauth = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxyOauthComponent);
