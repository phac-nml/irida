import React from "react";
import { connect } from "react-redux";
import { Alert, Icon } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { SPACE_SM } from "../../styles/spacing";
import { actions } from "./reducer";
import PropTypes from "prop-types";

/**
 * Component to
 */
class GalaxySamplesComponent extends React.Component {
  static propTypes = {
    getGalaxySamples: PropTypes.func.isRequired,
    finished: PropTypes.bool.isRequired
  };

  componentDidMount() {
    /*
    As soon as the page renders start asking for the samples to be formatted.
    This will get a list of link to the samples to be transferred to galaxy.
     */
    this.props.getGalaxySamples();
  }

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

const mapDispatchToProps = dispatch => ({
  getGalaxySamples: () => dispatch(actions.getGalaxySamples())
});

export const GalaxySamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxySamplesComponent);
