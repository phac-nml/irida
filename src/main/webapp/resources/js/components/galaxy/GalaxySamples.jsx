import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Alert, Icon } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { SPACE_SM } from "../../styles/spacing";
import { actions } from "./reducer";
import PropTypes from "prop-types";

/**
 * Component to display the status of fetching samples in the proper Galaxy format.
 * @param {function} getGalaxySamples
 * @param {boolean} finished
 */
function GalaxySamplesComponent({ getGalaxySamples, finished }) {
  useEffect(() => {
    getGalaxySamples();
  }, []);

  return (
    <div style={{ marginBottom: SPACE_SM }}>
      {finished ? (
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

GalaxySamplesComponent.propTypes = {
  getGalaxySamples: PropTypes.func.isRequired,
  finished: PropTypes.bool.isRequired
};

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
