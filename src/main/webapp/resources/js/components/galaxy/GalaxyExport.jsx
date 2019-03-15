import React, { useEffect } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { Card, Col, Row } from "antd";
import { GalaxyDetailsForm } from "./GalaxyDetailsForm";
import { GalaxySubmission } from "./GalaxySubmission";
import { GalaxySamples } from "./GalaxySamples";
import { GalaxySubmitError } from "./GalaxySubmitError";
import { actions } from "./reducer";

/**
 * Main UI component to render the export to galaxy component to the interface.
 * @param {function} getSamples
 * @returns {*}
 */
function GalaxyExportComponent({ getSamples }) {
  useEffect(() => {
    getSamples();
  }, []);

  return (
    <Row>
      <Col lg={{ span: 12, offset: 6 }} md={24}>
        <Card>
          <GalaxyDetailsForm />
          <GalaxySamples />
          <GalaxySubmitError />
          <GalaxySubmission />
        </Card>
      </Col>
    </Row>
  );
}

GalaxyExportComponent.propTypes = {
  getSamples: PropTypes.func.isRequired
};

const mapStateToProps = state => ({});
const mapDispatchToProps = dispatch => ({
  getSamples: () => dispatch(actions.getGalaxySamples())
});

export const GalaxyExport = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxyExportComponent);
