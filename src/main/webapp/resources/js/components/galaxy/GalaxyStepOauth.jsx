import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { GalaxyOauth } from "./GalaxyOauth";
import { actions } from "./reducer";
import { connect } from "react-redux";
import { SPACE_SM } from "../../styles/spacing";

export class GalaxyStepOauthComponent extends React.Component {
  static propTypes = {
    checkOauthStatus: PropTypes.func.isRequired
  };

  componentDidMount() {
    this.props.checkOauthStatus();
  }

  render() {
    return (
      <div style={{ marginBottom: SPACE_SM }}>
        {this.props.authorized ? (
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
}

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
