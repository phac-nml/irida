import React from "react";
import { connect } from "react-redux";
import { Alert, Icon } from "antd";
import { getI18N } from "../../utilities/i18n-utilties";
import { SPACE_SM } from "../../styles/spacing";
import PropTypes from "prop-types";

/**
 * Component to display the status of fetching samples in the proper Galaxy format.
 * @param {boolean} fetching
 */
function GalaxySamplesComponent({ fetching }) {
  return (
    <div style={{ marginBottom: SPACE_SM }}>
      {fetching ? (
        <Alert
          message={getI18N("GalaxySamples.processing")}
          icon={<Icon type="loading"/>}
          showIcon
          type="info"
        />
      ) : (
        <Alert
          message={getI18N("GalaxySamples.ready")}
          type="success"
          showIcon
        />
      )}
    </div>
  );
}

GalaxySamplesComponent.propTypes = {
  fetching: PropTypes.bool.isRequired
};

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({
  fetching: state.galaxyReducer.fetchingSamples
});

const mapDispatchToProps = dispatch => ({});

export const GalaxySamples = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxySamplesComponent);
