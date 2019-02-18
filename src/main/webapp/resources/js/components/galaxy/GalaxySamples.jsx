import React from "react";
import { connect } from "react-redux";
import { Alert, Icon } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { SPACE_SM } from "../../styles/spacing";

class GalaxySamplesComponent extends React.Component {
  render() {
    return (
      <div style={{ marginBottom: SPACE_SM }}>
        {this.props.finished ? (
          <Alert
            message={getI18N("GalaxySamples.ready")}
            type="success"
            showIcon
          />
        ) : (
          <Alert
            message={getI18N("GalaxySamples.processing")}
            icon={<Icon type="loading" />}
            showIcon
            type="info"
          />
        )}
      </div>
    );
  }
}

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({
  finished: typeof state.galaxyReducer.samples !== "undefined"
});

const mapDispatchToProps = dispatch => ({});

export const GalaxySamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxySamplesComponent);
