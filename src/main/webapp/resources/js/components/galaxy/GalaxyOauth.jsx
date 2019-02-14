import React from "react";
import PropTypes from "prop-types";
import { getGalaxyClientAuthentication } from "../../apis/galaxy/galaxy";
import { authenticateOauthClient } from "../../apis/oauth/oauth";
import { green6, red6 } from "../../styles/colors";
import { getI18N } from "../../utilities/i18n-utilties";
import { SubmitIcon, SubmitIconProcessing, SubmitStep } from "./components";

export class GalaxyOauth extends React.Component {
  static propTypes = {
    setOauth: PropTypes.func.isRequired
  };

  state = { processing: true, authorized: false };

  componentDidMount() {
    getGalaxyClientAuthentication(window.PAGE.galaxyClientID).then(data => {
      const authorized = data.isAuthorized;
      if (authorized) {
        this.setState({ authorized });
      } else {
        this.authenticate();
      }
    });
  }

  authenticate = () => {
    const redirect = `${window.TL.BASE_URL}galaxy/auth_code`;
    authenticateOauthClient(window.PAGE.galaxyClientID, redirect)
      .then(code =>
        this.setState({ authError: false }, () =>
          this.props.setOauth({
            code,
            redirect
          })
        )
      )
      .catch(() => {
        this.setState({ authError: true });
      });
  };

  render() {
    return this.state.authenticated ? (
      <SubmitStep>
        <SubmitIcon type="check-circle" theme="twoTone" twoToneColor={green6} />
        {getI18N("GalaxyOauth.authenticated")}
      </SubmitStep>
    ) : this.state.authError ? (
      <SubmitStep>
        <SubmitIcon
          type="exclamation-circle"
          theme="twoTone"
          twoToneColor={red6}
        />
        {getI18N("GalaxyOauth.error")}
      </SubmitStep>
    ) : (
      <SubmitStep>
        <SubmitIconProcessing type="loading" />
        {getI18N("GalaxyOauth.authenticating")}
      </SubmitStep>
    );
  }
}
