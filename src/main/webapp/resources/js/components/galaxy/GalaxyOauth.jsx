import React from "react";
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
class GalaxyOauthComponent extends React.Component {
  static propTypes = {};

  state = {
    authenticating: false
  };

  authenticate = () =>
    this.setState({ authenticating: true }, this.props.authenticateOauthClient);

  render() {
    return (
      <div>
        <div style={{ marginBottom: SPACE_SM }}>
          Galaxy Authentication Required
        </div>
        <Button
          block
          onClick={this.authenticate}
          loading={this.state.authenticating}
          disabled={this.state.authenticating}
        >
          {this.state.authenticating
            ? getI18N("GalaxyStepOauth.authenticating")
            : getI18N("GalaxyStepOauth.authenticate")}
        </Button>
      </div>
    );
  }
}

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
